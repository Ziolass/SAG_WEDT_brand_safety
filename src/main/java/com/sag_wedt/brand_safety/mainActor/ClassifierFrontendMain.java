package com.sag_wedt.brand_safety.mainActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import akka.util.Timeout;
import com.sag_wedt.brand_safety.messages.CommonMessages.*;
import com.sag_wedt.brand_safety.messages.Messages.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static akka.pattern.Patterns.ask;

public class ClassifierFrontendMain {
    public static void main(String[] args) {
        // Override the configuration of the port when specified as program argument
        final String port = args.length > 0 ? args[0] : "0";
        final Config config =
                ConfigFactory.parseString(
                        "akka.remote.netty.tcp.port=" + port + "\n" +
                                "akka.remote.artery.canonical.port=" + port)
                        .withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]"))
                        .withFallback(ConfigFactory.load());

        ActorSystem system = ActorSystem.create("ClusterSystem", config);

        final ActorRef frontend = system.actorOf(
                Props.create(ClassifierFrontendActor.class), "frontend");
        final FiniteDuration interval = Duration.create(2, TimeUnit.MINUTES);
        final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
        final ExecutionContext ec = system.dispatcher();
        final AtomicInteger counter = new AtomicInteger();
        system.scheduler().schedule(interval, interval, () -> {
            ask(frontend,
                    new TestMessage("hello-" + counter.incrementAndGet()),
                    timeout).onSuccess(new OnSuccess<Object>() {
                public void onSuccess(Object result) {

                }
            }, ec);
        }, ec);
    }
}
