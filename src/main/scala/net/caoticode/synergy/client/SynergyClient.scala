package net.caoticode.synergy.client

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import net.caoticode.synergy.ChannelMasterProtocol.{ ChannelCreate, ChannelCreated }
import akka.actor.ActorRef
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import akka.actor.Actor
import akka.actor.Props

class SynergyClient {

  val config = ConfigFactory.load()
  val clientConfig = config.getConfig("SynergyClient").withFallback(config)
  val system = ActorSystem("SynergyClient", clientConfig)

  implicit val timeout = Timeout(5 seconds)
  implicit val ex = system.dispatcher

  val channelMaster = system.actorSelection("akka.tcp://SynergyServer@127.0.0.1:2552/user/channelmaster")

  def createChannel(name: String): ChannelClient = {
    val res = (channelMaster ? ChannelCreate(name)).mapTo[ChannelCreated]
    
    new ChannelClient(res.map(_.channel), system)
  }

  def shutdown(): Unit = {
    system.shutdown()
  }
}

class ChannelClient(channelRef: Future[ActorRef], system: ActorSystem){
  import net.caoticode.synergy.Channel2ClientProtocol._
  
  implicit val ex = system.dispatcher
  
  def publish(message: Any, routingTag: String = ""): Unit = {
    channelRef.map { channel => channel ! Publish(message, routingTag)}
  }
  
  def subscribeForPush[T: Manifest](handler: (T => Unit)): Unit = {
	  subscribeForPush("", handler)
  }
  
  def subscribeForPush[T: Manifest](routingTag: String, handler: (T => Unit)): Unit = {
	  channelRef.map { channel => channel.tell(Subscribe(PushSubscription, routingTag), system.actorOf(Props(new PushSubscriberActor[T](handler))))}
  }
  
  // def subscribeForPull() = ???
  
  private class PushSubscriberActor[T](handler: (T => Unit)) extends Actor {
    def receive = {
      case x => handler(x.asInstanceOf[T])
    }
  }
}