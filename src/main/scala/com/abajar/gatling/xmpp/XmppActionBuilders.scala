package com.abajar.gatling.xmpp

import akka.actor.ActorDSL._
import akka.actor.ActorRef
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols
import io.gatling.core.session.Expression

class XmppConnectActionBuilder(requestName: Expression[String], username: Expression[String], password: Expression[String]) extends ActionBuilder {
  override def build(next: ActorRef, protocols: Protocols): ActorRef = actor(actorName("xmppConnect"))(new XmppConnectAction(
      requestName,
      username,
      password,
      next,
      protocols.getProtocol[XmppBoshProtocol].getOrElse(throw new UnsupportedOperationException("XmppBosh Protocol wasn't registered"))
    ))
}

class XmppDisconnectActionBuilder(requestName: Expression[String]) extends ActionBuilder {
  override def build(next: ActorRef, protocols: Protocols): ActorRef = actor(actorName("xmppDisconnect"))(new XmppDisconnectAction(requestName, next))
}

class XmppJoinMucActionBuilder(requestName: Expression[String], message: Expression[String]) extends ActionBuilder {
  override def build(next: ActorRef, protocols: Protocols): ActorRef = actor(actorName("xmppJoin"))(new XmppJoinMucAction(requestName, next, message))
}

class XmppSubscribePubsubActionBuilder(requestName: Expression[String], nodeName: Expression[String]) extends ActionBuilder {
  override def build(next: ActorRef, protocols: Protocols): ActorRef = actor(actorName("xmppSubscribe"))(new XmppSubscribePubsubAction(requestName, next, nodeName))
}

class XmppPublishPubsubActionBuilder(requestName: Expression[String], nodeName: Expression[String]) extends ActionBuilder {
  override def build(next: ActorRef, protocols: Protocols): ActorRef = actor(actorName("xmppPublish"))(new XmppPublishPubsubAction(requestName, next, nodeName))
}
