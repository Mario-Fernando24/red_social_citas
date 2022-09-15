package com.example.whatsappclone.models;

public class Message {

    private String id;
    private String idEnviar;
    private String idRecibido;
    private String idChat;
    private String message;
    private String status;
    private long timeTamp;
    //url de las imagenes
    private String url;
    //typo de las imagenes
    private String type;
    private String fileName;

    public Message() {}


    public Message(String id, String idEnviar, String idRecibido, String idChat, String message, String status, long timeTamp, String url, String type, String fileName) {
        this.id = id;
        this.idEnviar = idEnviar;
        this.idRecibido = idRecibido;
        this.idChat = idChat;
        this.message = message;
        this.status = status;
        this.timeTamp = timeTamp;
        this.url = url;
        this.type = type;
        this.fileName = fileName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEnviar() {
        return idEnviar;
    }

    public void setIdEnviar(String idEnviar) {
        this.idEnviar = idEnviar;
    }

    public String getIdRecibido() {
        return idRecibido;
    }

    public void setIdRecibido(String idRecibido) {
        this.idRecibido = idRecibido;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimeTamp() {
        return timeTamp;
    }

    public void setTimeTamp(long timeTamp) {
        this.timeTamp = timeTamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Message{" +
                "idChat='" + idChat + '\'' +
                ", message='" + message + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
