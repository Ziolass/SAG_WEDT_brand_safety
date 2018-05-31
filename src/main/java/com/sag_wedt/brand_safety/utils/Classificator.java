package com.sag_wedt.brand_safety.utils;

import com.sag_wedt.brand_safety.messages.RespondMessages;

public interface Classificator {
    RespondMessages.SuccessResponse classify();
}
