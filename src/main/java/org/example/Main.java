package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // Building the endpoint uri
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.healthit.gov/data/open-api?source=Meaningful-Use-Acceleration-Scorecard.csv&period=2014"))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        // Making the API request
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Parsing the resulting JSON
        JSONObject jsonObject = new JSONObject(response.body());
        ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject value = jsonObject.getJSONObject(key);
            if (!value.getString("region").equalsIgnoreCase("National")){
                jsonList.add(value);
            }
        }

        // Sorting the json list by percentage of eligible and critical access hospitals that have demonstrated Meaningful Use of CEHRT in descending order
        // JSON key for this value is "pct_hospitals_mu"
        JSONArray sortedJsonArray = new JSONArray();
        Collections.sort( jsonList, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "pct_hospitals_mu";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get(KEY_NAME);
                    valB = (String) b.get(KEY_NAME);
                }
                catch (JSONException e) {
                    //do something
                }

                return -valA.compareTo(valB);
                //if you want to flip the sort order, simply use the following:
                //return valA.compareTo(valB);
            }
        });

        //converting jsonList from type ArrayList to type JSONArray
        for (int i = 0; i < jsonList.size(); i++) {
            sortedJsonArray.put(jsonList.get(i));
        }

        // Displaying the final results
        for (int i = 0; i < sortedJsonArray.length(); i++) {
            System.out.println("region: " + sortedJsonArray.getJSONObject(i).getString("region_code") + ",\t\tpct_hospitals_mu: " + sortedJsonArray.getJSONObject(i).getString("pct_hospitals_mu"));
        }
    }
}