package com.adharmavarapu.futurestocks;

import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.natural_language_understanding.v1.model.Features;
import com.ibm.watson.natural_language_understanding.v1.model.SentimentOptions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class sentiment { ;
    AnalysisResults response;
    private String toAnalyze;
    CompletableFuture<Double> cf;
    public sentiment(String s){
        toAnalyze = s;
    }
    public void analyze() {
        cf = CompletableFuture.supplyAsync(() -> {
                IamOptions options = new IamOptions.Builder().apiKey(BuildConfig.WatsonKey).build();
                NaturalLanguageUnderstanding naturalLanguageUnderstanding = new NaturalLanguageUnderstanding(
                        "2019-07-12", options);
                naturalLanguageUnderstanding.setEndPoint("https://gateway.watsonplatform.net/natural-language-understanding/api");
            String text = toAnalyze;

            SentimentOptions sentiment = new SentimentOptions.Builder()
                    .build();

            Features features = new Features.Builder()
                    .sentiment(sentiment)
                    .build();

            AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                    .text(text)
                    .features(features)
                    .build();

            response = naturalLanguageUnderstanding
                    .analyze(parameters)
                    .execute()
                    .getResult();
            return response.getSentiment().getDocument().getScore();
        });
    }
    public double getSentiment() throws ExecutionException, InterruptedException {
        return cf.get();
    }
}
