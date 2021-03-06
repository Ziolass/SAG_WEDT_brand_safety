package com.sag_wedt.brand_safety.observerActor;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Spliterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;


public class ObserverActor extends AbstractActor {

    private Cluster cluster = Cluster.get(getContext().getSystem());

    @Override
    public void preStart() {
        final FiniteDuration interval = Duration.create(1, TimeUnit.SECONDS);
        final ExecutionContext ec = cluster.system().dispatcher();
        final AtomicInteger counter = new AtomicInteger();
        cluster.system().scheduler().schedule(interval, interval, this::refreshStats, ec);
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
        Spliterator<Member> members = cluster.state().getMembers().spliterator();
        AtomicLong textOpinionClassifierActorsNum = new AtomicLong(0);
        AtomicLong textSentimentClassifierActorsNum = new AtomicLong(0);
        AtomicLong frontendActorsNum = new AtomicLong(0);
        members.forEachRemaining(m -> {
            if(m.hasRole("opinionAnalysis")) {
                textOpinionClassifierActorsNum.getAndIncrement();
            }
            if(m.hasRole("sentimentAnalysis")) {
                textSentimentClassifierActorsNum.getAndIncrement();
            }
            if(m.hasRole("frontend")) {
                frontendActorsNum.getAndIncrement();
            }
        });

        Long all = StreamSupport.stream(cluster.state().getMembers().spliterator(), false).count();

        System.out.println("Nodes: " + Math.max(all-1,0));
        System.out.println("FrontendActorsNodes " + frontendActorsNum);
        System.out.println("TextOpinionClassifierActorsNodes: " + textOpinionClassifierActorsNum);
        System.out.println("TextSentimentClassifierActorsNodes: " + textSentimentClassifierActorsNum);
    }
}
