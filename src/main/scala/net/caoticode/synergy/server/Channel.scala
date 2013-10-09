package net.caoticode.synergy.server

import akka.actor.{ Actor, ActorRef }
import net.caoticode.synergy.Channel2ClientProtocol._
import scala.collection.mutable.{ Set => MutableSet }
import akka.actor.Terminated

class Channel extends Actor with PushChannel with PullChannel {

  val channelReceive: PartialFunction[Any,Unit] = {
    case Publish(msg, routingTag) =>
      for ((ref, tag) <- pushSubscribers if tag == routingTag)
        ref ! msg

    case Terminated(subscriber) =>
      removePush(subscriber)
      removePull(subscriber)
  }
  
  def receive = channelReceive orElse pushReceive orElse pullReceive
  
}