# iptables generator

A quick and dirty tool to generate an iptables firewall ruleset from
a HOCON configuration file.

Apply with iptables-apply (frop iptables-persistent)

Default rules are baked in.

Can setup open listening ports.

```
iptables {
  https {
    protocol = "tcp"
    port = 443
  }
}
```

```
iptables {
  ftp-pasv {
    protocol = "tcp"
    origin = "192.168.1.0/24"
    ports {
      from = 20000
      to   = 20999
    }
  }
}
```
  
## TODO

* integrate docker chain
* udp
