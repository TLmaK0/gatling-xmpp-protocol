package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation._
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.{OK, KO}
import io.gatling.core.result.writer.DataWriterClient


import java.io.IOException

import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.bosh.BOSHConfiguration
import org.jivesoftware.smack.bosh.XMPPBOSHConnection

class XmppConnectAction(requestName: Expression[String], val next: ActorRef, protocol: XmppProtocol) extends Interruptable with Failable {
  //connect
  override def executeOrFail(session: Session): Validation[_] = {
    def connect(session: Session, requestName: String) {
      val start = nowMillis
      val (connection, end, status) = try {
        protocol match {
          case boshProtocol: XmppBoshProtocol => {
            val conf = BOSHConfiguration.builder()
                .setFile(boshProtocol.path).setHost(boshProtocol.address).setServiceName(boshProtocol.domain).setPort(boshProtocol.port)
                .build()
            val connection = new XMPPBOSHConnection(conf)
            connection.connect()
            connection.login()
            (Some(connection), nowMillis, OK)
          }
          case _ => ???
        }
      } catch {
        case e:Exception => {
          logger.error(e.getMessage())
          (None, nowMillis, KO)
        }
      }

      val updatedSession = connection match {
        case Some(connection) => session.set("connection", connection)
        case _ => session
      }

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
    } yield connect(session, requestName)
  }
}
