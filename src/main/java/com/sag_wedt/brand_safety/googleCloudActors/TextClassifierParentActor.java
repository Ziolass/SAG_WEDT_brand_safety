package com.sag_wedt.brand_safety.googleCloudActors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import com.sag_wedt.brand_safety.messages.CommonMessages.TestMessage;
import com.sag_wedt.brand_safety.messages.RespondMessages.SuccessResponse;

import static com.sag_wedt.brand_safety.messages.CommonMessages.TEXT_CLASSIFIER_ACTOR_REGISTRATION;


public class TextClassifierParentActor extends AbstractActor {

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
                .match(TestMessage.class, msg -> {
                    ActorRef newAgent = getContext().system().actorOf(Props.create(TextClassifierActor.class));
                    newAgent.tell(msg, sender());
                })
                .match(ClusterEvent.CurrentClusterState.class, state -> {
                    for (Member member : state.getMembers()) {
                        if (member.status().equals(MemberStatus.up())) {
                            register(member);
                        }
                    }
                })
                .match(ClusterEvent.MemberUp.class, mUp -> {
                    register(mUp.member());
                })
                .build();
    }

    void register(Member member) {
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    TEXT_CLASSIFIER_ACTOR_REGISTRATION, self());
    }
}
