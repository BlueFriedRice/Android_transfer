package com.example.transfer.Model;

public class CardInfo {

    String tagID, name, phone, nickname;

    public CardInfo()
    {

    }

    public CardInfo(String tagID, String name, String phone, String nickname) {
        this.tagID = tagID;
        this.name = name;
        this.phone = phone;
        this.nickname = nickname;
    }

    public String getTagID() {
        return tagID;
    }

    public void setTagID(String tagID) {
        this.tagID = tagID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
