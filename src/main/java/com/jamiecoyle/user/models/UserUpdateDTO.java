package com.jamiecoyle.user.models;


public class UserUpdateDTO {

    private String name;
    private String emailAddress;
    private String password;

    protected UserUpdateDTO() {}

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }
}
