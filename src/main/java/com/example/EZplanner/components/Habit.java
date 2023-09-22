package com.example.EZplanner.components;

public class Habit {

    private boolean isCompleted;
    private String habit;

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getHabit() {
        return habit;
    }

    public void setHabit(String habit) {
        this.habit = habit;
    }

    @Override
    public String toString() {
        return isCompleted + "," + habit;
    }
}
