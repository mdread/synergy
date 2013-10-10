package net.caoticode.synergy

import net.caoticode.synergy.server.SynergyServer
import net.caoticode.synergy.client.SynergyClient

object Synergy extends App {

  val server = new SynergyServer()
//  val client = new SynergyClient()
  val client = new SynergyClient("192.168.18.46", 2552)
  
  val channel = client.joinOrCreateChannel("test")
  
  channel.subscribePush[String](){ msg: String => 
    println("[] message recived: " + msg)
  }
  
  channel.subscribePush[String]("myroute"){ msg: String => 
    println("[myroute] message recived: " + msg)
  }
  
  channel.subscribePush[String]("otheroute"){ msg: String => 
    println("[otheroute] message recived: " + msg)
  }
  
  channel.publish("message 1")
  channel.publish("message 2", "myroute")
  channel.publish("message 3", "otheroute")
  channel.publish("message 4", "myroute")
  channel.publish("message 5", "myroute")
  channel.publish("message 6")
  
  //channel.unsubscribePush("myroute")
  client.leaveChannel(channel)
  
  channel.publish("message to nonexistent route 1", "myroute")
  channel.publish("message to nonexistent route 2", "myroute")
  channel.publish("message 7")
  
//  client.shutdown
//  server.shutdown
}
