package com.sag_wedt.brand_safety.myClassifierActors;

import akka.cluster.Member;
import com.sag_wedt.brand_safety.messages.CommonMessages;
import com.sag_wedt.brand_safety.messages.RespondMessages;
import com.sag_wedt.brand_safety.myAgents.Callable;
import com.sag_wedt.brand_safety.myAgents.MyParentActor;

import static com.sag_wedt.brand_safety.messages.CommonMessages.MY_CLASSIFIER_ACTOR;


public class MyClassifierActor extends MyParentActor {

    private MyClassifierActor() {
        super(CommonMessages.TestMessage.class);
    }

    @Override
    public void register(Member member) {
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    MY_CLASSIFIER_ACTOR, self());
    }

    @Override
    public void answerMessage(CommonMessages.MyMessage msg, Callable callback) {
        //TODO obsługa wysłania zapytania i na koniec wywołanie callback.then
        System.out.println(msg.toString());
        callback.then(new RespondMessages.SuccessResponse(msg.id));
    }
}
