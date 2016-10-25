package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation.Validation
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.{OK, KO, Status}
import io.gatling.core.result.writer.DataWriterClient

import scala.concurrent.Future
import scala.util.{Success, Failure}

import scala.concurrent.duration._
import akka.util.Timeout

class XmppDisconnectAction(requestName: Expression[String], val next: ActorRef) extends Interruptable with Failable {
  implicit val timeout = Timeout(5 seconds)

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
      val disconnect = session("client").as[ActorRef] ? Disconnect

      disconnect.onComplete { 
        case Success(TimeSpent(start, end)) => {
          logResult(session, requestName, OK, start, end)
          next ! session
        }
        case Failure(XmppClientException(e, TimeSpent(start, end))) => {
          logger.error(e.getMessage)
          logResult(session, requestName, KO, start, end)
          next ! session 
        }
        case _ => ???
      }
    }

    for {
      requestName <- requestName(session)
    } yield disconnect(session, requestName)
  }
}
