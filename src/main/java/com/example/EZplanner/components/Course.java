package com.example.EZplanner.components;

public class Course {
    private String title;
    private String number;
    private String date;
    private String time;
    private String location;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Course: " +
                "title= " + title +
                ", number= " + number +
                ", date= " + date +
                ", time= " + time +
                ", location= " + location;
    }
}
