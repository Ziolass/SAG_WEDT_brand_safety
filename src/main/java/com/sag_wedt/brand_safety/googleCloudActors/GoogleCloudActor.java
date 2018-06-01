package com.sag_wedt.brand_safety.googleCloudActors;

import akka.cluster.Cluster;
import akka.cluster.Member;
import com.sag_wedt.brand_safety.myAgents.MyActorAnswer;
import com.sag_wedt.brand_safety.myAgents.MyParentActor;


public class GoogleCloudActor extends MyParentActor {

    Cluster cluster = Cluster.get(getContext().system());

    String googleCloudServiceUrl;

    protected GoogleCloudActor(Class typeMessageClass, MyActorAnswer answer, String googleCloudServiceUrl) {
        super(typeMessageClass, answer);
        this.googleCloudServiceUrl = googleCloudServiceUrl;
    }
}
