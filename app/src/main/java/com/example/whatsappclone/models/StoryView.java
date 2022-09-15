package com.example.whatsappclone.models;

public class StoryView {

    private String id;
    //aqui estara el id del usuario que esta viendo el estado
    private String idUser;
    //id del estado
    private String idStatus;
    //en que momento vio el estado
    private long timestamp;


    public StoryView(){}

    public StoryView(String id, String idUser, String idStatus, long timestamp) {
        this.id = id;
        this.idUser = idUser;
        this.idStatus = idStatus;
        this.timestamp = timestamp;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(String idStatus) {
        this.idStatus = idStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
