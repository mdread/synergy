package net.caoticode.synergy.server

import akka.actor.{ Actor, ActorRef }
import net.caoticode.synergy.Channel2ClientProtocol._
import scala.collection.mutable.{ Set => MutableSet }
import akka.actor.Terminated

class Channel extends Actor {

  val pushSubscribers = MutableSet[(ActorRef, String)]()
  val pullSubscribers = null

  def receive = {
    case SubscribePush(routingTag) =>
      context.watch(sender)
      pushSubscribers.add((sender, routingTag))

    case UnsubscribePush(routingTag) =>
      pushSubscribers.remove((sender, routingTag))

    case Publish(msg, routingTag) =>
      for ((ref, tag) <- pushSubscribers if tag == routingTag)
        ref ! msg

    case Terminated(subscriber) =>
      val toRemove = pushSubscribers.filter { case (ref, kind) => subscriber == ref }
      toRemove.foreach(pushSubscribers.remove(_))
  }

}