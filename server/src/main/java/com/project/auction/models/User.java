package com.project.auction.models;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "Users",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "email")
        })
public class User{

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_user;
    private String name;
    private String password;
    private String email;
    private double rating;
    
    public User(){    
    }

    public User(String name, String password, String email){
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public Long getId(){
        return id_user;
    }

    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail(){
        return email;
    }

    public double getRating(){
        return rating;
    }

    public void setId(Long id){
        id_user = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setRating(double rating){
        this.rating = rating;
    }

}