package com.nanodegree.swatisingh.popularmoviesapp2.Model;

public class Trailer {
    String key;
    String name;

    public Trailer(String key,String name){
        this.key = key;
        this.name = name;
    }

    public String getKey(){
        return key;
    }
    public String getName(){
        return name;
    }
}
