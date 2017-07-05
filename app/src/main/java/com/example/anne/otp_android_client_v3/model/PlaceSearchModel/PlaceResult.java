package com.example.anne.otp_android_client_v3.model.PlaceSearchModel;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Anne on 6/14/2017.
 */

public class PlaceResult {

    private PlaceGeometry geometry;

    private URL icon;

    private String id;

    private String name;

    private OpeningHours opening_hours;

    private ArrayList<PlacePhoto> photos;

    private String place_id;

    private Float rating;

    private String reference;

    private String scope;

    private ArrayList<String> types;

    private String vicinity;

    public PlaceGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(PlaceGeometry geometry) {
        this.geometry = geometry;
    }

    public URL getIcon() {
        return icon;
    }

    public void setIcon(URL icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningHours getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(OpeningHours opening_hours) {
        this.opening_hours = opening_hours;
    }

    public ArrayList<PlacePhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<PlacePhoto> photos) {
        this.photos = photos;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
}
