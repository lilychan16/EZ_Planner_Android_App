package com.example.EZplanner.components;

public class TimerHistory {

    private String location;
    private String time;

    public TimerHistory(String location, String time) {
        this.location = location;
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TimerHistory{" +
                "location='" + location + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
