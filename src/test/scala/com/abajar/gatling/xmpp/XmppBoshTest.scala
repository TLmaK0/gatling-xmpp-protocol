package com.abajar.gatling.xmpp

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

import scala.concurrent.duration._
import com.abajar.gatling.xmpp.Predef._

import org.jivesoftware.smack.SmackConfiguration
class XmppBoshTest extends Simulation {
  //SmackConfiguration.DEBUG = true;
  val xmppProtocol = xmpp.endpoint("127.0.0.1", 5280, "test.com").boshPath("/http-bind/")
  val scn = scenario("XmppBosh")
    .exec(xmpp("user").connect())
    .pause(10)
    .exec(xmpp("user").subscribe("/node"))
    .exec(xmpp("user").disconnect())

  setUp(scn.inject(atOnceUsers(1))).protocols(xmppProtocol)
}
