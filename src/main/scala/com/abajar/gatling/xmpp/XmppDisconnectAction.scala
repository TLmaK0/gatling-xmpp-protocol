package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation.Validation
import io.gatling.core.session.Session

class XmppDisconnectAction(requestName: Expression[String], val next: ActorRef, protocol: XmppProtocol) extends Interruptable with Failable {
  //disconnect
  override def executeOrFail(session: Session): Validation[_] = {
    requestName(session)
  }
}
