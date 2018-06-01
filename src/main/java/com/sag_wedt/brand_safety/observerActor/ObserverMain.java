package com.sag_wedt.brand_safety.observerActor;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ObserverMain {
    public static void main(String[] args) {
        final String port = args.length > 0 ? args[0] : "0";
        final Config config =
                ConfigFactory.parseString(
                        "akka.remote.netty.tcp.port=" + port + "\n" +
                                "akka.remote.artery.canonical.port=" + port).
                        withFallback(ConfigFactory.parseString("akka.cluster.roles = [observer]")).
                        withFallback(ConfigFactory.load());

        ActorSystem system = ActorSystem.create("ClusterSystem", config);

        system.actorOf(Props.create(ObserverActor.class), "observer");
    }
}
