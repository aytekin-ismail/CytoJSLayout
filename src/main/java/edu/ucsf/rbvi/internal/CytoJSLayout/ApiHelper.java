package edu.ucsf.rbvi.internal.CytoJSLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class ApiHelper {
    private String url = null;

    public ApiHelper(String url) {
        this.url = url;
    }

    public JSONObject postToSyblars(String payload) throws URISyntaxException, IOException, JSONException {
        URL obj = new URI(url).toURL();
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setDoOutput(true);
        try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
            os.writeBytes(payload);
            os.flush();
        }
        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        if(responseCode == HttpURLConnection.HTTP_OK) {
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))
            ) {
                String line;
                while((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            System.out.println("Response: " + response + "\n");
        }
        else {
            System.out.println("POST request failed: " + responseCode);
        }
        connection.disconnect();

        //iterate over response
        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getJSONObject("layout");
    }
}
