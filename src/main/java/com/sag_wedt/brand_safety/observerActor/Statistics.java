package com.sag_wedt.brand_safety.observerActor;

public class Statistics {
    private int time;
    private long frontendActors;
    private long textClassifierActors;

    public Statistics(int time, long frontendActors, long textClassifierActors) {
        this.time = time;
        this.frontendActors = frontendActors;
        this.textClassifierActors = textClassifierActors;
    }

    public int getTime() {
        return time;
    }

    public long getFrontendActors() {
        return frontendActors;
    }

    public long getTextClassifierActors() {
        return textClassifierActors;
    }
}
