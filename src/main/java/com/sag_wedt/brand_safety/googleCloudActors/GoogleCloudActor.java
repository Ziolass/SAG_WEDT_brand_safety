package com.sag_wedt.brand_safety.googleCloudActors;

import akka.cluster.Cluster;
import com.sag_wedt.brand_safety.messages.CommonMessages;
import com.sag_wedt.brand_safety.myAgents.Callable;
import com.sag_wedt.brand_safety.myAgents.MyParentActor;


public class GoogleCloudActor extends MyParentActor {

    Cluster cluster = Cluster.get(getContext().system());

    protected GoogleCloudActor(Class typeMessageClass) {
        super(typeMessageClass);
    }

    @Override
    public void answerMessage(CommonMessages.MyMessage msg, Callable callback) {}
}
