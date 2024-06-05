package com.example.diplom;

public class Specialist {
    private int id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String contactInfo;
    private int hours; // Изменено название поля на "hours"

    public Specialist(int id, String firstName, String lastName, String middleName, String contactInfo, int hours) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.contactInfo = contactInfo;
        this.hours = hours; // Изменено название поля
    }

    // Геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public int getHours() { // Изменено название метода на "getHours"
        return hours;
    }

    public void setHours(int hours) { // Изменено название метода на "setHours"
        this.hours = hours;
    }
}