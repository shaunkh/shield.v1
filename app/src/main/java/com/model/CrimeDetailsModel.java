package com.model;

/**
 * Created by shaunkh on 1/8/16.
 */
public class CrimeDetailsModel {

    private String cityname ="";
    private String roadnumber ="";
    private String description ="";
    private String crimetype ="";
    private String lng ="";
    private String lat ="";
    private String userid ="";
    private String country ="";
    private String postalcode ="";
    private String crimedate ="";

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getRoadnumber() {
        return roadnumber;
    }

    public void setRoadnumber(String roadnumber) {
        this.roadnumber = roadnumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCrimetype() {
        return crimetype;
    }

    public void setCrimetype(String crimetype) {
        this.crimetype = crimetype;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getCrimedate() {
        return crimedate;
    }

    public void setCrimedate(String crimedate) {
        this.crimedate = crimedate;
    }
}
