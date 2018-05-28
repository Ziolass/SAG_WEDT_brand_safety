package com.sag_wedt.brand_safety;

import com.sag_wedt.brand_safety.googleCloudActors.TextClassifierMain;
import com.sag_wedt.brand_safety.mainActor.ClassifierFrontendMain;
import com.sag_wedt.brand_safety.observerActor.ObserverMain;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.omg.CORBA.NameValuePair;
import sun.net.www.http.HttpClient;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.USER_AGENT;

public class BrandSafetyApp {
    public static void main(String[] args) {
        // starting 2 frontend nodes and 3 backend nodes
        TextClassifierMain.main(new String[] { "2551" });
        TextClassifierMain.main(new String[] { "2552" });
        TextClassifierMain.main(new String[0]);
        ObserverMain.main(new String[] { "2553" });
        ClassifierFrontendMain.main(new String[0]);
    }
}
