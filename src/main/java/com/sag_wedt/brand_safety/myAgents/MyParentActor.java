package com.sag_wedt.brand_safety.myAgents;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.japi.pf.ReceiveBuilder;


public class MyParentActor extends AbstractActor {

    Cluster cluster = Cluster.get(getContext().system());
    
    final Class typeMessageClass;

    MyActorAnswer answer;

    protected MyParentActor(Class typeMessageClass, MyActorAnswer answer) {
        this.typeMessageClass = typeMessageClass;
        this.answer = answer;
    }

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
        return commonBuilder()
                .match(typeMessageClass, msg -> {
                    ActorRef newAgent = getContext().system().actorOf(MyActor.props(this.typeMessageClass, this.answer));
                    newAgent.tell(msg, sender());
                })
                .build();
    }

    public final ReceiveBuilder commonBuilder() {
        return receiveBuilder()
                .match(ClusterEvent.CurrentClusterState.class, state -> {
                    for (Member member : state.getMembers()) {
                        if (member.status().equals(MemberStatus.up())) {
                            register(member);
                        }
                    }
                })
                .match(ClusterEvent.MemberUp.class, mUp -> {
                    register(mUp.member());
                });
    }

    public void register(Member member) {}

    public void setAnswer(MyActorAnswer answer) {
        this.answer = answer;
    }
}
