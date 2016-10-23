package com.abajar.gatling.xmpp

import io.gatling.core.config.Protocol

class XmppBoshProtocol(val address: String, val port: Int, val domain: String, val path: String) extends XmppProtocol(address, port, domain) {
}
