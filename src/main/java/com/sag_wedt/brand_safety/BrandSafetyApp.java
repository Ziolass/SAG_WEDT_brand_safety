package com.sag_wedt.brand_safety;

import com.sag_wedt.brand_safety.googleCloudActors.opinionAnalysis.OpinionAnalysisClassifierMain;
import com.sag_wedt.brand_safety.mainActor.ClassifierFrontendMain;
import com.sag_wedt.brand_safety.observerActor.ObserverMain;

public class BrandSafetyApp {
    public static void main(String[] args) {
        // starting 2 frontend nodes and 3 backend nodes
        OpinionAnalysisClassifierMain.main(new String[] { "2551" });
        OpinionAnalysisClassifierMain.main(new String[] { "2552" });
        OpinionAnalysisClassifierMain.main(new String[0]);
        ObserverMain.main(new String[] { "2553" });
        ClassifierFrontendMain.main(new String[0]);
    }
}
