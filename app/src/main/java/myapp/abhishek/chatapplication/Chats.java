package myapp.abhishek.chatapplication;

public class Chats {


    private String time_stamp;
    private boolean seen;

    public Chats(String time_stamp, boolean seen) {
        this.time_stamp = time_stamp;
        this.seen = seen;
    }

    public Chats()
    {

    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }






}
