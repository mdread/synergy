package net.caoticode.synergy.client

import akka.actor.ActorSystem

class SynergyClient {

  type SubscriberHandler = (Any => Unit)
  
  val system = ActorSystem("SynergyClient")

  def subscribeForPush(routingTag: String, handler: (Any => Unit)): Unit = {
    
  }
  
  def subscribeForPull() = ???
}