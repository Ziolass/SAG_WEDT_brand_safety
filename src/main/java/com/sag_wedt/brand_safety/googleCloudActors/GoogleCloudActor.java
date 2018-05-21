package com.sag_wedt.brand_safety.googleCloudActors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.ArrayList;

public class GoogleCloudActor extends AbstractActor {

    static public Props props() {
        return Props.create(GoogleCloudActor.class, () -> new GoogleCloudActor());
    }

    private ArrayList<ActorRef> child = new ArrayList<>();
    private int counter;

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private GoogleCloudActor() {}

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AskGoogleCloudActor.class, agc -> {
                    sender().tell(new GoogleCloudActorResponse("OK"), self());
                })
                .build();
    }

    //messages
    static public class GoogleCloudActorResponse {
        public final String response;

        public GoogleCloudActorResponse(String response) {
            this.response = response;
        }
    }

    static public class AskGoogleCloudActor {
        public final String webPageText;
        public final ArrayList<ClasificationStandart> standarts;

        public AskGoogleCloudActor(String webPageText, ArrayList<ClasificationStandart> standards) {
            this.webPageText = webPageText;
            this.standarts = standards;
        }
    }
}