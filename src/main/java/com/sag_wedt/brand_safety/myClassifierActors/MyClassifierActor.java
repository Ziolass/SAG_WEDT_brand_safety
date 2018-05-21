package com.sag_wedt.brand_safety.myClassifierActors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.ArrayList;

public class MyClassifierActor extends AbstractActor {

    static public Props props() {
        return Props.create(MyClassifierActor.class, () -> new MyClassifierActor());
    }

    private ArrayList<ActorRef> child = new ArrayList<>();
    private int counter;

    private MyClassifierActor() {}

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .build();
    }

    static public class AskMyClassifierActor {
        public final String webPageText;

        public AskMyClassifierActor(String webPageText) {
            this.webPageText = webPageText;
        }
    }
}
