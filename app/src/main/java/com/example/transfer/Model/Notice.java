package com.example.transfer.Model;

public class Notice {

    private String info;
    private String date;
    private String time;
    private String aftertime;

    public Notice() {

    }

    public Notice(String info, String date, String time, String aftertime) {
        this.info = info;
        this.date = date;
        this.time = time;
        this.aftertime = aftertime;
    }

    public String getInfo() {
        return info;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getAftertime() {
        return aftertime;
    }

}
