package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation._
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.OK
import io.gatling.core.result.writer.DataWriterClient

class XmppConnectAction(requestName: Expression[String], val next: ActorRef, protocol: XmppProtocol) extends Interruptable with Failable {
  //connect
  override def executeOrFail(session: Session): Validation[_] = {

    def connect(session: Session, requestName: String) {
      new DataWriterClient{}.writeRequestData(
        session,
        requestName,
        nowMillis,
        nowMillis,
        nowMillis,
        nowMillis,
        OK,
        None
      )
      next ! session
    }

    for {
      requestName <- requestName(session)
    } yield connect(session, requestName)
  }
}
