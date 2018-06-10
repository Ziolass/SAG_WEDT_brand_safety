package com.sag_wedt.brand_safety.googleCloudActors.sentimentAnalysis;

import akka.cluster.Member;
import com.google.cloud.language.v1.*;
import com.google.common.collect.ImmutableList;
import com.sag_wedt.brand_safety.googleCloudActors.GoogleCloudActor;
import com.sag_wedt.brand_safety.messages.CommonMessages;
import com.sag_wedt.brand_safety.messages.Messages;
import com.sag_wedt.brand_safety.messages.RespondMessages;
import com.sag_wedt.brand_safety.myAgents.Callable;

import java.util.List;

import static com.sag_wedt.brand_safety.messages.CommonMessages.SENTIMENT_ANALYSIS_ACTOR_REGISTRATION;
import static com.sag_wedt.brand_safety.utils.BrandSafetyResponse.*;

public class SentimentAnalysisClassifierActor extends GoogleCloudActor {

    private static final ImmutableList<String> CATEGORIES_BLACKLIST =
            ImmutableList.of(
                    "/Adult",
                    "/Arts & Entertainment/Events & Listings//Bars, Clubs & Nightlife",
                    "/Arts & Entertainment/Humor//Political Humor",
                    "/Arts & Entertainment/Music & Audio//Religious Music",
                    "/Arts & Entertainment/Offbeat/Occult & Paranormal",
                    "/Food & Drink/Beverages/Alcoholic Beverages",
                    "/Games/Card Games//Poker & Casino Games",
                    "/Games/Gambling",
                    "/Games/Gambling/Lottery",
                    "/Health/Health Conditions/AIDS & HIV",
                    "/Health/Health Conditions/Cancer",
                    "/Health/Health Conditions/Infectious Diseases",
                    "/Health//Mental Health",
                    "/Health//Mental Health/Anxiety & Stress",
                    "/Health//Mental Health/Depression",
                    "/Health/Pharmacy//Drugs & Medications",
                    "/Health/Substance Abuse",
                    "/Law & Government/Government/Visa & Immigration",
                    "/Law & Government/Legal/Bankruptcy",
                    "/Law & Government/Public Safety/Crime & Justice",
                    "/News/Gossip & Tabloid News//Scandals & Investigations",
                    "/Online Communities/Dating & Personals//Matrimonial Services",
                    "/People & Society/Family & Relationships//Troubled Relationships",
                    "/People & Society//Religion & Belief",
                    "/People & Society/Social Issues & Advocacy/Discrimination & Identity Relations",
                    "/People & Society/Social Issues & Advocacy/Human Rights & Liberties",
                    "/People & Society/Social Issues & Advocacy/Poverty & Hunger",
                    "/People & Society/Social Issues & Advocacy/Work & Labor Issues",
                    "/Science/Ecology & Environment//Climate Change & Global Warming",
                    "/Shopping//Tobacco Products",
                    "/Sensitive Subjects");

    private SentimentAnalysisClassifierActor() {
        super(Messages.ClassifySentimentWebPage.class);
    }

    @Override
    public void register(Member member) {
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    SENTIMENT_ANALYSIS_ACTOR_REGISTRATION, self());
    }

    @Override
    public void answerMessage(CommonMessages.MyMessage msg, Callable callback) {
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze
            String text = ((Messages.ClassifySentimentWebPage)msg).getPageContent();

            Document doc = Document.newBuilder()
                    .setContent(text).setType(Document.Type.PLAIN_TEXT).build();

            ClassifyTextRequest request = ClassifyTextRequest.newBuilder()
                    .setDocument(doc)
                    .build();
            // detect categories in the given text
            ClassifyTextResponse response = language.classifyText(request);

            List<ClassificationCategory> categories = response.getCategoriesList();

            for (ClassificationCategory category : categories) {
                System.out.printf("Category name : %s, Confidence : %.3f\n",
                        category.getName(), category.getConfidence());
            }

            if (categories.size() < 1)
            {
                callback.then(new RespondMessages.SuccessResponse<>(msg.id, UNDEFINED));
            }

            for (ClassificationCategory category : categories) {
                if (category.getConfidence() > 0.2 && CATEGORIES_BLACKLIST.contains(category.getName()))
                {
                    callback.then(new RespondMessages.SuccessResponse<>(msg.id, NEGATIVE));
                }
            }

            callback.then(new RespondMessages.SuccessResponse<>(msg.id, POSITIVE));

        }
        catch (Exception ex)
        {
            callback.then(new RespondMessages.FailureResponse(msg.id, ex.getLocalizedMessage()));
        }
    }
}


