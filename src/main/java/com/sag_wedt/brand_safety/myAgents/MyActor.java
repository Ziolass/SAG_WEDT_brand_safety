package com.sag_wedt.brand_safety.myAgents;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.sag_wedt.brand_safety.messages.CommonMessages;

public class MyActor extends AbstractActor {

    Cluster cluster = Cluster.get(getContext().system());
    final Class typeMessageClass;
    MyActorAnswer answer;

    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static Props props(Class typeMessageClass, MyActorAnswer answer) {
        return Props.create(MyActor.class, () -> new MyActor(typeMessageClass, answer));
    }

    private MyActor(Class typeMessageClass, MyActorAnswer answer) {
        this.typeMessageClass = typeMessageClass;
        this.answer = answer;
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(typeMessageClass, msg -> {
                    if(msg instanceof CommonMessages.MyMessage) {
                        answer.apply((CommonMessages.MyMessage)msg, () -> getContext().system().stop(self()));
                    } else {
                        log.warning("Actor get unexpected msg: " + msg);
                    }
                })
                .build();
    }
}
