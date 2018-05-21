package com.sag_wedt.brand_safety.mainActor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.sag_wedt.brand_safety.googleCloudActors.ClasificationStandart;
import com.sag_wedt.brand_safety.googleCloudActors.GoogleCloudActor;
import com.sag_wedt.brand_safety.myClassifierActors.MyClassifierActor;

import java.util.ArrayList;

public class MainActor extends AbstractActor {

    static public Props props(ActorRef myClassifierActor, ActorRef googleCloudActor) {
        return Props.create(MainActor.class, () -> new MainActor(myClassifierActor, googleCloudActor));
    }

    private ActorRef myClassifierActor;
    private ActorRef googleCloudActor;

    private ArrayList<ActorRef> child = new ArrayList<>();
    private int counter;

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private MainActor(ActorRef myClassifierActor, ActorRef googleCloudActor) {
        this.myClassifierActor = myClassifierActor;
        this.googleCloudActor = googleCloudActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClassifyWebPage.class, cwp -> {
                    this.askGoogleCloudActor(cwp.webPageText, cwp.standarts);
                    this.askMyClassifier(cwp.webPageText);
                })
                .match(GoogleCloudActor.GoogleCloudActorResponse.class, gcar -> log.info(gcar.response))
                .build();
    }

    //messages
    static public class ClassifyWebPage {
        public final String webPageText;
        public final ArrayList<ClasificationStandart> standarts;

        public ClassifyWebPage(String webPageText, ArrayList<ClasificationStandart> standards) {
            this.webPageText = webPageText;
            this.standarts = standards;
        }
    }

    private void askGoogleCloudActor(String text, ArrayList<ClasificationStandart> standarts) {
        this.googleCloudActor.tell(new GoogleCloudActor.AskGoogleCloudActor(text, standarts), getSelf());
    }

    private void askMyClassifier(String text) {
        this.myClassifierActor.tell(new MyClassifierActor.AskMyClassifierActor(text), getSelf());
    }
}
