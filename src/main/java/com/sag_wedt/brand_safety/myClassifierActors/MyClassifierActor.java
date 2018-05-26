package com.sag_wedt.brand_safety.myClassifierActors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import com.sag_wedt.brand_safety.messages.CommonMessages;
import com.sag_wedt.brand_safety.messages.RespondMessages;

import java.util.ArrayList;

import static com.sag_wedt.brand_safety.messages.CommonMessages.TEXT_CLASSIFIER_ACTOR_REGISTRATION;

public class MyClassifierActor extends AbstractActor {

    Cluster cluster = Cluster.get(getContext().system());

    @Override
    public void preStart() {
        cluster.subscribe(self(), ClusterEvent.MemberUp.class);
    }

    @Override
    public void postStop() {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .build();
    }
}
