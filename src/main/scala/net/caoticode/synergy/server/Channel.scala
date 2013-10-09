package net.caoticode.synergy.server

import akka.actor.{Actor, Terminated}
import net.caoticode.synergy.Channel2ClientProtocol._

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