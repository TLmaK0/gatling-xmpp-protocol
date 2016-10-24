package com.abajar.gatling.xmpp

import io.gatling.core.session.Expression

class Xmpp(requestName: Expression[String]) {
  def connect() = new XmppConnectActionBuilder(requestName)
  def disconnect() = new XmppDisconnectActionBuilder(requestName)
  def join(serviceName: Expression[String]) = new XmppJoinMucActionBuilder(requestName, serviceName)
  def subscribe(nodeName: Expression[String]) = new XmppSubscribePubsubActionBuilder(requestName, nodeName)
}
