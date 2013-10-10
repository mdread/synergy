package net.caoticode.synergy.client

import akka.actor.{ Actor, ActorRef, TypedActor, Props }
import net.caoticode.synergy.Channel2ClientProtocol._
import scala.collection.mutable.{ Map => MutableMap }
import akka.actor.Terminated

trait ChannelClient {
  def publish(message: Any, routingTag: String = ""): Unit
  def subscribePush[T: Manifest](routingTag: String = "")(handler: (T => Unit)): Unit
  def unsubscribePush(routingTag: String = ""): Unit
}

class ChannelClientImpl(channel: ActorRef) extends ChannelClient with TypedActor.Receiver {
  //import TypedActor.dispatcher

  val pushSubscriptions = MutableMap[String, ActorRef]()
  var shutdownInitiated = false
  
  def publish(message: Any, routingTag: String = ""): Unit = {
    channel ! Publish(message, routingTag)
  }

  def subscribePush[T: Manifest](routingTag: String = "")(handler: (T => Unit)): Unit = {
    val ref = TypedActor.context.actorOf(Props(new ChannelClientActor[T](handler)))
    TypedActor.context.watch(ref)
    
    channel.tell(SubscribePush(routingTag), ref)
    
    pushSubscriptions.put(routingTag, ref)
  }
  
  def unsubscribePush(routingTag: String = ""): Unit = {
    val ref = pushSubscriptions(routingTag)
    
    channel.tell(UnsubscribePush(routingTag), ref)
    pushSubscriptions.remove(routingTag)
  }
  
  def onReceive(message: Any, sender: ActorRef): Unit = {
    message match {
      case InitiateShutdown =>
        println("shutdown initiated")
        shutdownInitiated = true
        pushSubscriptions.foreach { case (routingTag, ref) => 
          channel.tell(UnsubscribePush(routingTag), ref)
        }
      case Terminated(ref) => 
        for(tag <- pushSubscriptions.filter(e => e._2 == ref).map(e => e._1))
          pushSubscriptions.remove(tag)
        
        println(pushSubscriptions.size + " remaining")
        if(shutdownInitiated && pushSubscriptions.isEmpty)
          TypedActor(TypedActor.context).poisonPill(TypedActor.self[ChannelClient])
    }
  }
}

class ChannelClientActor[T](handler: (T => Unit)) extends Actor {
  def receive = {
    case x => handler(x.asInstanceOf[T])
  }
}