package com.sag_wedt.brand_safety.googleCloudActors.opinionAnalysis;

import akka.cluster.Member;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.sag_wedt.brand_safety.googleCloudActors.GoogleCloudActor;
import com.sag_wedt.brand_safety.messages.CommonMessages;
import com.sag_wedt.brand_safety.messages.Messages;
import com.sag_wedt.brand_safety.messages.RespondMessages;
import com.sag_wedt.brand_safety.myAgents.Callable;

import static com.sag_wedt.brand_safety.messages.CommonMessages.OPINION_ANALYSIS_ACTOR_REGISTRATION;
import static com.sag_wedt.brand_safety.utils.BrandSafetyResponse.*;

public class OpinionAnalysisClassifierActor extends GoogleCloudActor {

    private OpinionAnalysisClassifierActor() {
        super(Messages.ClassifyOpinionWebPage.class);
    }

    @Override
    public void register(Member member) {
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    OPINION_ANALYSIS_ACTOR_REGISTRATION, self());
    }

    @Override
    public void answerMessage(CommonMessages.MyMessage msg, Callable callback) {
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze
            String text = ((Messages.ClassifyOpinionWebPage)msg).getPageContent();

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
                    callback.then(new RespondMessages.SuccessResponse<>(msg.id, POSITIVE));
                }
                else if (sentiment.getScore() > 0.0)
                {
                    callback.then(new RespondMessages.SuccessResponse<>(msg.id, NEGATIVE));
                }
                else
                {
                    callback.then(new RespondMessages.SuccessResponse<>(msg.id, UNDEFINED));
                }
            }
            else
            {
                callback.then(new RespondMessages.SuccessResponse<>(msg.id, UNDEFINED));
            }

        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
            callback.then(new RespondMessages.FailureResponse(msg.id, ex.getLocalizedMessage()));
        }
    }
}
