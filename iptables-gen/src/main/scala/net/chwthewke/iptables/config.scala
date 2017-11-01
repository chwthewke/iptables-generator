package net.chwthewke.iptables

import cats.Show
import cats.kernel.Monoid
import cats.kernel.Semigroup
import cats.implicits._
import com.typesafe.config.Config
import enumeratum.Enum
import enumeratum.EnumEntry
import enumeratum.EnumEntry.Hyphencase
import pureconfig._
import pureconfig.error.ConfigReaderFailure
import pureconfig.module.cats.syntax._
import pureconfig.module.enumeratum._
import scala.collection.immutable

case class PortRange( from: Int, to: Int )

sealed trait Protocol extends EnumEntry with Hyphencase
object Protocol extends Enum[Protocol] {
  case object Tcp extends Protocol

  override def values: immutable.IndexedSeq[Protocol] = findValues

  implicit val configReader: ConfigReader[Protocol] = enumeratumConfigConvert
}

sealed abstract class AllowRule( protocol: Protocol, origin: Option[String] )

case class AllowPort( protocol: Protocol, origin: Option[String], port: Int ) extends AllowRule( protocol, origin )
case class AllowPorts( protocol: Protocol, origin: Option[String], ports: PortRange )
    extends AllowRule( protocol, origin )

object AllowRule {
  implicit val allowRuleSemigroup: Semigroup[AllowRule] = ( x, y ) => y

  implicit val configReader: ConfigReader[AllowRule] =
    ConfigReader[AllowPort].orElse( ConfigReader[AllowPorts] )

}

case class IptablesConfig( iptables: Map[String, AllowRule] )

object IptablesConfig {
  implicit val monoid: Monoid[IptablesConfig] = new Monoid[IptablesConfig] {
    override def empty: IptablesConfig = IptablesConfig( Monoid.empty[Map[String, AllowRule]] )

    override def combine( x: IptablesConfig, y: IptablesConfig ): IptablesConfig =
      IptablesConfig( x.iptables.combine( y.iptables ) )
  }

}

object config {
  def fromConfig( cfg: Config ): ConfigParseResult =
    loadConfig[IptablesConfig]( cfg ).leftMap( _.toNonEmptyList ).toValidated
}

sealed trait Error

final case class PureconfigError( failure: ConfigReaderFailure ) extends Error
final case class IoError( cause: Throwable )                     extends Error

object Error {
  implicit val showError: Show[Error] = {
    case PureconfigError( crf ) => crf.toString // Meh
    case IoError( err )         => s"IO error: ${err.getMessage}"
  }
}
