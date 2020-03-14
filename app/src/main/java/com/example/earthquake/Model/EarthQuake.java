package com.example.earthquake.Model;
public class EarthQuake {
    private  String place;
    private double mag;
    private long time;
    private String type ;
    private String detailLink;
    private double lat;
    private double lon;

    public EarthQuake(String place, double mag, long time, String type, String detailLink, double lat, double lon) {
        this.place = place;
        this.mag = mag;
        this.time = time;
        this.type = type;
        this.detailLink = detailLink;
        this.lat = lat;
        this.lon = lon;
    }

    public EarthQuake() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getMag() {
        return mag;
    }

    public void setMag(double mag) {
        this.mag = mag;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}