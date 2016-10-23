package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation.Validation
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.{OK, KO, Status}
import io.gatling.core.result.writer.DataWriterClient
import org.jivesoftware.smack.AbstractXMPPConnection

import scala.concurrent.Future
import scala.util.{Success, Failure}

class XmppDisconnectAction(requestName: Expression[String], val next: ActorRef, protocol: XmppProtocol) extends Interruptable with Failable {
  override def executeOrFail(session: Session): Validation[_] = {
    def logResult(session: Session, requestName: String, status: Status, started: Long, ended: Long) {
      new DataWriterClient{}.writeRequestData(
        session,
        requestName,
        started,
        ended,
        ended,
        ended,
        status
      )
    }

    def disconnect(session: Session, requestName: String) {
      val start = nowMillis
      val disconnect = Future {
        session("connection").as[AbstractXMPPConnection].disconnect()
      }

      val updatedSession = session.set("connection", null)

      disconnect.onComplete { 
        case Success(connection) => {
          val end = nowMillis
          val updatedSession = session.set("connection", null)
          logResult(updatedSession, requestName, OK, start, end)
          next ! updatedSession
        }
        case Failure(e) => {
          next ! session
        }
      }
    }

    for {
      requestName <- requestName(session)
    } yield disconnect(session, requestName)
  }
}
