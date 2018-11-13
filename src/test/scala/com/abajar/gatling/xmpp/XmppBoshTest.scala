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
  val pubsubNode = sys.env.getOrElse("pubsub", "/node/1")
  val xmppProtocol = xmpp.endpoint(host, 5280, domain).boshPath("/http-bind/")
  val scn = scenario("XmppBosh")
    .exec(xmpp("user").connect())
    .pause(0)
    .exec(xmpp("user").subscribe(pubsubNode))
    .exec(xmpp("user").disconnect())

  setUp(scn.inject(atOnceUsers(1))).protocols(xmppProtocol)
}
