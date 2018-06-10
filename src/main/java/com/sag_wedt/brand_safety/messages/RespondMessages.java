package com.sag_wedt.brand_safety.messages;

import java.io.Serializable;
import java.util.UUID;

public interface RespondMessages {

    class Response implements Serializable {
        public UUID id;

        Response(UUID id) {
            this.id = id;
        }
    }

    class FailureResponse extends Response {
        private String msg;
        public FailureResponse(UUID id, String msg) {
            super(id);
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "FailureResponse{" +
                    "msg='" + msg + '\'' +
                    '}';
        }
    }

    class SuccessResponse<T> extends Response {
        private T opinion;

        public SuccessResponse(UUID id) {
            super(id);
        }

        public SuccessResponse(UUID id, T opinion) {
            super(id);
            this.opinion = opinion;
        }

        @Override
        public String toString() {
            return "SuccessResponse{" +
                    "opinion=" + opinion +
                    '}';
        }
    }
}
