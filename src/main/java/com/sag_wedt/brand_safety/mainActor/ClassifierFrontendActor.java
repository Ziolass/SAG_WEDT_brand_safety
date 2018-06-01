package com.sag_wedt.brand_safety.mainActor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.sag_wedt.brand_safety.messages.CommonMessages.*;
import com.sag_wedt.brand_safety.messages.RespondMessages.*;

import java.util.ArrayList;
import java.util.List;

import static com.sag_wedt.brand_safety.messages.CommonMessages.OPINION_ANALYSIS_ACTOR_REGISTRATION;


public class ClassifierFrontendActor extends AbstractActor {

    List<ActorRef> opinionAnalysisClassifierActorList = new ArrayList<>();
    int opinionAnalysisClassifierActorCounter = 0;
    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TestMessage.class, msg -> opinionAnalysisClassifierActorList.isEmpty(), msg -> {
                    sender().tell(new FailureResponse(),
                            sender());
                    log.info("Failure Message. Message: " + msg.getText() + " from: " + sender());
                })
                .match(TestMessage.class, msg -> {
                    opinionAnalysisClassifierActorCounter++;
                    opinionAnalysisClassifierActorList.get(opinionAnalysisClassifierActorCounter % opinionAnalysisClassifierActorList.size())
                            .forward(msg, getContext());
                    log.info("Success Message. Message: " + msg.getText() + " from: " + sender());
                })
                .matchEquals(OPINION_ANALYSIS_ACTOR_REGISTRATION, msg -> {
                    getContext().watch(sender());
                    opinionAnalysisClassifierActorList.add(sender());
                    log.info("Text classifier actor registration. Actor: " + sender());
                })
                .match(Terminated.class, terminated -> {
                    opinionAnalysisClassifierActorList.remove(terminated.getActor());
                    log.info("Text classifier actor terminated. Actor: " + sender());
                })
                .build();
    }
}
