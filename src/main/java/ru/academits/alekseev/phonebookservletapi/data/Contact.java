package ru.academits.alekseev.phonebookservletapi.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Contact {
    private int id;
    private String surname;
    private String name;
    private String phone;

    @JsonCreator
    public Contact(@JsonProperty("id") int id, @JsonProperty("surname") String surname,
                   @JsonProperty("name") String name, @JsonProperty("phone") String phone) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
