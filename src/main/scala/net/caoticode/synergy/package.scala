package net.caoticode

import akka.actor.ActorRef

package object synergy {
  object MasterWorkerProtocol {
    // Messages from Workers
    case class WorkerCreated(worker: ActorRef)
    case class WorkerRequestsWork(worker: ActorRef)
    case class WorkIsDone(worker: ActorRef)

    // Messages to Workers
    case class WorkToBeDone(work: Any)
    case object WorkIsReady
    case object NoWorkToBeDone
  }
  
  object CommonProtocol {
    case class SubscriberConnected(worker: ActorRef)
  }
  
  object ChannelMasterProtocol {
    // messages from client
    case class ChannelCreate(name: String)
    case class ChannelDelete(name: String)
    case class ChannelJoin(name: String)
    case class ChannelLeave(name: String)
    case class ChannelExists(name: String)
    case class ChannelJoinCreate(name: String)
    
    // messages to clients
    case class ChannelJoinSuccess(channel: ActorRef)
    case class ChannelJoinFail(exception: Exception)
    case class ChannelExistsResponse(exists: Boolean)
    case class ChannelCreated(channel: ActorRef)
  }
  
  object Channel2ClientProtocol {
    private val DefaultRoutingTag = ""
    
    case class SubscribePull(routingTag: String = DefaultRoutingTag)
    case class SubscribePush(routingTag: String = DefaultRoutingTag)
    case class UnsubscribePull(routingTag: String = DefaultRoutingTag)
    case class UnsubscribePush(routingTag: String = DefaultRoutingTag)
    case class Publish(message: Any, routingTag: String = DefaultRoutingTag)
    case object InitiateShutdown
  }
}