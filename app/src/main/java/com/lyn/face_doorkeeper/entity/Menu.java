package com.lyn.face_doorkeeper.entity;

public class Menu {
    private String Title;

    private int resID;

    public Menu(String title, int resID) {
        Title = title;
        this.resID = resID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }
}
