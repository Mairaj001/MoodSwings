package com.example.moodswings.Sentiment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class SentimentAnalyzer {
    private static final String  my_API_key="245f6adc1b2e4cfa90a35e449b65f57c";
    private static final String API_KEY = "7d18507fd8304cb1b02d1c1abf9ba5a6";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

    public String transcribeAudio(String audioUrl) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("audio_url", audioUrl);
        jsonObject.addProperty("sentiment_analysis", true);
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url("https://api.assemblyai.com/v2/transcript")
                .post(body)
                .addHeader("Authorization", API_KEY)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        String responseBody = response.body().string();
        JsonObject responseObject = JsonParser.parseString(responseBody).getAsJsonObject();
        return responseObject.get("id").getAsString();
    }

    public String pollTranscription(String transcriptId) throws IOException, InterruptedException {
        String transcriptUrl = "https://api.assemblyai.com/v2/transcript/" + transcriptId;
        while (true) {
            Request pollRequest = new Request.Builder()
                    .url(transcriptUrl)
                    .addHeader("Authorization", API_KEY)
                    .build();

            Response pollResponse = client.newCall(pollRequest).execute();
            if (!pollResponse.isSuccessful()) throw new IOException("Unexpected code " + pollResponse);

            String pollResponseBody = pollResponse.body().string();
            JsonObject pollResponseObject = JsonParser.parseString(pollResponseBody).getAsJsonObject();
            String status = pollResponseObject.get("status").getAsString();
            if (status.equals("completed")) {
                return pollResponseBody;
            } else if (status.equals("failed")) {
                throw new IOException("Transcription failed");
            }

            Thread.sleep(5000);
        }
    }
}