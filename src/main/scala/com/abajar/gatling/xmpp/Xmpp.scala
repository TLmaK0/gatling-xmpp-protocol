package com.abajar.gatling.xmpp

import io.gatling.core.session.Expression

class Xmpp(requestName: Expression[String]) {
  def connect() = new XmppConnectActionBuilder(requestName)
  def disconnect() = new XmppDisconnectActionBuilder(requestName)
}
