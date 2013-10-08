package net.caoticode.synergy

import net.caoticode.synergy.server.SynergyServer
import net.caoticode.synergy.client.SynergyClient
import net.caoticode.synergy.client.ClientConfig
import net.caoticode.synergy.client.ClientConfig

object Synergy extends App {

//  val server = new SynergyServer()
//  val client = new SynergyClient()
  val client = new SynergyClient(Some(ClientConfig(myHost="192.168.18.24", myPort=2553, serverHost="192.168.18.46", serverPort=2552)))
  
  val channel = client.joinOrCreateChannel("test")
  
  channel.subscribeForPush[String]{ msg: String => 
    println("[] message recived: " + msg)
  }
  
  channel.subscribeForPush[String]("myroute"){ msg: String => 
    println("[myroute] message recived: " + msg)
  }
  
  channel.subscribeForPush[String]("otheroute"){ msg: String => 
    println("[otheroute] message recived: " + msg)
  }
  
  channel.publish("message 1")
  channel.publish("message 2", "myroute")
  channel.publish("message 3", "otheroute")
  channel.publish("message 4", "myroute")
  channel.publish("message 5", "myroute")
  
//  client.shutdown
//  server.shutdown
}
