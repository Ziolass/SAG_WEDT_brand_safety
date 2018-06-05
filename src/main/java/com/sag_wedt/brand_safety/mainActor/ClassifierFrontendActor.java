package com.sag_wedt.brand_safety.mainActor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.sag_wedt.brand_safety.messages.CommonMessages.*;
import com.sag_wedt.brand_safety.messages.Messages;
import com.sag_wedt.brand_safety.messages.RespondMessages.*;
import com.sag_wedt.brand_safety.utils.ResponseWatcher;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.sag_wedt.brand_safety.messages.CommonMessages.MY_CLASSIFIER_ACTOR;
import static com.sag_wedt.brand_safety.messages.CommonMessages.OPINION_ANALYSIS_ACTOR_REGISTRATION;


public class ClassifierFrontendActor extends AbstractActor {

    private final int REPLACE_LIMIT = 2;
    private final int RESPOND_TIME = 30;

    List<ActorRef> opinionAnalysisClassifierActorList = new ArrayList<>();
    int opinionAnalysisClassifierActorCounter = 0;

    List<ActorRef> myClassifierActorList = new ArrayList<>();
    int myClassifierActorCounter = 0;

    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    List<ResponseWatcher> responses = new ArrayList<>();

    ClassifierFrontendActor() {
        scheduleCheckInformation();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.ClassifyWebPage.class, msg -> opinionAnalysisClassifierActorList.isEmpty(), msg -> {
                    log.info("Failure Message. Message: " + msg.getPageContent() + " from: " + sender());
                })
                .match(Messages.ClassifyWebPage.class, msg -> {
                    responses.add(new ResponseWatcher(sender(), msg));
                    sendMessageToClassifier(msg);
                })
                .match(Response.class, msg -> {
                    responses.removeIf(r -> r.getMessageId().equals( msg.id));
                    log.info("Success Message. Message: " + msg.toString() + " from: " + sender());
                })
                .matchEquals(OPINION_ANALYSIS_ACTOR_REGISTRATION, msg -> {
                    getContext().watch(sender());
                    opinionAnalysisClassifierActorList.add(sender());
                    log.info("Text classifier actor registration. Actor: " + sender());
                })
                .matchEquals(MY_CLASSIFIER_ACTOR, msg -> {
                    getContext().watch(sender());
                    myClassifierActorList.add(sender());
                    log.info("My classifier actor registration. Actor: " + sender());
                })
                .match(Terminated.class, terminated -> {
                    opinionAnalysisClassifierActorList.remove(terminated.getActor());
                    log.info("Text classifier actor terminated. Actor: " + sender());
                })
                .build();
    }

    private void scheduleCheckInformation(){
        final FiniteDuration interval = Duration.create(30, TimeUnit.SECONDS);
        final ExecutionContext ec = getContext().system().dispatcher();
        getContext().system().scheduler().schedule(interval, interval, () -> {
            responses.forEach(r -> {
                if(r.getDate().until(LocalDateTime.now(), ChronoUnit.SECONDS) > RESPOND_TIME) {
                    r.incrementReplace();
                }
                if(r.getReplace() < REPLACE_LIMIT){
                    sendMessageToClassifier(r.getMessage());
                } else {
                    sendFailureMessage(r.getMessage(), r.getSender());
                }
            });
            responses.removeIf(r -> r.getReplace() >= REPLACE_LIMIT);
        }, ec);
    }

    private void sendMessageToClassifier(MyMessage msg){
        opinionAnalysisClassifierActorCounter++;
        opinionAnalysisClassifierActorList.get(opinionAnalysisClassifierActorCounter % opinionAnalysisClassifierActorList.size())
                .tell(msg, self());
    }

    private void sendFailureMessage(MyMessage msg, ActorRef recipient){
        recipient.tell(new FailureResponse(msg.id), self());
    }
}
