package com.abajar.gatling.xmpp

import akka.actor.Actor
import akka.actor.Status
import io.gatling.core.akka.BaseActor
import io.gatling.core.util.TimeHelper._

import org.jivesoftware.smackx.pubsub.PubSubManager 
import org.jivesoftware.smackx.pubsub.LeafNode
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.bosh.XMPPBOSHConnection
import org.jivesoftware.smack.bosh.BOSHConfiguration

case class ConnectBOSH(host: String, port: Int, domain: String, path: String, nodeName: String)
case class Disconnect()
case class Subscribe(nodeName: String)
case class TimeSpent(start: Long, end: Long)
case class XmppClientException(exception: Exception, times: TimeSpent) extends RuntimeException

class XmppClient(username: String, password: String) extends BaseActor {
  var connection: AbstractXMPPConnection = null

  def this() = this(null, null)

  def receive = {
    case ConnectBOSH(host, port, domain, path, nodeName) => {
      val conf = BOSHConfiguration.builder()
          .setHost(host).setPort(port)
          .setServiceName(domain).setFile(path).build()
      connection = new XMPPBOSHConnection(conf)
      val start = nowMillis
      val response = try {
        connection.connect()
        connection.login()
        val pubsubMgr = new PubSubManager(connection)
        pubsubMgr.getNode(nodeName)
          .asInstanceOf[LeafNode].subscribe(connection.getUser())
        TimeSpent(start, nowMillis)
      } catch {
        case e:Exception => Status.Failure(XmppClientException(e, TimeSpent(start, nowMillis)))
      }
      sender() ! response
    }
    case Subscribe(nodeName) => {
      val start = nowMillis
      val response = try {
        val pubsubMgr = new PubSubManager(connection)
        pubsubMgr.getNode(nodeName)
          .asInstanceOf[LeafNode].subscribe(connection.getUser())
        TimeSpent(start, nowMillis)
      } catch {
        case e:Exception => Status.Failure(XmppClientException(e, TimeSpent(start, nowMillis)))
      }
      sender() ! response
    }
    case Disconnect => {
      val start = nowMillis
      
      val response = try {
        connection.disconnect()
        TimeSpent(start, nowMillis)
      } catch {
        case e:Exception => Status.Failure(XmppClientException(e, TimeSpent(start, nowMillis)))
      }

      sender() ! response
    }
  }
}
