package com.sag_wedt.brand_safety.messages;

import java.io.Serializable;

public interface RespondMessages {

    class Response implements Serializable {}

    class FailureResponse extends Response {}

    class SuccessResponse extends Response {}
}
