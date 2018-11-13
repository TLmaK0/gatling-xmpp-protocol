package com.abajar.gatling.xmpp

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

import scala.concurrent.duration._
import com.abajar.gatling.xmpp.Predef._

import org.jivesoftware.smack.SmackConfiguration
class XmppBoshTest extends Simulation {
  //SmackConfiguration.DEBUG = true;
  val host = sys.env.getOrElse("host", "localhost")
  val domain = sys.env.getOrElse("domain", "localhost.localhost")
  val port = sys.env.getOrElse("port", "5280")
  var username = sys.env.getOrElse("username", "")
  var password = sys.env.getOrElse("password", "")
  val pubsubNode = sys.env.getOrElse("pubsub", "/node/1")
  val xmppProtocol = xmpp.endpoint(host, port.toInt, domain).boshPath("/http-bind/")
  val subscribers = scenario("subscriber")
    .exec(xmpp("connect").connect("", "")) //anonymous
    .exec(xmpp("subscribe").subscribe(pubsubNode))
    .pause(30)
    .exec(xmpp("disconnect").disconnect())

  val publishers = scenario("publisher")
    .exec(xmpp("connect").connect(username, password))
    .repeat(12) {
      exec(xmpp("publish").publish(pubsubNode))
      .pause(10)
    }
    .exec(xmpp("disconnect").disconnect())

  setUp(
    subscribers.inject(rampUsers(100) over (50 seconds)),
    publishers.inject(atOnceUsers(1))
  ).protocols(xmppProtocol)
}
