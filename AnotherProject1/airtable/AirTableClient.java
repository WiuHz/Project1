
import java.io.*;
import java.net.*;
import java.util.*;

public class AirTableClient {
    private static final String AIRTABLE_API_URL = "https://api.airtable.com/v0/appeGT6lK1d0VpmW0/Telegram%20User";
    private static final String AIRTABLE_API_KEY = "patMKbjod6Mj7cfE5.9e4af91818d81b1b176102cd171114cfd41f11f48e8064584ad13d5e46cd1ada";

    public void postToTable(String tableName, String jsonData) {
        String url = AIRTABLE_API_URL + tableName;

        RequestBody body = RequestBody.create(
            jsonData, MediaType.parse("applicationQ/json"));

        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", AIRTABLE_API_KEY)
            .addHeader("Content-Type", "application/json")
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Request failed: " + response.code());
            } else {
                System.out.println("Record added to " + tableName + " successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}