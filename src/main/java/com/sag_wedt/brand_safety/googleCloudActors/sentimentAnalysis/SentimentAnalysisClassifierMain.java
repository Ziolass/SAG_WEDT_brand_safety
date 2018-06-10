package com.sag_wedt.brand_safety.googleCloudActors.sentimentAnalysis;

// Imports the Google Cloud client library
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import com.google.cloud.language.v1.ClassifyTextRequest;
import com.google.cloud.language.v1.ClassifyTextResponse;
import com.google.cloud.language.v1.ClassificationCategory;


public class SentimentAnalysisClassifierMain {
    public static void main(String... args) throws Exception {
        // Instantiates a client
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze
            String text = "The 2009 Richmond High School gang rape occurred on Saturday, October 24, 2009, in Richmond, a city on the northeast side of the San Francisco Bay in California, U.S., when a female student of Richmond High School was gang raped repeatedly by a group of young males in a courtyard on the school campus while a homecoming dance was being held in the gymnasium. Although seven people faced charges related to the rape, one was released after a preliminary hearing. Five of the remaining six faced life imprisonment, should the charges be upheld, and one faced a maximum of eight years in jail. All initially pleaded not guilty.\n" +
                    "\n" +
                    "The incident received national attention. As many as 20 witnesses are believed to have been aware of the attack, but for more than two hours no one notified the police.\n" +
                    "\n" +
                    "The trials for the six defendants began September 2012, with defendant Manuel Ortega pleading guilty to four felonies and sentenced the following month to 32 years in prison. Ari Morales was sentenced to 27 years in prison. Jose Montano and Marceles Peter were convicted of forcible rape acting in concert, a forcible act of sexual penetration while acting in concert, and forcible oral copulation in concert.";

            Document doc = Document.newBuilder()
                    .setContent(text).setType(Type.PLAIN_TEXT).build();

            // Detects the sentiment of the text
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

            System.out.printf("Text: %s%n\n", text);
            System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());

            ClassifyTextRequest request = ClassifyTextRequest.newBuilder()
                    .setDocument(doc)
                    .build();
            // detect categories in the given text
            ClassifyTextResponse response = language.classifyText(request);

            for (ClassificationCategory category : response.getCategoriesList()) {
                System.out.printf("Category name : %s, Confidence : %.3f\n",
                        category.getName(), category.getConfidence());
            }
        }
    }
}
