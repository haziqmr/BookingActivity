package com.example.bookingactivity;

import com.google.firebase.firestore.Exclude;

public class Userbook {

    String date, documentId;
    int time, endTime;

    public Userbook(){}

    public Userbook(String date, int time, int endTime){
        this.date = date;
        this.time = time;
        this.endTime = endTime;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
