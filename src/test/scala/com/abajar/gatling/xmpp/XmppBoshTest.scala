package com.abajar.gatling.xmpp

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

import scala.concurrent.duration._
import com.abajar.gatling.xmpp.Predef._

class XmppBoshTest extends Simulation {
  val xmppProtocol = xmpp.endpoint("127.0.0.1", 5280).boshPath("/http-bind/")
  val scn = scenario("XmppBosh")
    .exec(xmpp("user").connect())
    .pause(1)
    .exec(xmpp("user").disconnect())

  setUp(scn.inject(atOnceUsers(5))).protocols(xmppProtocol)
}
