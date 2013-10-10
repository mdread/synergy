package net.caoticode.synergy.client

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import com.typesafe.config.{Config, ConfigFactory}
import akka.actor.{ActorSystem, TypedActor, TypedProps}
import akka.actor.ActorSelection.toScala
import akka.pattern.ask
import akka.util.Timeout
import net.caoticode.synergy.ChannelMasterProtocol.{ChannelCreate, ChannelCreated, ChannelDelete, ChannelJoinCreate, ChannelJoinSuccess}
import net.caoticode.synergy.Channel2ClientProtocol.InitiateShutdown
import akka.actor.ActorRef


class SynergyClient(serverHost: String, serverPort: Int) {
  val system = ActorSystem("SynergyClient", configuration())

  implicit val timeout = Timeout(5 seconds)

  val channelMaster = system.actorSelection(serverConnection(serverHost, serverPort))

  def createChannel(name: String): ChannelClient = {
    val future = channelMaster ? ChannelCreate(name)
    val channel = Await.result(future, timeout.duration).asInstanceOf[ChannelCreated].channel
    
    createChannelClient(channel)
  }

  def joinOrCreateChannel(name: String): ChannelClient = {
	val future = channelMaster ? ChannelJoinCreate(name)
    val channel = Await.result(future, timeout.duration).asInstanceOf[ChannelJoinSuccess].channel
    
    createChannelClient(channel)
  }

  def deleteChannel(name: String): Unit = channelMaster ! ChannelDelete(name)

  def shutdown(): Unit = system.shutdown()

  def leaveChannel(channel: ChannelClient): Unit = {
    TypedActor(system).getActorRefFor(channel) ! InitiateShutdown
  }
  
  private def configuration(): Config = {
    val config = ConfigFactory.load()
    config.getConfig("SynergyClient").withFallback(config)
  }
  
  def serverConnection(host: String, port: Int) = s"akka.tcp://SynergyServer@$host:$port/user/channelmaster"
  
  private def createChannelClient(channel: ActorRef): ChannelClient = {
    TypedActor(system).typedActorOf(
      TypedProps(classOf[ChannelClient], new ChannelClientImpl(channel))
    )
  }
}

