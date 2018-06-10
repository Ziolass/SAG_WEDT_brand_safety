package com.sag_wedt.brand_safety.messages;

public interface Messages {

    class ClassifyWebPage extends CommonMessages.MyMessage {
        private final String text;

        public ClassifyWebPage(String text) {
            this.text = text;
        }

        public String getPageContent() {
            return text;
        }
    }

    class ClassifyOpinionWebPage extends CommonMessages.MyMessage {
        private final String text;

        public ClassifyOpinionWebPage(String text) {
            this.text = text;
        }

        public String getPageContent() {
            return text;
        }
    }

    class ClassifySentimentWebPage extends CommonMessages.MyMessage {
        private final String text;

        public ClassifySentimentWebPage(String text) {
            this.text = text;
        }

        public String getPageContent() {
            return text;
        }
    }
}
