package com.abajar.gatling.xmpp

object XmppProtocolBuilder {
  def endpoint(address: String, port: Int, domain: String) = XmppProtocolBuilder(address, port, domain)
}

case class XmppProtocolBuilder(address: String, port: Int, domain: String) {
  def build() = XmppProtocol(address, port, domain)
  def boshPath(path: String) = XmppBoshProtocolBuilder(address, port, domain, path) 
}

case class XmppBoshProtocolBuilder(address: String, port: Int, domain: String, path: String) {
  def build() = new XmppBoshProtocol(address, port, domain, path)
}
