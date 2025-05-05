import org.telegram.telegrambots.meta.api.objects.Registration;

public class Registration{
    private int user_registration_id;
    private int event_id;
    private String status;
    private String registeredTime;

    public Registration(int user_registration_id, int event_id, String status, String registeredTime) {
        this.user_registration_id = user_registration_id;
        this.event_id = event_id;
        this.status = status;
        this.registeredTime = registeredTime;
    }

    public void setUserRegistrationId(int user_registration_id) {
        this.user_registration_id = user_registration_id;
    }
    public int getUserRegistrationId() {
        return user_registration_id;
    }

    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return status;
    }
    public void setEventId(int event_id) {
        this.event_id = event_id;
    }
    public int getEventId() {
        return event_id;
    }
    public void setRegisteredTime(String registeredTime) {
        this.registeredTime = registeredTime;
    }
    public String getRegisteredTime() {
        return registeredTime;
    }

    @Override
    public String toString(){
        return "User Registration ID: " + user_registration_id + ", Status: " + status + ", Event ID: " + event_id + ", Registered Time: " + registeredTime;    
    }

    @Override
    public void onUpdateReceived(Update update) {
        Registration registration = update.getMessage().getRegistration();
        String user_registration_id = String.valueOf(registration.getUserRegistrationId());
        String event_id = String.valueOf(registration.getEventId());
        String status = registration.getStatus();
        String registeredTime = registration.getRegisteredTime();

        Registration registration_obj = new Registration(parsed_user_registration_id, event_id, status, registeredTime);


        saveRegistrationToAirTable(registration_obj);

        System.out.println(registration.getStatus());
    }

    public void saveRegistrationToAirTable(Registration registration) {
        System.out.println("Saving registration to Airtable: " + registration.toString());
    }
}