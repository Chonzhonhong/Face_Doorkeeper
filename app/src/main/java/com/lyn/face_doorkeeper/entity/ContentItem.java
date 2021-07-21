package com.lyn.face_doorkeeper.entity;

public class ContentItem {
    private String Label;

    private String Text;

    public ContentItem(String label, String text) {
        Label = label;
        Text = text;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
