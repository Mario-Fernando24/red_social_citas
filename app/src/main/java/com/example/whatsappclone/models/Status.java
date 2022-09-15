package com.example.whatsappclone.models;

public class Status {

    private String id;
    //el id del usuario que esta publicando
    private String idUser;
    //commentario que el usuario haga en su historia
    private String comment;
    //la url de la imagen
    private String url;
    //tojson para empaquetar todos los estados
    private String json;
    //hora del estado que se creo
    private long timestamp;
    //limite que debe vencer nuestros estados
    private long timestampLimit;

    private int counterr;

    public Status(){}


    public Status(String id, String idUser, String comment, String url,
                  String json, long timestamp, long timestampLimit,int counterr) {
        this.id = id;
        this.idUser = idUser;
        this.comment = comment;
        this.url = url;
        this.json = json;
        this.timestamp = timestamp;
        this.timestampLimit = timestampLimit;
        this.counterr=counterr;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestampLimit() {
        return timestampLimit;
    }

    public void setTimestampLimit(long timestampLimit) {
        this.timestampLimit = timestampLimit;
    }

    public int getCounterr() {
        return counterr;
    }

    public void setCounterr(int counterr) {
        this.counterr = counterr;
    }
}
