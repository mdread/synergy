package net.caoticode.synergy

import akka.actor.{ActorSystem, Actor, ActorLogging, Props}
import akka.testkit.{TestKit, ImplicitSender, EventFilter}
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import com.typesafe.config.ConfigFactory
import akka.actor.actorRef2Scala
import net.caoticode.synergy.Channel2ClientProtocol.Publish
import net.caoticode.synergy.Channel2ClientProtocol.SubscribePush
import net.caoticode.synergy.ChannelMasterProtocol.ChannelCreate
import net.caoticode.synergy.ChannelMasterProtocol.ChannelCreated
import net.caoticode.synergy.ChannelMasterProtocol.ChannelExists
import net.caoticode.synergy.ChannelMasterProtocol.ChannelExistsResponse
import net.caoticode.synergy.ChannelMasterProtocol.ChannelJoin
import net.caoticode.synergy.ChannelMasterProtocol.ChannelJoinFail
import net.caoticode.synergy.ChannelMasterProtocol.ChannelJoinSuccess
import net.caoticode.synergy.server.ChannelMaster

object SynergyTestKit {
  class LoggerActor extends Actor with ActorLogging{
    def receive = {
      case x => 
        log.info(x.toString)
        //println(x.toString)
    }
  }
}

class SynergyTestKit(_system: ActorSystem) extends TestKit(_system) with WordSpec
  with ImplicitSender with MustMatchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("SynergyTestKit", ConfigFactory.parseString("""
  akka.loggers = ["akka.testkit.TestEventListener"]
  """)))

  import net.caoticode.synergy.server._
  import net.caoticode.synergy.ChannelMasterProtocol._
  import net.caoticode.synergy.Channel2ClientProtocol._
  import SynergyTestKit._
  
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
  
  val chm = system.actorOf(Props[ChannelMaster])
  
  "a channel" must {
	
    "be created" in {
      chm ! ChannelCreate("testchannel")
      
      expectMsgType[ChannelCreated]
    }
    
    "exist after creation" in {
      chm ! ChannelExists("testchannel")
      
      expectMsg(ChannelExistsResponse(true))
    }
    
    "not exist if has not been created" in {
      chm ! ChannelExists("idonotexist")
      
      expectMsg(ChannelExistsResponse(false))
    }
    
    "be joined" in {
      chm ! ChannelJoin("testchannel")
      
      expectMsgType[ChannelJoinSuccess]
    }
    
    "not be joined if it does not exists" in {
      chm ! ChannelJoin("idonotexist")
      
      expectMsgType[ChannelJoinFail]
    }
 
  }
  
  "the log subscriber" must {
    
    "log a message" in {
      chm ! ChannelCreate("testchannel2")
      
      val ChannelCreated(channel) = expectMsgType[ChannelCreated]
      
      val loggerActor = system.actorOf(Props[LoggerActor])
      
      channel.tell(SubscribePush(), loggerActor)
      
      EventFilter.info(message = "hello!", occurrences = 3) intercept {
    	  channel ! Publish("hello!")
    	  channel ! Publish("hello!")
    	  channel ! Publish("hello!")
      }
    }
    
    "only log messages published with the same route-tag" in {
      chm ! ChannelCreate("testchannel3")
      
      val ChannelCreated(channel) = expectMsgType[ChannelCreated]
      
      val loggerActor = system.actorOf(Props[LoggerActor])
      
      channel.tell(SubscribePush("log"), loggerActor)
      
      EventFilter.info(message = "hello!", occurrences = 1) intercept {
    	  channel ! Publish("hello!")
    	  channel ! Publish("hello!", "myroute")
    	  channel ! Publish("hello!", "log")
      }
    }
    
  }
  
}