package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation.Validation
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.{OK, KO, Status}
import io.gatling.core.result.writer.DataWriterClient
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smack.AbstractXMPPConnection

import scala.concurrent.Future
import scala.util.{Success, Failure}

class XmppJoinMucAction(requestName: Expression[String], val next: ActorRef, serviceName: Expression[String]) extends Interruptable with Failable {
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

    def join(session: Session, requestName: String, serviceName: String) {
      val start = nowMillis
      val join = Future {
        val connection = session("connection").as[AbstractXMPPConnection]
        val mucm = MultiUserChatManager.getInstanceFor(connection)
        mucm.getMultiUserChat(serviceName).join("test" + start)
      }

      val updatedSession = session.set("connection", null)

      join.onComplete { 
        case Success(connection) => {
          val end = nowMillis
          logResult(updatedSession, requestName, OK, start, end)
          next ! updatedSession
        }
        case Failure(e) => {
          logger.error(e.getMessage)
          val end = nowMillis
          logResult(updatedSession, requestName, KO, start, end)
          next ! updatedSession 
        }
      }
    }

    for {
      requestName <- requestName(session)
      serviceName <- serviceName(session)
    } yield join(session, requestName, serviceName)
  }
}
