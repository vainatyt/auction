package com.project.auction.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "users")
public class User{

    @Id
    @Column("id_user")
    private Long id;
    @Column("name")
    private String name;
    @Column("password")
    private String password;
    @Column("email")
    private String email;
    @Column("rating")
    private double rating;
    
    public User(){    
    }

    public User(String name, String password, String email){
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public Long getId(){
        return id;
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
        this.id= id;
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