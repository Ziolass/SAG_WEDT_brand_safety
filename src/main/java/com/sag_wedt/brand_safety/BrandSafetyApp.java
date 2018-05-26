package com.sag_wedt.brand_safety;

import com.sag_wedt.brand_safety.googleCloudActors.TextClassifierMain;
import com.sag_wedt.brand_safety.mainActor.ClassifierFrontendMain;

public class BrandSafetyApp {
    public static void main(String[] args) {
        // starting 2 frontend nodes and 3 backend nodes
        TextClassifierMain.main(new String[] { "2551" });
        TextClassifierMain.main(new String[] { "2552" });
        TextClassifierMain.main(new String[0]);
        ClassifierFrontendMain.main(new String[0]);
    }
}
