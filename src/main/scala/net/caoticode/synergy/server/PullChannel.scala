package net.caoticode.synergy.server

import scala.collection.mutable.{ Set => MutableSet }
import akka.actor.{ Actor, ActorRef, ActorContext }
import net.caoticode.synergy.Channel2ClientProtocol._

trait PullChannel {
  this: Actor =>

  val pullSubscribers = null

  val pullReceive: PartialFunction[Any, Unit] = {

    case SubscribePull(routingTag) =>
      context.watch(sender)
    // TODO: implement

    case UnsubscribePull(routingTag) =>
    // TODO: implement
  }

  def removePull(subscriber: ActorRef): Unit = {
    // TODO: implement
  }
}