package com.example.anne.otp_android_client_v3.model.OTPStopsModel;

/**
 * Created by Anne on 6/3/2017.
 */

public class Stop {

    // FIELDS

    private String id;

    private String code;

    private String name;

    private Double lat;

    private Double lon;

    // CONSTRUCTORS

    public Stop() {}

    public Stop(String id, String code, String name, Double lat, Double lon) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    // GETTERS

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    // SETTERS

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    // TO STRING

    @Override
    public String toString() {
        return "Stop{" +
                "id='" + id + "\'" +
                ",code='" + code + "\'" +
                ",name='" + name + "\'" +
                ",lat='" + lat + "\'" +
                ",lon='" + lat + "\'" +
                "}";
    }

}
