package com.abajar.gatling.xmpp

import io.gatling.core.config.Protocol

class XmppProtocol(address: String, port: Int, domain: String) extends Protocol {
}

object XmppProtocol {
  def apply(address: String, port: Int, domain: String) = new XmppProtocol(address, port, domain)
}
