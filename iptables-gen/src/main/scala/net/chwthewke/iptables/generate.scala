package net.chwthewke.iptables

object generate {

  import Protocol._

  def source( origin: Option[String] ): String =
    origin.fold( "" )( o => s" -s $o" )

  def allow( rule: AllowRule ): String = rule match {
    case AllowPort( Tcp, origin, port ) =>
      s"-A INPUT${source( origin )} -p tcp -m state --state NEW -m tcp --dport $port -j ACCEPT"
    case AllowPorts( Tcp, origin, PortRange( low, high ) ) =>
      s"-A INPUT${source( origin )} -p tcp -m state --state NEW -m tcp --dport $low:$high -j ACCEPT"
  }

  val inputPre: List[String] = List(
    "-A INPUT -i lo -j ACCEPT",
    "-A INPUT -d 127.0.0.0/8 ! -i lo -j REJECT --reject-with icmp-port-unreachable",
    "-A INPUT -p icmp -j ACCEPT",
    "-A INPUT -m state --state RELATED,ESTABLISHED -j ACCEPT"
  )

  val inputPost: List[String] = List(
    "-A INPUT -m limit --limit 5/min -j LOG --log-prefix \"iptables denied [INPUT]: \" --log-level 7",
    "-A INPUT -j REJECT --reject-with icmp-port-unreachable"
  )

  val output: List[String] = List(
    "-A OUTPUT -p tcp -m tcp --dport 25 -j REJECT --reject-with icmp-port-unreachable",
    "-A OUTPUT -j ACCEPT"
  )

  val header: List[String] = List(
    "*filter",
    ":INPUT DROP [0:0]",
    ":FORWARD DROP [0:0]",
    ":OUTPUT DROP [0:0]"
  )

  val footer: List[String] = List(
    "COMMIT"
  )

  def apply( iptablesConfig: IptablesConfig ): String =
    (
      header ++
        inputPre ++
        iptablesConfig.iptables.values.map( allow ) ++
        inputPost ++
        output ++
        footer
    ).mkString( "\n" )

}
