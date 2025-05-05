package model;

import java.util.List;

import AnotherProject1.MyBot;
import model.User;

public class Admin{
    public static final String ADMIN_NAME = "admin";
    public static final String ADMIN_iD = "1234";

    public boolean isAdmin(String userID){
        if (userID.equals(ADMIN_iD)){
            return true;
        }else{
            return false;
        }
    }

    public void handleAdminCommand(String msgText, Long chatid, Mybot bot) {
        String[] args = messageText.split(" ");
        String command = parts[0];

        switch (command) {
            case "/createevent":
                handleCreateEvent(Long chatid, String[] args, MyBot bot);
                break;
            case "/updateevent":
                handleUpdateEvent();
                break;
            case "/approve":
                handleApprove(Long chatid, MyBot bot);
                break;
            case "/addeventgroup":
                handleAddEventGroup(Long chatid, String[] args, MyBot bot);
                break;
            case "/notify":
                handleNotify(Long chatid, MyBot bot);
                break;
            case "/show":
                handleShow(Long chatid, MyBot bot);
                break;
            default:
                bot.sendMessage(chatId.toString(), "Do not understand the command!");
        }
    }

    private void handleCreateEvent(Long chatid, String[] args, MyBot bot) {
        if (args.length < 6) {
            bot.sendMessage(chatid.toString(), 
                "Correct format: /createevent <name> | <time> | <description> | <group link> | <capacity>");
            return;
        }
    
        try {
            if (parts.length < 5) {
                bot.sendMessage(chatid.toString(), "Wrong format! ");
                return;
            }
    
            String name = parts[0].trim();
            String description = parts[1].trim();
            String time = parts[2].trim();
            String groupLink = parts[3].trim();
            int capacity = Integer.parseInt(parts[4].trim());
    
            Event event = new Event(name, description, time, groupLink, capacity);

            airTableService.saveEventToAirTable(event);
    
            bot.sendMessage(chatid.toString(), "The event was created! \n" + event.toString());
    
        } catch (Exception e) {
            bot.sendMessage(chatid.toString(), "Failed to create event!");
            e.printStackTrace();
        }
    }
    
    private void handleUpdateEvent() {
        
    }

    private void handleApprove(Long chatid, MyBot bot) {
        try{
            List<Event> events = airTableClient.getAllEvents();

            if (events.isEmpty()) {
                bot.sendMessage(chatId.toString(), "The event list is empty!");
                return;
            }

            StringBuilder message = new StringBuilder("Event List:\n");

            for (Event event : events) {
                message.append(event.getName()).append("\n");
            }

            bot.sendMessage(chatId.toString(), message.toString());

        } catch (Exception e) {
            bot.sendMessage(chatId.toString(), "Failed to approve!");
            e.printStackTrace();
        }
    }

    private void handleAddEventGroup(Long chatid, String[] args, MyBot bot) {
        if (args.length < 3) {
            bot.sendMessage(chatid.toString(), "Correct format: /addgroupevent <Event Name> <Group Link>");
            return;
        }

        String eventName = args[1];
        String groupLink = args[2];

        List<Event> events = airTableService.getAllEvents();
        boolean found = false;

        for (Event event : events) {
            if (event.getName().equalsIgnoreCase(eventName)) {
                event.setGroupLink(groupLink);
                airTableService.saveEventtoAirTable(event); 
                bot.sendMessage(chatid.toString(), "The event link was updated: " + eventName);
                found = true;
                break;
            }
        }

        if (!found) {
            bot.sendMessage(chatid.toString(), "We could not found the event named: " + eventName);
        }
    }

    private void handleNotify(Long chatid, String[] args, MyBot bot) {
        if (args.length < 2) {
            bot.sendMessage(chatId.toString(), "Correct format: /notify <event_name> <notification_message>");
            return;
        }   
        String eventName = args[0];
        String messageContent = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        try {
            List<Event> events = airTableService.getAllEvents();
            Event targetEvent = null;
            for (Event event : events) {
                if (event.getName().equalsIgnoreCase(eventName)) {
                targetEvent = event;
                break;
                }
            }
            if (targetEvent == null) {
                bot.sendMessage(chatId.toString(), "We could not found the event named: " + eventName);
                return;
            }
            if (targetEvent.getGroupLink() != null && !targetEvent.getGroupLink().isEmpty()) {
                bot.sendMessage(targetEvent.getGroupLink(), "Notify: " + messageContent);
            }
            List<User> users = airTableService.getAllUsers(); 
            for (User user : users) {
                if (user.getRegisteredEvents().contains(eventName)) {
                    bot.sendMessage(user.getChatId(), " [" + eventName + "] " + messageContent);
                }
            }
        bot.sendMessage(chatid.toString(), "Successful Notify: " + eventName);
        } catch (Exception e) {
            bot.sendMessage(chatid.toString(), "Failed Notify!");
            e.printStackTrace();
        }
    }

    private void handleShow(Long chatid, MyBot bot) {
        try {
        List<Event> events = airtableClient.getAllEvents();

        if (events.isEmpty()) {
            bot.sendMessage(chatid.toString(), "The event list is empty!");
            return;
        }

        StringBuilder message = new StringBuilder("Event List:\n");

        for (Event event : events) {
            String event_id = event.getEventId(); 
            int count = airTableService.getRegistrationCountForEvent(event_id);

            message.append(event.getName())
                   .append(": ").append(count).append(" user registered!\n");
        }

        bot.sendMessage(chatid.toString(), message.toString());

        } catch (Exception e) {
            bot.sendMessage(chatid.toString(), "Failed.");
            e.printStackTrace(); 
        }
    }
}