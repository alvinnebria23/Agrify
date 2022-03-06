package com.example.agrify.RegisterPackage;

public class UserInfo {
    private String Username, Password, Firstname, Lastname, EmailAddress, Location, ContactNo, TypeOfUser;

    public UserInfo(String username, String password, String firstname, String lastname,
                    String emailAddress, String location, String contactno, String typeOfUser) {
        Username = username;
        Password = password;
        Firstname = firstname;
        Lastname = lastname;
        EmailAddress = emailAddress;
        Location = location;
        ContactNo = contactno;
        TypeOfUser = typeOfUser;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getTypeOfUser() {
        return TypeOfUser;
    }

    public void setTypeOfUser(String typeOfUser) {
        TypeOfUser = typeOfUser;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }
}
