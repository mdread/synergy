package net.caoticode.synergy.server

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props

class SynergyServer {
  
  val config = ConfigFactory.load()
  val system = ActorSystem("SynergyServer", config.getConfig("SynergyServer").withFallback(config))
  
  val channelMaster = system.actorOf(Props[ChannelMaster], "channelmaster")
  
  def shutdown(): Unit = {
    system.shutdown()
  }
}