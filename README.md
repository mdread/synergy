Synergy
=======

Synergy is about cooperation and collaboration, on its core is similar to a message-queue system
but it does not aims to be a full-featured MQ, it's a lightweight and easy asynchronous library
to handle communication between components.

Introduction
------------

There is a _server_, and there is a _client_... the server manages __channels__, clients can create, join and delete
those channels, once a client joins a channel it can publish data to it and subscribe to receive
the latest news from the channel your interested on.

Channel subscriptions
---------------------

There are two kinds of channel subscriptions: __push-subscription__ and __pull-subscription__, there are both useful for
different use cases. 

In the first case, a _push_ subscriber receives messages without consuming it, allowing other push subscribers
to receive the message too. Think about this as some kind of broadcast message sended to all push-subscribers of the channel.

The second case can be used to distribute work to a bunch of workers, messages get consumed by 
the first "free" _pull_ subscriber, while it consumes the message and works on it, can not consume new messages. 
Is worth to notice that there is no polling in here, it's purely event driven.

The nice thing about delegating the decision of how to process and receive messages to subscribers instead of building separate
channels with those roles, is the fact that _push_ and _pull_ subscribers can be mixed in the same channel, allowing for
the creation of interesting workflows.

Tag-Routing
-----------

Think about a channel as a queue of heterogeneous messages, there can be all kinds of messages on it, and clients need some
way of filtering it and only receive the kind of messages they are interested in, this is when tag-routing comes into play.
A tag is just an identifier string associated with a subscriber and a published message, subscribers who define a tag-route
will only receive messages published with the same tag-route

Technologies
------------

Synergy is made in Scala with the excellent AKKA library, they really outperform when talking about asynchronous programming

Build and Install
-----------------

__TODO__

How to use
----------

__TODO__

Notice
------

This project is in its early stages, it is not even alpha and definitely is not production ready, YET...
