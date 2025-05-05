import some.package.Update; 

public class Event{
    private String event_name;
    private String time;
    private String description;
    private String group_link;
    private int capacity;
    public Event(String event_name, String time, String description, String group_link, int capacity){
        new Event(event_name, time, description, group_link, capacity);
        this.event_name = event_name;
        this.time = time;
        this.description = description;
        this.group_link = group_link;
        this.capacity = capacity;
    }
    
    public void setEventName(String event_name){
        this.event_name = event_name;
    }
    public String getEventName(){
        return event_name;
    }
    
    public void setTime(String time){
        this.time = time;
    }
    public String getTime(){
        return time;
    }
    
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }

    public void setGroupLink(String group_link){
        this.group_link = group_link;
    }
    public String getGroupLink(){
        return group_link;
    }

    public void setCapacity(int capacity){
        this.capacity = capacity;
    }
    public int getCapacity(){
        return capacity;
    }
    
    @Override
    public String toString(){
        return "Event Name: " + event_name + ", Time: " + time + ", Description: " + description + ", Group Link: " + group_link + ", Capacity: " + capacity;
    }

    @Override
    public void onUpdateReceived(Update update){
        Event event = update.getForm();
        String event_name = event.getEventName();
        String time = event.getTime();
        String description = event.getDescription();
        String group_link = event.getGroupLink();
        int capacity = event.getCapacity();
        
        Event event_obj = new Event(event_name, time, description, group_link, capacity);
        
        saveEventtoAirTable(event_obj);
        
        System.out.println(event.getEventName());
    }

    private void saveEventtoAirTable(Event event) {
        System.out.println("Saving event to AirTable: " + event);
    }
}