package net.caoticode.synergy.server

import com.typesafe.config.ConfigFactory

import akka.actor.{ActorSystem, Props}

class SynergyServer {
  
  val config = ConfigFactory.load()
  val system = ActorSystem("SynergyServer", config.getConfig("SynergyServer").withFallback(config))
  
  val channelMaster = system.actorOf(Props[ChannelMaster], "channelmaster")
  
  def shutdown(): Unit = {
    system.shutdown()
  }
}