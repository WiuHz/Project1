package model;
import org.telegram.telegrambots.meta.api.objects.User;

public class User{
    private int userID;
    private String username;
    private String email;
    private String phoneNumber;
    private int telegramID;
    private String telegramUsername;
    private String dateRegister;
    
    public User(int userID, String username, String email, String phoneNumber, int telegramID, String telegramUsername, String dateRegister){
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.telegramID = telegramID;
        this.telegramUsername = telegramUsername;
        this.dateRegister = dateRegister;
        
    }
    
    public void setUserID(int userID){
        this.userID = userID;
    }
    public int getUserID(){
        return userID;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return username;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return email;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public void setTelegramID(int telegramID){
        this.telegramID = telegramID;
    }
    public int getTelegramID(){
        return telegramID;
    }
    public void setTelegramUsername(String telegramUsername){
        this.telegramUsername = telegramUsername;
    }
    public String getTelegramUsername(){
        return telegramUsername;
    }
    public void setDateRegister(String dateRegister){
        this.dateRegister = dateRegister;
    }
    public String getDateRegister(){
        return dateRegister;
    }

    public String toString(){
        return "User ID: " + userID + ", Username: " + username + ", Email: " + email + ", Phone Number: " + phoneNumber + ", TelegramID: " + telegramID + ", Telegram Username: " + telegramUsername + ", Date Register: " + dateRegister;
    }
    
    @Override
    public void onUpdateReceived(Update update){
        User user = update.getMessage().getForm();
        int userID = user.getUserID(); 
        String username = user.getUsername();
        String email = user.getEmail();
        String phoneNumber = user.getPhoneNumber();
        String telegramID = String.valueOf(user.getTelegramID());
        String telegramUsername = user.getTelegramUsername();
        String dateRegister = user.getDateRegister();

        
        User user_obj = new User(userID, username, email, phoneNumber, telegramID, telegramUsername, dateRegister);
        
        saveUsertoAirTable(user_obj);
        
        System.out.println(user.getUsername());
        System.out.println(update.getMessage().getText());
    }

    
}