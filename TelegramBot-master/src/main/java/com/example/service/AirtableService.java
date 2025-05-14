package com.example.service;

import com.example.AirtableClient;
import com.example.model.Registration;
import com.example.model.User;
import com.example.util.Constant;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.model.*;
import com.example.service.AirtableService;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AirtableService {
    private final AirtableClient airtableClient;

    public AirtableService() {
        this.airtableClient = new AirtableClient();
    }

    // Save a user to the User table in Airtable
    public boolean saveUser(User user) {
        try {
            // Check if user already exists
            if (getUserByTelegramId(user.getTelegramId().toString()) != null) {
                System.out.println("User already exists: " + user);
                return true; // Skip saving if user exists
            }

            JSONObject fields = new JSONObject();
            fields.put("FullName", user.getFullName());
            fields.put("Phone", user.getPhoneNumber());
            fields.put("Email", user.getEmail());
            fields.put("TelegramId", user.getTelegramId().toString());
            fields.put("DateRegistered", user.getDateRegistered().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            JSONObject record = new JSONObject();
            record.put("fields", fields);

            JSONObject requestBody = new JSONObject();
            requestBody.put("records", new JSONArray().put(record));

            String response = airtableClient.postToTable(Constant.AIRTABLE_API_USER, requestBody.toString());
            if (response != null) {
                System.out.println("User saved: " + user + ", Response: " + response);
                return true;
            } else {
                System.out.println("Failed to save user: " + user + ", No response from Airtable");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error saving user: " + user + ", Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve a user by Telegram ID
    public User getUserByTelegramId(String telegramId) {
        try {
            String filterFormula = "?filterByFormula={TelegramId}='" + telegramId + "'";
            String response = airtableClient.getFromTable(Constant.AIRTABLE_API_USER + filterFormula);
            if (response == null) {
                System.out.println("No response when retrieving user: " + telegramId);
                return null;
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray records = jsonResponse.getJSONArray("records");
            if (records.length() == 0) {
                System.out.println("No user found for Telegram ID: " + telegramId);
                return null;
            }

            JSONObject record = records.getJSONObject(0);
            JSONObject fields = record.getJSONObject("fields");

            User user = new User();
            user.setFullName(fields.getString("FullName"));
            user.setPhoneNumber(fields.getString("Phone"));
            user.setEmail(fields.getString("Email"));
            user.setTelegramId(Long.parseLong(fields.getString("TelegramId")));
            user.setDateRegistered(OffsetDateTime.parse(fields.getString("DateRegistered")));
            System.out.println("Retrieved user: " + user);
            return user;
        } catch (Exception e) {
            System.err.println("Error retrieving user: " + telegramId + ", Exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Save a registration to the Registration table in Airtable
    public boolean saveRegistration(Registration registration) {
        try {
            JSONObject fields = new JSONObject();
            fields.put("UserId", registration.getUserId());
            fields.put("EventId", registration.getEventId());
            fields.put("Status", registration.getStatus());
            fields.put("RegisteredTime", registration.getRegisteredTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            JSONObject record = new JSONObject();
            record.put("fields", fields);

            JSONObject requestBody = new JSONObject();
            requestBody.put("records", new JSONArray().put(record));

            System.out.println("Sending registration to Airtable: " + requestBody.toString());
            String response = airtableClient.postToTable(Constant.AIRTABLE_API_REGISTRATION, requestBody.toString());
            if (response != null) {
                System.out.println("Registration saved for user: " + registration.getUserId() + 
                                   ", event: " + registration.getEventId() + 
                                   ", Response: " + response);
                return true;
            } else {
                System.out.println("Failed to save registration for user: " + registration.getUserId() + 
                                   ", event: " + registration.getEventId() + 
                                   ", No response from Airtable");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error saving registration for user: " + registration.getUserId() + 
                               ", event: " + registration.getEventId() + 
                               ", Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve all events from the Event table
    public String getAllEvents() {
        try {
            String response = airtableClient.getFromTable(Constant.AIRTABLE_API_EVENT);
            if (response == null) {
                System.out.println("No events found in Event table");
                return "Không thể lấy danh sách sự kiện.";
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray records = jsonResponse.getJSONArray("records");
            StringBuilder eventList = new StringBuilder("Danh sách sự kiện:\n");

            for (int i = 0; i < records.length(); i++) {
                JSONObject record = records.getJSONObject(i);
                JSONObject fields = record.getJSONObject("fields");
                String eventId = fields.getString("EventId");
                String eventName = fields.getString("EventName");
                String time = fields.has("Time") ? fields.getString("Time") : "N/A";
                if (time.equals("N/A")) {
                    System.out.println("Time field is missing for event: " + eventName);
                }
                String description = fields.getString("Description");
                String groupLink = fields.getString("Group Link");
                Integer capacity = fields.getInt("Capacity");
                eventList.append(eventId).append(". ")
                        .append(eventName).append(". ")
                        .append(time).append(". ")
                        .append(description).append(". ")
                        .append(groupLink).append(". ")
                        .append(capacity).append("\n");
            }

            return eventList.length() > "Danh sách sự kiện:\n".length() ? eventList.toString() : "Không có sự kiện nào.";
        } catch (Exception e) {
            System.err.println("Error retrieving events: " + e.getMessage());
            e.printStackTrace();
            return "Lỗi khi lấy danh sách sự kiện: " + e.getMessage();
        }
    }

    // Retrieve all registrations from the Registration table
    public String getAllRegistrations() {
        try {
            String response = airtableClient.getFromTable(Constant.AIRTABLE_API_REGISTRATION);
            if (response == null) {
                System.out.println("No registrations found in Registration table");
                return "Không thể lấy danh sách đăng ký.";
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray records = jsonResponse.getJSONArray("records");
            StringBuilder registrationList = new StringBuilder("Danh sách đăng ký:\n");

            for (int i = 0; i < records.length(); i++) {
                JSONObject record = records.getJSONObject(i);
                JSONObject fields = record.getJSONObject("fields");
                String userId = fields.getString("UserId");
                String eventId = fields.getString("EventId");
                String status = fields.getString("Status");
                registrationList.append("User: ").append(userId)
                        .append(" - Sự kiện: ").append(eventId)
                        .append(": ").append(status).append("\n");
            }

            return registrationList.length() > "Danh sách đăng ký:\n".length() ? registrationList.toString() : "Không có đăng ký nào.";
        } catch (Exception e) {
            System.err.println("Error retrieving registrations: " + e.getMessage());
            e.printStackTrace();
            return "Lỗi khi lấy danh sách đăng ký: " + e.getMessage();
        }
    }

    // Retrieve registrations for a specific user
        public String getUserRegistrations(String userId) {
            try {
                // Lấy danh sách đăng ký của người dùng
                String filterFormula = "?filterByFormula={UserId}='" + userId + "'";
                String response = airtableClient.getFromTable(Constant.AIRTABLE_API_REGISTRATION + filterFormula);
                if (response == null) {
                    System.out.println("No registrations found for user: " + userId);
                    return "Bạn chưa đăng ký sự kiện nào.";
                }

                JSONObject jsonResponse = new JSONObject(response);
                JSONArray records = jsonResponse.getJSONArray("records");
                if (records.length() == 0) {
                    System.out.println("No registrations found for user: " + userId);
                    return "Bạn chưa đăng ký sự kiện nào.";
                }

                // Lấy danh sách tất cả sự kiện để ánh xạ
                String eventResponse = airtableClient.getFromTable(Constant.AIRTABLE_API_EVENT);
                if (eventResponse == null) {
                    System.out.println("No events found in Event table");
                    return "Không thể lấy thông tin sự kiện.";
                }

                JSONObject eventJsonResponse = new JSONObject(eventResponse);
                JSONArray eventRecords = eventJsonResponse.getJSONArray("records");
                Map<String, JSONObject> eventMap = new HashMap<>();
                for (int i = 0; i < eventRecords.length(); i++) {
                    JSONObject eventRecord = eventRecords.getJSONObject(i);
                    JSONObject fields = eventRecord.getJSONObject("fields");
                    String eventId = fields.getString("EventId");
                    eventMap.put(eventId, fields);
                }

                // Xây dựng danh sách đăng ký
                StringBuilder registrationList = new StringBuilder("Các sự kiện bạn đã đăng ký:\n");
                for (int i = 0; i < records.length(); i++) {
                    JSONObject record = records.getJSONObject(i);
                    JSONObject fields = record.getJSONObject("fields");
                    String eventId = fields.getString("EventId");
                    String status = fields.getString("Status");

                    // Lấy thông tin sự kiện từ eventMap
                    JSONObject eventFields = eventMap.get(eventId);
                    String eventInfo = eventFields != null 
                        ? eventFields.getString("EventName") + " (" + eventFields.getString("Time") + ")"
                        : eventId;

                    registrationList.append(i + 1).append(". ")
                                    .append(eventInfo)
                                    .append(" (Trạng thái: ").append(status).append(")\n");
                }

                return registrationList.toString();
            } catch (Exception e) {
                System.err.println("Error retrieving registrations for user: " + userId + ", Exception: " + e.getMessage());
                e.printStackTrace();
                return "Lỗi khi lấy danh sách sự kiện đã đăng ký: " + e.getMessage();
            }
        }

    // Update the status of a registration
    public void updateRegistrationStatus(String userId, String eventId, String status) {
        try {
            String filterFormula = "?filterByFormula=AND({UserId}='" + userId + "',{EventId}='" + eventId + "')";
            String response = airtableClient.getFromTable(Constant.AIRTABLE_API_REGISTRATION + filterFormula);
            if (response == null) {
                System.out.println("Registration not found for user: " + userId + ", event: " + eventId);
                return;
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray records = jsonResponse.getJSONArray("records");
            if (records.length() == 0) {
                System.out.println("Registration not found for user: " + userId + ", event: " + eventId);
                return;
            }

            String recordId = records.getJSONObject(0).getString("id");

            JSONObject fields = new JSONObject();
            fields.put("Status", status);

            JSONObject record = new JSONObject();
            record.put("id", recordId);
            record.put("fields", fields);

            JSONObject requestBody = new JSONObject();
            requestBody.put("records", new JSONArray().put(record));

            String patchResponse = airtableClient.patchToTable(Constant.AIRTABLE_API_REGISTRATION, requestBody.toString());
            if (patchResponse != null) {
                System.out.println("Updated registration status for user: " + userId + ", event: " + eventId + " to: " + status);
            } else {
                System.out.println("Failed to update registration status for user: " + userId + ", event: " + eventId);
            }
        } catch (Exception e) {
            System.err.println("Error updating registration status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Check if an event exists in the Event table
    public boolean eventExists(String eventId) {
        try {
            String filterFormula = "?filterByFormula={EventId}='" + eventId + "'";
            String response = airtableClient.getFromTable(Constant.AIRTABLE_API_EVENT + filterFormula);
            if (response == null) {
                System.out.println("No response when checking event: " + eventId);
                return false;
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray records = jsonResponse.getJSONArray("records");
            return records.length() > 0;
        } catch (Exception e) {
            System.err.println("Error checking event existence: " + eventId + ", Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Check if a user is registered for a specific event
    public boolean isUserRegistered(String userId, String eventId) {
        try {
            String filterFormula = "?filterByFormula=AND({UserId}='" + userId + "',{EventId}='" + eventId + "')";
            String response = airtableClient.getFromTable(Constant.AIRTABLE_API_REGISTRATION + filterFormula);
            if (response == null) {
                System.out.println("No response when checking registration for user: " + userId + ", event: " + eventId);
                return false;
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray records = jsonResponse.getJSONArray("records");
            return records.length() > 0;
        } catch (Exception e) {
            System.err.println("Error checking registration for user: " + userId + ", event: " + eventId + ", Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cancel a registration by deleting it from the Registration table
    public boolean cancelRegistration(String userId, String eventId) {
        try {
            String filterFormula = "?filterByFormula=AND({UserId}='" + userId + "',{EventId}='" + eventId + "')";
            String response = airtableClient.getFromTable(Constant.AIRTABLE_API_REGISTRATION + filterFormula);
            if (response == null) {
                System.out.println("No response when checking registration for cancellation: user: " + userId + ", event: " + eventId);
                return false;
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray records = jsonResponse.getJSONArray("records");
            if (records.length() == 0) {
                System.out.println("Registration not found for user: " + userId + ", event: " + eventId);
                return false;
            }

            String recordId = records.getJSONObject(0).getString("id");
            boolean deleted = airtableClient.deleteFromTable(Constant.AIRTABLE_API_REGISTRATION, recordId);
            if (deleted) {
                System.out.println("Registration deleted for user: " + userId + ", event: " + eventId);
            } else {
                System.out.println("Failed to delete registration for user: " + userId + ", event: " + eventId);
            }
            return deleted;
        } catch (Exception e) {
            System.err.println("Error cancelling registration for user: " + userId + ", event: " + eventId + ", Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveEvent(Event event) {
        try {
            if (eventExists(event.getId())) {
                System.out.println("Event already exists: " + event);
                return true; 
            }

            JSONObject fields = new JSONObject();
            fields.put("EventId", event.getId());
            fields.put("EventName", event.getEventName());
            fields.put("Time", event.getTime());
            fields.put("Description", event.getDescription());
            fields.put("Group Link", event.getGroupLink());
            fields.put("Capacity", event.getCapacity());

            JSONObject record = new JSONObject();
            record.put("fields", fields);

            JSONObject requestBody = new JSONObject();
            requestBody.put("records", new JSONArray().put(record));

            String response = airtableClient.postToTable(Constant.AIRTABLE_API_EVENT, requestBody.toString());
            if (response != null) {
                System.out.println("Event saved: " + event + ", Response: " + response);
                return true;
            } else {
                System.out.println("Failed to save event: " + event + ", No response from Airtable");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error saving event: " + event + ", Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvent(String eventId) {
        try {
            String filterFormula = "?filterByFormula={EventId}='" + eventId + "'";
            String response = airtableClient.getFromTable(Constant.AIRTABLE_API_EVENT + filterFormula);
            if (response == null) {
                System.out.println("No response when checking event for deletion: " + eventId);
                return false;
            }

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray records = jsonResponse.getJSONArray("records");
            if (records.length() == 0) {
                System.out.println("Event not found for deletion: " + eventId);
                return false;
            }

            String recordId = records.getJSONObject(0).getString("id");
            boolean deleted = airtableClient.deleteFromTable(Constant.AIRTABLE_API_EVENT, recordId);
            if (deleted) {
                System.out.println("Event deleted: " + eventId);
            } else {
                System.out.println("Failed to delete event: " + eventId);
            }
            return deleted;
        } catch (Exception e) {
            System.err.println("Error deleting event: " + eventId + ", Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}