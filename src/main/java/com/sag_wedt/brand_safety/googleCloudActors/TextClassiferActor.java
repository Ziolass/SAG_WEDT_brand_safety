package com.sag_wedt.brand_safety.googleCloudActors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class TextClassiferActor extends AbstractActor implements RestActor {

    static public Props props() {
        return Props.create(TextClassiferActor.class, TextClassiferActor::new);
    }

    TextClassiferActor() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .build();
    }

    @Override
    public int sendRestRequest() {
        return 0;
    }
}
