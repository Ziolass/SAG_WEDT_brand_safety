package com.sag_wedt.brand_safety.myAgents;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.japi.pf.ReceiveBuilder;
import com.sag_wedt.brand_safety.messages.CommonMessages;


public class MyParentActor extends AbstractActor {

    private Cluster cluster = Cluster.get(getContext().system());

    private final Class typeMessageClass;

    private MyActorAnswer answer;

    protected MyParentActor(Class typeMessageClass) {
        this.typeMessageClass = typeMessageClass;
        this.setAnswer(this::answerMessage);
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

    private ReceiveBuilder commonBuilder() {
        return receiveBuilder()
                .match(ClusterEvent.CurrentClusterState.class, state -> {
                    for (Member member : state.getMembers()) {
                        if (member.status().equals(MemberStatus.up())) {
                            register(member);
                        }
                    }
                })
                .match(ClusterEvent.MemberUp.class, mUp -> register(mUp.member()));
    }

    public void register(Member member) {}

    void setAnswer(MyActorAnswer answer) {
        this.answer = answer;
    }

    public void answerMessage(CommonMessages.MyMessage msg, Callable callback) {}
}
