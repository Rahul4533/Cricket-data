package com.example.cricketdata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class Cricket {

    private static final String API_URL = "https://api.cuvora.com/car/partner/cricket-data";
    private static final String API_KEY = "test-creds@2320";

    public static void main(String[] args) {
        try {
            // Fetch data from the API
            String responseBody = fetchApiData(API_URL, API_KEY);
            System.out.println("Raw response: " + responseBody);  // Print raw response for debugging

            // Check if the response starts with '[' indicating a JSON array
            if (responseBody.trim().startsWith("[")) {
                // Parse JSON response as array
                JSONArray matches = new JSONArray(responseBody);

                // Variables to store results
                int highestScore = 0;
                String highestScoreTeam = "";
                int matches300Plus = 0;

                // Process each match
                for (int i = 0; i < matches.length(); i++) {
                    JSONObject match = matches.getJSONObject(i);

                    // Parse team scores
                    int t1Score = parseScore(match.optString("t1s"));
                    int t2Score = parseScore(match.optString("t2s"));

                    // Check for highest score
                    if (t1Score > highestScore) {
                        highestScore = t1Score;
                        highestScoreTeam = match.getString("t1");
                    }
                    if (t2Score > highestScore) {
                        highestScore = t2Score;
                        highestScoreTeam = match.getString("t2");
                    }

                    // Check for matches with total score of 300 or more
                    if (t1Score + t2Score >= 300) {
                        matches300Plus++;
                    }
                }

                // Print results
                System.out.println("Highest Score: " + highestScore + " and Team Name is: " + highestScoreTeam);
                System.out.println("Number Of Matches with total 300 Plus Score: " + matches300Plus);
            } else {
                // Handle case where response is not a JSON array
                JSONObject jsonResponse = new JSONObject(responseBody);
                System.out.println("Received JSON object instead of array. Response: " + jsonResponse.toString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to fetch data from the API
    private static String fetchApiData(String apiUrl, String apiKey) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apiKey", apiKey);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    // Helper method to parse score from string
    private static int parseScore(String score) {
        try {
            return score.isEmpty() ? 0 : Integer.parseInt(score.split("/")[0].trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
