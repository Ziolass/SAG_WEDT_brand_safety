package com.sag_wedt.brand_safety.myAgents;

import com.sag_wedt.brand_safety.messages.CommonMessages;

public interface MyActorAnswer {
    void apply(CommonMessages.MyMessage message, Callable andThen);
}
