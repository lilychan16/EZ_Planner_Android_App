package com.example.EZplanner.components;

public class RankingItem implements Comparable<RankingItem> {
    String name;
    int result;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public RankingItem(String name, int result) {
        this.name = name;
        this.result = result;
    }

    @Override
    public int compareTo(RankingItem o) {
        if (this.result > o.result) {
            return 1;
        } else if (this.result < o.result) {
            return -1;
        } else {
            return 0;
        }
    }
}
