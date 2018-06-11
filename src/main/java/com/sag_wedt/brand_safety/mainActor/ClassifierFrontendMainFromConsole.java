package com.sag_wedt.brand_safety.mainActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import akka.util.Timeout;
import com.sag_wedt.brand_safety.messages.Messages.ClassifyWebPage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

public class ClassifierFrontendMainFromConsole {
    public static void main(String[] args) throws IOException {
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
        final Timeout timeout = new Timeout(Duration.create(50, TimeUnit.SECONDS));
        final ExecutionContext ec = system.dispatcher();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter text");
        String s = br.readLine();

        System.out.println("SEND Message");
        ask(frontend,
                new ClassifyWebPage(s),
                timeout).onSuccess(new OnSuccess<Object>() {
            public void onSuccess(Object result) {
                System.out.println("Frontend get: " + result.toString());
            }
        }, ec);

    }
}
