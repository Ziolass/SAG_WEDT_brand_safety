package com.sag_wedt.brand_safety.messages;

import java.io.Serializable;

public interface CommonMessages {

    String TEXT_CLASSIFIER_ACTOR_REGISTRATION = "TextClassifierActorRegistration";

    class TestMessage implements Serializable {
        private final String text;

        public TestMessage(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
