package com.sag_wedt.brand_safety.observerActor;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.dispatch.OnSuccess;
import akka.util.Timeout;
import com.sag_wedt.brand_safety.messages.CommonMessages.TestMessage;
import com.sag_wedt.brand_safety.messages.RespondMessages.SuccessResponse;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static akka.pattern.Patterns.ask;
import static com.sag_wedt.brand_safety.messages.CommonMessages.TEXT_CLASSIFIER_ACTOR_REGISTRATION;


public class ObserverActor extends AbstractActor {

    Cluster cluster = Cluster.get(getContext().system());
    ObserverServlet observerServlet = new ObserverServlet();

    @Override
    public void preStart() {
        final FiniteDuration interval = Duration.create(10, TimeUnit.SECONDS);
        final ExecutionContext ec = cluster.system().dispatcher();
        final AtomicInteger counter = new AtomicInteger();
        cluster.system().scheduler().schedule(interval, interval, () -> refreshStats(), ec);
    }

    @Override
    public void postStop() {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.class, cE -> {
                })
                .build();
    }

    private void refreshStats() {
        Stream<Member> memebers = StreamSupport.stream(cluster.state().getMembers().spliterator(), false);
        long textClassifierActorsNum = memebers.filter(m -> m.hasRole("textClassifier")).count();
        Stream<Member> memebers2 = StreamSupport.stream(cluster.state().getMembers().spliterator(), false);
        long frontendActorsNum = memebers2.filter(m -> m.hasRole("frontend")).count();

        observerServlet.addStatistics(0, frontendActorsNum, textClassifierActorsNum);
    }
}
