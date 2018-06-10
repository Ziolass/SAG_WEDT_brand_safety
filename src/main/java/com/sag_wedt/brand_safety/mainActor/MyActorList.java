package com.sag_wedt.brand_safety.mainActor;

import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

public class MyActorList {
    private List<ActorRef> actorList = new ArrayList<>();
    private int actorCounter = 0;

    public ActorRef getNext(){
        actorCounter++;
        return actorList.get(actorCounter % actorList.size());
    }

    public boolean add(ActorRef actor){
        return actorList.add(actor);
    }

    public boolean remove(ActorRef actor){
        return actorList.remove(actor);
    }

    public boolean nonEmpty(){
        return !actorList.isEmpty();
    }

    public boolean isEmpty(){
        return actorList.isEmpty();
    }
}
