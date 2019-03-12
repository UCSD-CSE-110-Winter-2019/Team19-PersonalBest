package com.example.team19.personalbest.Chat;

public class Messages {


    private String message;
    private String from;
    private long time;

    public Messages() {
    }

    public Messages(String message, String from, long time) {
        this.message = message;
        this.from = from;
        this.time = time;
    }

    public String getmessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }

    public long gettime() {
        return time;
    }

    public void settime(long time) {
        this.time = time;
    }

    public String getfrom() {
        return from;
    }

    public void setfrom(String from) {
        this.from = from;
    }
}
