package AnotherProject1;

import java.util.LocalDateTime;

import org.w3c.dom.events.Event;

import model.User;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler{
    private List<Event> event_by_users = new ArrayList<>();
    private List<User> user_register = new ArrrayList<>();
    
    void handleCommand(String cmdText, Long chatid, Long userID, MyBot botz){
        if (cmdText.equals("/create_event")){
            handleCreateEvent(cmdText, chatid, userID, botz);
        }
        else if (cmdText.equals("/event")){
            handleEvent(chatid, botz);
        }
        else if (cmdText.equals("/show")){
            handleShow();
        }
        else if (cmdText.equals("/register")){
            handleRegister();
        }
        else if (cmdText.equals("/myregistration")){
            handleMyRegistration();
        }
        else if (cmdText.equals("/start")){
            handleStart();
        }
    }
    
    public void handleEvent(Long chatid, MyBot botz){
        if(!event_by_users.isEmpty()){
            botz.sendMessage(chatid.toString(), "No events created yet.");
        }
        else{
            StringBuilder eventList = new StringBuilder("Events created:\n");
            for (int i = 0; i < event_by_users.size(); i++){
                Event event = event_by_users.get(i);
                eventList.append((i + 1) + ". " + event.getEventName() + " at " + event.getTime() + ". Description: " + event.getDescription() + "\n");
            }
            botz.sendMessage(chatid.toString(), eventList.toString());
        }
    }

    public void handleRegister(Long chatid, Long userID, MyBot botz){
        User user = findUser(userID);
        if (user == null) {
            user = new User(userID, chatid); 
            user_register.add(user);
        }

        if (event_by_users.isEmpty()) {
            botz.sendMessage(chatid.toString(), "The event list is empty!");
            return;
        }

        StringBuilder message = new StringBuilder("Choose the event:\n");
        for (int i = 0; i < event_by_users.size(); i++) {
            message.append(i + 1).append(". ").append(event_by_users.get(i).getEventName()).append("\n");
        }

        botz.sendMessage(chatid.toString(), message.toString());
    }

    public void handleMyRegistration(Long chatid, Long userID, MyBot botz){
        User user = findUser(userID);
        if (user == null || user.getRegisteredEvents().isEmpty()) {
            botz.sendMessage(chatid.toString(), "You have not registered for any events yet.");
        } 
        else {
            StringBuilder response = new StringBuilder("Your event list was registered before:\n");
            for (String event : user.getRegisteredEvents()) {
                response.append("- ").append(event).append("\n");
            }
            botz.sendMessage(chatid.toString(), response.toString());
        }
    }

    public void handleStart(Long chatid, MyBot botz){
        String intro = "Hi! I'm bot help you to register on our event.\n" +
                       "Using some command to let me know what you want:\n" +
                       "/events — You will see the list of our events\n" +
                       "/register — Start to register\n" +
                       "/myregistrations — Check your registered event\n" +
                       "/cancel <event_id> — You cancel the register you want\n" +
                       "/changeevent <old_event_id> <new_event_id> — Change the event you registered\n"+
                       "/start — Start the bot\n";
        botz.sendMessage(chatid.toString(), intro);
    }


    public void handleCancel(Long chatid, Long userID, String cmdText, MyBot botz){
        String[] parts = cmdText.split(" ");
        if (parts.length < 2) {
            botz.sendMessage(chatid.toString(), "Correct format: /cancel <event_id>");
            return;
        }

        int eventIndex;
        try {
            eventIndex = Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            botz.sendMessage(chatid.toString(), "Event ID was not matched.");
            return;
        }

        User user = findUser(userID);
        if (user != null && eventIndex >= 0 && eventIndex < event_by_users.size()) {
            String eventName = event_by_users.get(eventIndex).getEventName();
            if (user.getRegisteredEvents().remove(eventName)) {
                botz.sendMessage(chatid.toString(), "You have been canceled this event: " + eventName);
            } else {
                botz.sendMessage(chatid.toString(), "You have not registered this event yet.");
            }
        } 
        else {
            botz.sendMessage(chatid.toString(), "This event does not exist.");
        }
    }
    public void handleChange(){
        String[] parts = cmdText.split(" ");
        if (parts.length < 3) {
            botz.sendMessage(chatid.toString(), "Correct format: /changeevent <old_event_id> <new_event_id>");
            return;
        }

        try {
            int oldId = Integer.parseInt(parts[1]) - 1;
            int newId = Integer.parseInt(parts[2]) - 1;

            User user = findUser(userID);
            if (user == null || oldId >= event_by_users.size() || newId >= event_by_users.size()) {
                botz.sendMessage(chatid.toString(), "The ID was not matched.");
                return;
            }

            String oldEvent = event_by_users.get(oldId).getEventName();
            String newEvent = event_by_users.get(newId).getEventName();

            if (user.getRegisteredEvents().contains(oldEvent)) {
                user.getRegisteredEvents().remove(oldEvent);
                user.getRegisteredEvents().add(newEvent);
                botz.sendMessage(chatid.toString(), "You change " + oldEvent + " to " + newEvent);
            } else {
                botz.sendMessage(chatid.toString(), "You did not register this event " + oldEvent + " yet.");
            }

        } catch (NumberFormatException e) {
            botz.sendMessage(chatid.toString(), "The ID was not matched.");
        }
    }
}