package com.group04.studentaide;

public class User {

    private String userName, password, firstName, lastName, email;
    private Boolean educator;

    /* Getters and Setters*/
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public Boolean getEducator() {
        return educator;
    }

    public void setEducator(Boolean educator) {
        this.educator = educator;
    }


    /*User class Constructor*/
    //V1 we sayin fuck da boolean
    public User(String userName, String firstName, String lastName) {
        this.userName = userName;
        //this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        //this.educator = educator;
    }
}
