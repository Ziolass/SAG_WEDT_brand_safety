package com.sag_wedt.brand_safety.mainActor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import com.sag_wedt.brand_safety.messages.CommonMessages.*;
import com.sag_wedt.brand_safety.messages.Messages.*;
import com.sag_wedt.brand_safety.messages.RespondMessages.*;

import java.util.ArrayList;
import java.util.List;

import static com.sag_wedt.brand_safety.messages.CommonMessages.TEXT_CLASSIFIER_ACTOR_REGISTRATION;


public class ClassifierFrontendActor extends AbstractActor {

    List<ActorRef> textClassifierList = new ArrayList<>();
    int textClassifierCounter = 0;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TestMessage.class, job -> textClassifierList.isEmpty(), job -> {
                    sender().tell(new FailureResponse(),
                            sender());
                })
                .match(TestMessage.class, job -> {
                    textClassifierCounter++;
                    textClassifierList.get(textClassifierCounter % textClassifierList.size())
                            .forward(job, getContext());
                })
                .matchEquals(TEXT_CLASSIFIER_ACTOR_REGISTRATION, message -> {
                    getContext().watch(sender());
                    textClassifierList.add(sender());
                })
                .match(Terminated.class, terminated -> {
                    textClassifierList.remove(terminated.getActor());
                })
                .build();
    }
}
