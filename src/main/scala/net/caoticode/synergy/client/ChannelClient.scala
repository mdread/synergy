package net.caoticode.synergy.client

import akka.actor.{ Actor, ActorRef, TypedActor, Props }
import net.caoticode.synergy.Channel2ClientProtocol._
import scala.collection.mutable.{ Map => MutableMap }

trait ChannelClient {
  def publish(message: Any, routingTag: String = ""): Unit
  def subscribePush[T: Manifest](routingTag: String = "")(handler: (T => Unit)): Unit
  def unsubscribePush(routingTag: String = ""): Unit
  def close(): Unit
}

class ChannelClientImpl(channel: ActorRef) extends ChannelClient {
  //import TypedActor.dispatcher

  val pushSubscriptions = MutableMap[String, ActorRef]()
  
  def publish(message: Any, routingTag: String = ""): Unit = {
    channel ! Publish(message, routingTag)
  }

  def subscribePush[T: Manifest](routingTag: String = "")(handler: (T => Unit)): Unit = {
    val ref = TypedActor.context.actorOf(Props(new ChannelClientActor[T](handler)))
    channel.tell(SubscribePush(routingTag), ref)
    
    pushSubscriptions.put(routingTag, ref)
  }
  
  def unsubscribePush(routingTag: String = ""): Unit = {
    val ref = pushSubscriptions(routingTag)
    
    channel.tell(UnsubscribePush(routingTag), ref)
    TypedActor.context.stop(ref)
    pushSubscriptions.remove(routingTag)
  }
  
  def close(): Unit = {
    pushSubscriptions.foreach { case (routingTag, ref) => 
      channel.tell(UnsubscribePush(routingTag), ref)
      TypedActor.context.stop(ref)
    }
    
    pushSubscriptions.clear()
  }
}

class ChannelClientActor[T](handler: (T => Unit)) extends Actor {
  def receive = {
    case x => handler(x.asInstanceOf[T])
  }
}