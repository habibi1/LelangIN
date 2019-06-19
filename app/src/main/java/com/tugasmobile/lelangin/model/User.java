package com.tugasmobile.lelangin.model;

public class User {
    public String email;
    public String name;
    public String photo;
    public String saldo;

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String name, String photo, String saldo) {
        this.email = email;
        this.name = name;
        this.photo = photo;
        this.saldo = saldo;
    }
}
