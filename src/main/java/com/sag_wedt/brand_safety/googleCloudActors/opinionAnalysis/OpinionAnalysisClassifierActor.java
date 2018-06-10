package com.sag_wedt.brand_safety.googleCloudActors.opinionAnalysis;

import akka.cluster.Member;
import com.sag_wedt.brand_safety.googleCloudActors.GoogleCloudActor;
import com.sag_wedt.brand_safety.googleCloudActors.RestActor;
import com.sag_wedt.brand_safety.messages.*;
import com.sag_wedt.brand_safety.myAgents.Callable;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.sag_wedt.brand_safety.utils.brandSafetyResponse;

import static com.sag_wedt.brand_safety.messages.CommonMessages.OPINION_ANALYSIS_ACTOR_REGISTRATION;

public class OpinionAnalysisClassifierActor extends GoogleCloudActor implements RestActor {

    private final static String ANALYSIS_URL = "jakisAdres";

    private OpinionAnalysisClassifierActor() {
        super(Messages.ClassifyWebPage.class, (msg, callback) -> {}, ANALYSIS_URL);
        this.setAnswer(this::answerMessage);
    }

    @Override
    public void register(Member member) {
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    OPINION_ANALYSIS_ACTOR_REGISTRATION, self());
    }

    void answerMessage(CommonMessages.MyMessage msg, Callable callback) {
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze
            String text = ((Messages.ClassifyWebPage)msg).getPageContent();

            Document doc = Document.newBuilder()
                    .setContent(text).setType(Type.PLAIN_TEXT).build();

            // Detects the sentiment of the text
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

            System.out.printf("Text: %s%n\n", text);
            System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());

            if (sentiment.getMagnitude() > 0.5)
            {
                if (sentiment.getScore() > 0.0)
                {
                    callback.then(new RespondMessages.SuccessResponse(msg.id, brandSafetyResponse.POSITIVE));
                }
                else if (sentiment.getScore() > 0.0)
                {
                    callback.then(new RespondMessages.SuccessResponse(msg.id, brandSafetyResponse.NEGATIVE));
                }
                else
                {
                    callback.then(new RespondMessages.SuccessResponse(msg.id, brandSafetyResponse.UNDEFINED));
                }
            }
            else
            {
                callback.then(new RespondMessages.SuccessResponse(msg.id, brandSafetyResponse.UNDEFINED));
            }

        }
        catch (Exception ex)
        {
            callback.then(new RespondMessages.FailureResponse(msg.id));
        }
    }

    public int sendRestRequest() {
        //ApacheHttpClient.sendGet(url) lub ApacheHttpClient.sendPost(url, listaparametrów)
        return 0;
    }
}
