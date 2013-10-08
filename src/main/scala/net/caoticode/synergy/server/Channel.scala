package net.caoticode.synergy.server

import akka.actor.{ Actor, ActorRef }
import net.caoticode.synergy.Channel2ClientProtocol._
import scala.collection.mutable.{Set => MutableSet}
import akka.actor.Terminated

class Channel extends Actor {

  val pushSubscribers = MutableSet[(ActorRef, String)]()
  val pullSubscribers = null
  
  def receive = {
    case Subscribe(kind, routingTag) => kind match {
      case PushSubscription =>
        context.watch(sender)
        pushSubscribers.add((sender, routingTag))
        
      case PullSubscription =>
        
    }
    
    case Unsubscribe(kind, routingTag) => kind match {
      case PushSubscription =>
        pushSubscribers.remove((sender, routingTag))
        
      case PullSubscription =>
        
    }
    
    case Publish(msg, routingTag) =>
      for((ref, tag) <- pushSubscribers if tag == routingTag)
        ref ! msg
        
    case Terminated(subscriber) =>
      
  }
  
}