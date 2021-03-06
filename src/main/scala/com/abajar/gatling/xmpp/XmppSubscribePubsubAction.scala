package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation.Validation
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.{OK, KO, Status}
import io.gatling.core.result.writer.DataWriterClient
import org.jivesoftware.smackx.pubsub.PubSubManager 
import org.jivesoftware.smackx.pubsub.LeafNode
import org.jivesoftware.smackx.pubsub.ItemPublishEvent
import org.jivesoftware.smackx.pubsub.Item
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener
import java.io.StringWriter
import java.io.PrintWriter

import scala.concurrent.Future
import scala.util.{Success, Failure}

class XmppEventListener(session: Session) extends ItemEventListener[Item] {
  def handlePublishedItems(publishedEvent: ItemPublishEvent[Item]) {
    val ended = nowMillis
    val pattern = """.*>(\d+)</text>.*""".r
    val pattern(start) = publishedEvent.getItems.get(0).toXML(null)
    new DataWriterClient{}.writeRequestData(
      session,
      "message-received",
      start.toLong,
      ended,
      ended,
      ended,
      OK
    )
  }
}

class XmppSubscribePubsubAction(requestName: Expression[String], val next: ActorRef, nodeName: Expression[String]) extends Interruptable with Failable {
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

    def subscribe(session: Session, requestName: String, nodeName: String) {
      val start = nowMillis
      val pubsub = Future {
        val connection = session("connection").as[AbstractXMPPConnection]
        val pubsubMgr = PubSubManager.getInstance(connection)
        val node = pubsubMgr.getNode(nodeName).asInstanceOf[LeafNode]
        node.addItemEventListener(new XmppEventListener(session));
        node.subscribe(connection.getUser().toString())
      }

      pubsub.onComplete { 
        case Success(_) => {
          val end = nowMillis
          logResult(session, requestName, OK, start, end)
          next ! session
        }
        case Failure(e) => {
          logger.error(e.getMessage)

          val sw = new StringWriter
          e.printStackTrace(new PrintWriter(sw))
          logger.error(sw.toString)

          val end = nowMillis
          logResult(session, requestName, KO, start, end)
          next ! session 
        }
      }
    }

    for {
      requestName <- requestName(session)
      nodeName <- nodeName(session)
    } yield subscribe(session, requestName, nodeName)
  }
}
