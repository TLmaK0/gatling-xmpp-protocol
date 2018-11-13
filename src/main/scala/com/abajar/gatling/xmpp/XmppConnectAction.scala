package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation._
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.{OK, KO, Status}
import io.gatling.core.result.writer.DataWriterClient

import scala.concurrent.Future
import scala.util.{Success, Failure}

import java.io.IOException

import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.bosh.BOSHConfiguration
import org.jivesoftware.smack.bosh.XMPPBOSHConnection
import org.jivesoftware.smack.roster.Roster

import org.jxmpp.jid.impl.JidCreate;

class XmppConnectAction(requestName: Expression[String], username: Expression[String], password: Expression[String], val next: ActorRef, protocol: XmppProtocol) extends Interruptable with Failable {
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

    def connect(session: Session, requestName: String, username: String, password: String) {
      val start = nowMillis
      val connect = Future {
        protocol match {
            case boshProtocol: XmppBoshProtocol => {
              val conf = BOSHConfiguration.builder()
                  .setFile(boshProtocol.path)
                  .setHost(boshProtocol.address)
                  .setServiceName(JidCreate.domainBareFrom(boshProtocol.domain))
                  .setPort(boshProtocol.port)

              if (username == "") conf.performSaslAnonymousAuthentication()
              else conf.setUsernameAndPassword(username, password);

              val connection = new XMPPBOSHConnection(conf.build())
              Roster.getInstanceFor(connection).setRosterLoadedAtLogin(false);
              connection.connect()
              connection.login()
              connection
            }
            case _ => ???
          }
      }

      connect.onComplete { 
        case Success(connection) => {
          val end = nowMillis
          val updatedSession = session.set("connection", connection)
          logResult(updatedSession, requestName, OK, start, end)
          next ! updatedSession
        }
        case Failure(e) => {
          val end = nowMillis
          logger.error(e.getMessage)
          logResult(session, requestName, KO, start, end)
          next ! session
        }
      }
    }

    for {
      requestName <- requestName(session)
      username <- username(session)
      password <- password(session)
    } yield connect(session, requestName, username, password)
  }
}
