package com.abajar.gatling.xmpp

object XmppProtocolBuilder {
  def endpoint(address: String, port: Int) = XmppProtocolBuilder(address, port)
}

case class XmppProtocolBuilder(address: String, port: Int) {
  def build() = XmppProtocol(address, port)
  def boshPath(path: String) = XmppBoshProtocolBuilder(address, port, path) 
}

case class XmppBoshProtocolBuilder(address: String, port: Int, path: String) {
  def build() = new XmppBoshProtocol(address, port, path)
}
