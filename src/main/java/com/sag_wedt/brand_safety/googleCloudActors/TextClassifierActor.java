package com.sag_wedt.brand_safety.googleCloudActors;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import com.sag_wedt.brand_safety.messages.CommonMessages.*;
import com.sag_wedt.brand_safety.messages.RespondMessages.*;
import com.sag_wedt.brand_safety.utils.Classificator;


public class TextClassifierActor extends AbstractActor implements Classificator {

    Cluster cluster = Cluster.get(getContext().system());

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TestMessage.class, job -> {
                    sender().tell(new SuccessResponse(),
                            getContext().getParent());
                    getContext().system().stop(self());
                })
                .build();
    }

    @Override
    public SuccessResponse classify() {
        return null;
    }
}
