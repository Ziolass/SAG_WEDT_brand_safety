package com.sag_wedt.brand_safety.messages;

import java.io.Serializable;
import java.util.UUID;

public interface RespondMessages {

    class Response implements Serializable {
        public UUID id;

        public Response(UUID id) {
            this.id = id;
        }
    }

    class FailureResponse extends Response {
        public FailureResponse(UUID id) {
            super(id);
        }
    }

    class SuccessResponse extends Response {
        public SuccessResponse(UUID id) {
            super(id);
        }
    }
}
