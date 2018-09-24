package com.nanodegree.swatisingh.popularmoviesapp2.Model;

public class Movie {

    private int id;
    private String poster_path;

    public Movie(int id, String poster_path){
        this.id=id;
        this.poster_path = poster_path;

    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getPoster_path(){
        return poster_path;
    }

    public void setPoster_path(String poster_path){
        this.poster_path = poster_path;
    }
}
