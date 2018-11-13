package com.abajar.gatling.xmpp

import io.gatling.core.session.Expression

class Xmpp(requestName: Expression[String]) {
  def connect(username: Expression[String], password: Expression[String]) = new XmppConnectActionBuilder(requestName, username, password)
  def disconnect() = new XmppDisconnectActionBuilder(requestName)
  def join(serviceName: Expression[String]) = new XmppJoinMucActionBuilder(requestName, serviceName)
  def subscribe(nodeName: Expression[String]) = new XmppSubscribePubsubActionBuilder(requestName, nodeName)
  def publish(nodeName: Expression[String]) = new XmppPublishPubsubActionBuilder(requestName, nodeName)
}
