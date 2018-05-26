package com.sag_wedt.brand_safety.messages;

import java.io.Serializable;

public interface Messages {

    class ClassifyWebPage implements Serializable {
        private final String text;

        public ClassifyWebPage(String text) {
            this.text = text;
        }

        public String getPageContent() {
            return text;
        }
    }
}
