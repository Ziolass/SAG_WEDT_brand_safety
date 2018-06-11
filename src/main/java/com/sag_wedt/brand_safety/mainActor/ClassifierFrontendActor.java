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

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.sag_wedt.brand_safety.messages.CommonMessages.*;


public class ClassifierFrontendActor extends AbstractActor {

    private int REPLACE_LIMIT;
    private int RESPOND_TIME;

    {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream("actorFrontend.conf"));
            REPLACE_LIMIT = Integer.getInteger(prop.getProperty("REPLACE_LIMIT"));
            RESPOND_TIME = Integer.getInteger(prop.getProperty("RESPOND_TIME"));
        } catch (IOException e) {
            REPLACE_LIMIT = 2;
            RESPOND_TIME = 3;
        }
    }


    private MyActorList opinionAnalysisClassifierActorList = new MyActorList();

    private MyActorList sentimentAnalysisClassifierActorList = new MyActorList();

    private MyActorList myClassifierActorList = new MyActorList();

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private List<ResponseWatcher> responses = Collections.synchronizedList(new ArrayList<>());

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
                    responses.stream().filter(r -> r.getMessageId().equals(msg.id)).forEach(r -> r.getSender().tell(msg, sender()));
                    responses.removeIf(r -> r.getMessageId().equals( msg.id));
                    System.out.println("Success Message. Message: " + msg.toString() + " from: " + sender());
                    log.debug("Success Message. Message: " + msg.toString() + " from: " + sender());
                })
                .matchEquals(OPINION_ANALYSIS_ACTOR_REGISTRATION, msg -> {
                    getContext().watch(sender());
                    opinionAnalysisClassifierActorList.add(sender());
                    System.out.println("Opinion classifier actor registration. Actor: " + sender());
                    log.debug("Opinion classifier actor registration. Actor: " + sender());
                })
                .matchEquals(SENTIMENT_ANALYSIS_ACTOR_REGISTRATION, msg -> {
                    getContext().watch(sender());
                    sentimentAnalysisClassifierActorList.add(sender());
                    System.out.println("Sentiment classifier actor registration. Actor: " + sender());
                    log.debug("Sentiment classifier actor registration. Actor: " + sender());
                })
                .matchEquals(MY_CLASSIFIER_ACTOR, msg -> {
                    getContext().watch(sender());
                    myClassifierActorList.add(sender());
                    System.out.println("My classifier actor registration. Actor: " + sender());
                    log.debug("My classifier actor registration. Actor: " + sender());
                })
                .match(Terminated.class, terminated -> {
                    opinionAnalysisClassifierActorList.remove(terminated.getActor());
                    sentimentAnalysisClassifierActorList.remove(terminated.getActor());
                    log.debug("Text classifier actor terminated. Actor: " + sender());
                })
                .build();
    }

    private void scheduleCheckInformation(){
        final FiniteDuration interval = Duration.create(2, TimeUnit.SECONDS);
        final ExecutionContext ec = getContext().system().dispatcher();
        getContext().system().scheduler().schedule(interval, interval, () -> {
            synchronized (responses) {
                for (Iterator<ResponseWatcher> iterator = responses.iterator(); iterator.hasNext(); ) {
                    ResponseWatcher r = iterator.next();
                    if (r.getDate().until(LocalDateTime.now(), ChronoUnit.SECONDS) > RESPOND_TIME) {
                        r.incrementReplace();
                    }
                    if (r.getReplace() < REPLACE_LIMIT) {
                        sendMessageToClassifier(r.getMessage());
                    } else {
                        sendFailureMessage(r.getMessage(), r.getSender());
                        iterator.remove();
                    }
                }
            }
        }, ec);
    }

    private void sendMessageToClassifier(MyMessage msg){
        if (msg instanceof ClassifyOpinionWebPage && opinionAnalysisClassifierActorList.nonEmpty()) {
            opinionAnalysisClassifierActorList.getNext().tell(msg, self());
        } else if (msg instanceof ClassifySentimentWebPage && sentimentAnalysisClassifierActorList.nonEmpty()) {
            sentimentAnalysisClassifierActorList.getNext().tell(msg, self());
        } else if (msg instanceof ClassifyWebPage) {
            sendMessageToClassifier(new ClassifySentimentWebPage(((ClassifyWebPage) msg).getPageContent()));
            sendMessageToClassifier(new ClassifyOpinionWebPage(((ClassifyWebPage) msg).getPageContent()));
        } else if(opinionAnalysisClassifierActorList.nonEmpty() && sentimentAnalysisClassifierActorList.nonEmpty()) {
            System.out.println("Bad message format");
            log.warning("Bad message format" + MyMessage.class);
        } else {
            System.out.println("No classifier actors in system!");
            log.warning("No classifier actors in system!");
        }
    }

    private void sendFailureMessage(MyMessage msg, ActorRef recipient){
        recipient.tell(new FailureResponse(msg.id, "Classifier system is unavailable"), self());
    }
}
