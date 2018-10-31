package TLCalendarParser;


public class Event {

    private String name;
    private String time;
    private String stage;


    Event(String name, String time, String stage) {
        this.name = name;
        this.time = time;
        this.stage = stage;
    }

    @Override
    public String toString() {
        return time + "\n" + name + "\n" + stage + "\n";
    }

}
