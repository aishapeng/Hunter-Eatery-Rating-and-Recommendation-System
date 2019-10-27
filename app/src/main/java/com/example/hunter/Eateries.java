package com.example.hunter;

public class Eateries {

    public String resName, address, profile;

    public Eateries(String resName, String address, String profile) {
        this.resName = resName;
        this.address = address;
        this.profile = profile;
    }

    public Eateries(){

    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
