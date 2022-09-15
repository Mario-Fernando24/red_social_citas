package com.example.whatsappclone.models;

import java.util.ArrayList;

public class Chat {

    private String id;
    //fecha cuando se creo el chat
    private long timesTamp;
    //nos almacenara el id de los usuario que vamos a tener la comunicación grupos
    private ArrayList<String> ids;
    //almacenar si el chat tiene mensajes
    private int numberMessage;
    private String escribiendo;

    //
    private int idNotification;

    public Chat(){}


    public Chat(String id, long timesTamp, ArrayList<String> ids, int numberMessage, String escribiendo, int idNotification) {
        this.id = id;
        this.timesTamp = timesTamp;
        this.ids = ids;
        this.numberMessage = numberMessage;
        this.escribiendo = escribiendo;
        this.idNotification = idNotification;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimesTamp() {
        return timesTamp;
    }

    public void setTimesTamp(long timesTamp) {
        this.timesTamp = timesTamp;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public int getNumberMessage() {
        return numberMessage;
    }

    public void setNumberMessage(int numberMessage) {
        this.numberMessage = numberMessage;
    }

    public String getEscribiendo() {
        return escribiendo;
    }

    public void setEscribiendo(String escribiendo) {
        this.escribiendo = escribiendo;
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }
}
