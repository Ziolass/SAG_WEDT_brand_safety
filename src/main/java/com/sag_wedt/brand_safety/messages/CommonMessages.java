package com.sag_wedt.brand_safety.messages;

import java.io.Serializable;

public interface CommonMessages {

    String OPINION_ANALYSIS_ACTOR_REGISTRATION = "OpinionAnalysisActorRegistration";

    abstract class MyMessage implements Serializable {}

    class TestMessage extends MyMessage {
        private final String text;

        public TestMessage(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
