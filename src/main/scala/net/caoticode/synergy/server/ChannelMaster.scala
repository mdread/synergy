package net.caoticode.synergy.server

import akka.actor.{ Actor, ActorRef, Props }
import net.caoticode.synergy.ChannelMasterProtocol._
import scala.collection.mutable.{ Map => MutableMap }

class ChannelMaster extends Actor {
  

  val channels = MutableMap[String, ActorRef]()

  def receive = {
    case ChannelCreate(name) =>
      val ref = context.actorOf(Props[Channel], name)
      channels.put(name, ref)
      sender ! ChannelCreated(ref)

    case ChannelDelete(name) =>
      context.stop(channels(name)) // TODO handle in a better way... some sort of graceful shutdown

    case ChannelJoin(name) =>
      channels.get(name) match {
        case Some(ref) => sender ! ChannelJoinSuccess(ref)
        case None => sender ! ChannelJoinFail(new Exception(s"channel with name $name does not exists"))
      }

    case ChannelLeave(name) =>

    case ChannelExists(name) =>
      sender ! ChannelExistsResponse(channels.contains(name))
  }

}