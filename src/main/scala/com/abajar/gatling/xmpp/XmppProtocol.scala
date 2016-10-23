package com.abajar.gatling.xmpp

import io.gatling.core.config.Protocol

class XmppProtocol(address: String, port: Int) extends Protocol {
}

object XmppProtocol {
  def apply(address: String, port: Int) = new XmppProtocol(address, port)
}
