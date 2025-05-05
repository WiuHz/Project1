package service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import AnotherProject1;
import airtable.AirtableClient;
import model.User;
import model.Registration;
import model.Event;

public class AirTableService{
    private AirTableService airTableService; 
    private AirtableClient airtableClient;

    public AirtableSaving(AirtableClient airtableClient) {
        this.airtableClient = new AirtableClient(airtableClient);
    }

    public void saveUsertoAirTable(User user){
        airtableClient.postToTable("Users", user.toJson());
    }
    public void saveRegistrationtoAirTable(Registration registration){
        airtableClient.postToTable("Registrations", registration.toJson());
    }
    public void saveEventtoAirTable(Event event){
        airtableClient.postToTable("Events", event.toJson());
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        JSONArray records = airtableClient.getFromTable("Events");

        for (int i = 0; i < records.length(); i++) {
            JSONObject record = records.getJSONObject(i);
            JSONObject fields = record.getJSONObject("fields");

            Event event = new Event();
            event.setId(record.getString("id")); 
            event.setName(fields.optString("name"));
            event.setDescription(fields.optString("description"));
            event.setTime(fields.optString("time"));
            event.setGroupLink(fields.optString("Group Link"));
            event.setCapacity(fields.optInt("capacity"));

            events.add(event);
        }

        return events;
    }
}