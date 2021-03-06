package com.freshmanapp.blooddonor.model;

/**
 * Created by Ramkumar on 09/04/15.
 */
public class User {
    String name,  location, dob, weight, mobile, email, blood_type;
    String lat, lon;
    String profile_pic;
    String token;

    public User(String name,String lat,String lon,String location,String dob,String weight,String mobile,String email,String blood_type,String profile_pic,String token){
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.location = location;
        this.dob = dob;
        this.weight = weight;
        this.mobile = mobile;
        this.email = email;
        this.blood_type = blood_type;
        this.profile_pic = profile_pic;
        this.token = token;
    }
    public String getName(){
        return name;
    }
    public String getLon(){
        return lat;
    }
    public String getLat(){
        return lon;
    }
    public String getLocation(){
        return location;
    }
    public String getDOB(){
        return dob;
    }
    public String getWeight(){
        return weight;
    }
    public String getMobile(){
        return mobile;
    }
    public String getEmail(){
        return email;
    }
    public String getBloodType(){
        return blood_type;
    }
    public String getProfilePic(){
        return profile_pic;
    }
    public String getToken(){
        return token;
    }

}
