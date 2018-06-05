package com.sag_wedt.brand_safety.messages;

import java.io.Serializable;
import java.util.UUID;

public interface CommonMessages {

    String OPINION_ANALYSIS_ACTOR_REGISTRATION = "OpinionAnalysisActorRegistration";
    String MY_CLASSIFIER_ACTOR = "MyClassifierActor";

    abstract class MyMessage implements Serializable {
        public final UUID id;

        MyMessage() {
            this.id = UUID.randomUUID();
        }
    }

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
