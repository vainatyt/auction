package com.project.auction.pojo;

public class ProfileResponse {
    private String username;
    private String email;

    public ProfileResponse(String username, String email){
        this.username = username;
        this.email = email;
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setEmail(String email){
        this.email = email;
    }
}
