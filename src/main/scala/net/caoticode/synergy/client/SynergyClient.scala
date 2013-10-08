package net.caoticode.synergy.client

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import net.caoticode.synergy.ChannelMasterProtocol._
import akka.actor.ActorRef
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import akka.actor.Actor
import akka.actor.Props

case class ClientConfig(serverHost: String, serverPort: Int, myHost: String, myPort: Int)

class SynergyClient(conf: Option[ClientConfig] = None) {

  val referenceConfig = ConfigFactory.load()
  val providedConfig = conf.map { config =>
    ConfigFactory.parseString(s"""
      akka.remote.netty.tcp.hostname="${config.myHost}"
      akka.remote.netty.tcp.port=${config.myPort}
      serverConnection = "akka.tcp://SynergyServer@${config.serverHost}:${config.serverPort}/user/channelmaster"
     """)
  }.getOrElse(ConfigFactory.empty())

  val clientConfig = providedConfig.withFallback(referenceConfig.getConfig("SynergyClient")).withFallback(referenceConfig)
  val system = ActorSystem("SynergyClient", clientConfig)

  implicit val timeout = Timeout(5 seconds)
  implicit val ex = system.dispatcher

  val channelMaster = system.actorSelection(clientConfig.getString("serverConnection"))

  def createChannel(name: String): ChannelClient = {
    val res = (channelMaster ? ChannelCreate(name)).mapTo[ChannelCreated]

    new ChannelClient(res.map(_.channel), system)
  }
  
  def joinOrCreateChannel(name: String): ChannelClient = {
    val res = (channelMaster ? ChannelJoinCreate(name)).mapTo[ChannelJoinSuccess]

    new ChannelClient(res.map(_.channel), system)
  }
  
  def deleteChannel(name: String): Unit = {
    channelMaster ! ChannelDelete(name)
  }

  def shutdown(): Unit = {
    system.shutdown()
  }
}

class ChannelClient(channelRef: Future[ActorRef], system: ActorSystem) {
  import net.caoticode.synergy.Channel2ClientProtocol._

  implicit val ex = system.dispatcher

  def publish(message: Any, routingTag: String = ""): Unit = {
    channelRef.map { channel => channel ! Publish(message, routingTag) }
  }

  def subscribeForPush[T: Manifest](handler: (T => Unit)): Unit = {
    subscribeForPush("")(handler)
  }

  def subscribeForPush[T: Manifest](routingTag: String)(handler: (T => Unit)): Unit = {
    channelRef.map { channel => channel.tell(Subscribe(PushSubscription, routingTag), system.actorOf(Props(new PushSubscriberActor[T](handler)))) }
  }

  // def subscribeForPull() = ???

  private class PushSubscriberActor[T](handler: (T => Unit)) extends Actor {
    def receive = {
      case x => handler(x.asInstanceOf[T])
    }
  }
}