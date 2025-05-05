package AnotherProject1;

import java.util.LocalDateTime;

import model.User;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler{
    private List<Event> event_by_users = new ArrayList<>();
    private List<User> user_register = new ArrrayList<>();
    
    void handleCommand(String cmdText, Long chatid, Long userID, MyBot botz){
        if (cmdText.equals("/createeevent")){
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
    public void handleRegister(){
        
    }
    public void handleMyRegistration(){
        
    }
    public void handleStart(){
        
    }
    public void handleCancel(){

    }
    public void handleChange(){
        
    }
}