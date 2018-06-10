package com.sag_wedt.brand_safety.mainActor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.sag_wedt.brand_safety.messages.CommonMessages.*;
import com.sag_wedt.brand_safety.messages.Messages.ClassifyOpinionWebPage;
import com.sag_wedt.brand_safety.messages.Messages.ClassifySentimentWebPage;
import com.sag_wedt.brand_safety.messages.Messages.ClassifyWebPage;
import com.sag_wedt.brand_safety.messages.RespondMessages.FailureResponse;
import com.sag_wedt.brand_safety.messages.RespondMessages.Response;
import com.sag_wedt.brand_safety.utils.ResponseWatcher;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.sag_wedt.brand_safety.messages.CommonMessages.*;


public class ClassifierFrontendActor extends AbstractActor {

    private final int REPLACE_LIMIT = 2;
    private final int RESPOND_TIME = 30;

    List<ActorRef> opinionAnalysisClassifierActorList = new ArrayList<>();
    int opinionAnalysisClassifierActorCounter = 0;

    List<ActorRef> sentimentAnalysisClassifierActorList = new ArrayList<>();
    int sentimentAnalysisClassifierActorCounter= 0;

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
                .match(ClassifyWebPage.class, msg -> opinionAnalysisClassifierActorList.isEmpty(), msg -> {
                    responses.add(new ResponseWatcher(sender(), msg));
                    sendMessageToClassifier(msg);
                })
                .match(ClassifyWebPage.class, msg -> {
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
                    log.info("Opinion classifier actor registration. Actor: " + sender());
                })
                .matchEquals(SENTIMENT_ANALYSIS_ACTOR_REGISTRATION, msg -> {
                    getContext().watch(sender());
                    sentimentAnalysisClassifierActorList.add(sender());
                    log.info("Sentiment classifier actor registration. Actor: " + sender());
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
        if (msg instanceof ClassifyOpinionWebPage && !opinionAnalysisClassifierActorList.isEmpty()) {
            opinionAnalysisClassifierActorCounter++;
            opinionAnalysisClassifierActorList.get(opinionAnalysisClassifierActorCounter % opinionAnalysisClassifierActorList.size())
                    .tell(msg, self());
        } else if (msg instanceof ClassifySentimentWebPage && !sentimentAnalysisClassifierActorList.isEmpty()) {
            sentimentAnalysisClassifierActorCounter++;
            sentimentAnalysisClassifierActorList.get(sentimentAnalysisClassifierActorCounter % sentimentAnalysisClassifierActorList.size())
                    .tell(msg, self());
        } else if (msg instanceof ClassifyWebPage) {
            sendMessageToClassifier(new ClassifySentimentWebPage(((ClassifyWebPage) msg).getPageContent()));
            sendMessageToClassifier(new ClassifyOpinionWebPage(((ClassifyWebPage) msg).getPageContent()));
        } else if(!opinionAnalysisClassifierActorList.isEmpty() || !sentimentAnalysisClassifierActorList.isEmpty()) {
            log.warning("Bad message format" + MyMessage.class);
        } else {
            log.warning("No classifier actors in system!");
        }
    }

    private void sendFailureMessage(MyMessage msg, ActorRef recipient){
        recipient.tell(new FailureResponse(msg.id, "Classifier system is unavailable"), self());
    }
}
