package net.caoticode.synergy.server

import scala.collection.mutable.{ Set => MutableSet }
import akka.actor.{ Actor, ActorRef, ActorContext }
import net.caoticode.synergy.Channel2ClientProtocol._

trait PushChannel {
  this: Actor =>

  val pushSubscribers = MutableSet[(ActorRef, String)]()

  val pushReceive: PartialFunction[Any, Unit] = {

    case SubscribePush(routingTag) =>
      context.watch(sender)
      pushSubscribers.add((sender, routingTag))

    case UnsubscribePush(routingTag) =>
      pushSubscribers.remove((sender, routingTag))
  }

  def removePush(subscriber: ActorRef): Unit = {
    val toRemove = pushSubscribers.filter { case (ref, kind) => subscriber == ref }
    toRemove.foreach(pushSubscribers.remove(_))
  }
}