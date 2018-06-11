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
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

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
        final FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
        final Timeout timeout = new Timeout(Duration.create(50, TimeUnit.SECONDS));
        final ExecutionContext ec = system.dispatcher();
        system.scheduler().schedule(interval, interval, () -> {
            System.out.println("SEND Message");
            ask(frontend,
                    new ClassifyWebPage("Text: The 2009 Richmond High School gang rape occurred on Saturday, October 24, 2009, in Richmond, a city on the northeast side of the San Francisco Bay in California, U.S., when a female student of Richmond High School was gang raped repeatedly by a group of young males in a courtyard on the school campus while a homecoming dance was being held in the gymnasium. Although seven people faced charges related to the rape, one was released after a preliminary hearing. Five of the remaining six faced life imprisonment, should the charges be upheld, and one faced a maximum of eight years in jail. All initially pleaded not guilty.\n" +
                            "\n" +
                            "The incident received national attention. As many as 20 witnesses are believed to have been aware of the attack, but for more than two hours no one notified the police.\n" +
                            "\n" +
                            "The trials for the six defendants began September 2012, with defendant Manuel Ortega pleading guilty to four felonies and sentenced the following month to 32 years in prison. Ari Morales was sentenced to 27 years in prison. Jose Montano and Marceles Peter were convicted of forcible rape acting in concert, a forcible act of sexual penetration while acting in concert, and forcible oral copulation in concert.\n"),
                    timeout).onSuccess(new OnSuccess<Object>() {
                public void onSuccess(Object result) {
                    System.out.println("Frontend get: " + result.toString());
                }
            }, ec);
        }, ec);
    }
}
