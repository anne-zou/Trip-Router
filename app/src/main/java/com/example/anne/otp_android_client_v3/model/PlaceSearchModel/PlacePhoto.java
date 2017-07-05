package com.example.anne.otp_android_client_v3.model.PlaceSearchModel;

import java.util.ArrayList;

/**
 * Created by Anne on 6/14/2017.
 */

public class PlacePhoto {

    private Integer height;

    private Integer width;

    private ArrayList<String> html_attributions;

    private String photo_reference;

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public ArrayList<String> getHtml_attributions() {
        return html_attributions;
    }

    public void setHtml_attributions(ArrayList<String> html_attributions) {
        this.html_attributions = html_attributions;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }
}
