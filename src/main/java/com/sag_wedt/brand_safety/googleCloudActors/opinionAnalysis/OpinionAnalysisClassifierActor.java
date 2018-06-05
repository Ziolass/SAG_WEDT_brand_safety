package com.sag_wedt.brand_safety.googleCloudActors.opinionAnalysis;

import akka.cluster.Member;
import com.sag_wedt.brand_safety.googleCloudActors.GoogleCloudActor;
import com.sag_wedt.brand_safety.googleCloudActors.RestActor;
import com.sag_wedt.brand_safety.messages.*;
import com.sag_wedt.brand_safety.myAgents.Callable;

import static com.sag_wedt.brand_safety.messages.CommonMessages.OPINION_ANALYSIS_ACTOR_REGISTRATION;

public class OpinionAnalysisClassifierActor extends GoogleCloudActor implements RestActor {

    private final static String ANALYSIS_URL = "jakisAdres";

    private OpinionAnalysisClassifierActor() {
        super(CommonMessages.TestMessage.class, (msg, callback) -> {}, ANALYSIS_URL);
        this.setAnswer(this::answerMessage);
    }

    @Override
    public void register(Member member) {
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    OPINION_ANALYSIS_ACTOR_REGISTRATION, self());
    }

    void answerMessage(CommonMessages.MyMessage msg, Callable callback) {
        //TODO obsługa wysłania zapytania i na koniec wywołanie callback.then
        System.out.println(msg.toString());
        callback.then(new RespondMessages.SuccessResponse(msg.id));
    }

    public int sendRestRequest() {
        //ApacheHttpClient.sendGet(url) lub ApacheHttpClient.sendPost(url, listaparametrów)
        return 0;
    }
}
