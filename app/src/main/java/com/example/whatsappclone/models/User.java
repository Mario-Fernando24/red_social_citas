package com.example.whatsappclone.models;

public class User {

    private String id;
    private String username;
    private String phone;
    private String image;
    private String info;
    private String token;
    //para saber hace cuando fue la ultima conexion en la aplicacion
    private Long ultimaConexionOnline;
    //para saber si esta en linea
    private Boolean online;
    //genero
    private String genero;

    public User(){}


    public User(String id, String username, String phone, String image, String info, String token,
                Long ultimaConexionOnline, Boolean online, String genero) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.image = image;
        this.info = info;
        this.token = token;
        this.ultimaConexionOnline = ultimaConexionOnline;
        this.online = online;
        this.genero = genero;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUltimaConexionOnline() {
        return ultimaConexionOnline;
    }

    public void setUltimaConexionOnline(Long ultimaConexionOnline) {
        this.ultimaConexionOnline = ultimaConexionOnline;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
