package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation.Validation
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.{OK, KO}
import io.gatling.core.result.writer.DataWriterClient
import org.jivesoftware.smack.AbstractXMPPConnection

class XmppDisconnectAction(requestName: Expression[String], val next: ActorRef, protocol: XmppProtocol) extends Interruptable with Failable {
  //disconnect
  override def executeOrFail(session: Session): Validation[_] = {
    def disconnect(session: Session, requestName: String) {
      val start = nowMillis
      val (end, status) = try {
        session("connection").as[AbstractXMPPConnection].disconnect()
        (nowMillis, OK)
      } catch {
        case e:Exception => {
          logger.error(e.getMessage())
          (nowMillis, KO)
        }
      }

      val updatedSession = session.set("connection", null)

      new DataWriterClient{}.writeRequestData(
        updatedSession,
        requestName,
        start,
        end,
        end,
        end,
        status
      )
      next ! updatedSession
    }

    for {
      requestName <- requestName(session)
    } yield disconnect(session, requestName)
  }
}
