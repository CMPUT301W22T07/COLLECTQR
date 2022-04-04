package com.example.collectqr.model;

import java.util.Date;

public class ScanCommentItem {
    private final String user;
    private final Date date;
    private String comment;

    public ScanCommentItem(String user, Date date) {
        this.user = user;
        this.date = date;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getUser() {
        return user;
    }
    public Date getDate() {
        return date;
    }
    public String getComment() {
        return comment;
    }
}