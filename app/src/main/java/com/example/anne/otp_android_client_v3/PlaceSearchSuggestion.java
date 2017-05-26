package com.example.anne.otp_android_client_v3;


import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anne on 5/17/17.
 */

class PlaceSearchSuggestion implements SearchSuggestion {

    private String mText;
    private LatLng mLatlng;

    public PlaceSearchSuggestion(String primaryText, String secondaryText,
                                 LatLng latlng) {
        mText = primaryText + "\n" + secondaryText;
        mLatlng = latlng;
    }

    public PlaceSearchSuggestion(CharSequence primaryText, CharSequence secondaryText,
                                 LatLng latlng) {
        mText = primaryText + "\n" + secondaryText;
        mLatlng = latlng;
    }

    @Override
    public String getBody() {
        return mText;
    }

    public LatLng getLatLng() { return mLatlng; }


    // Useless stuff that we are forced to override:

    public PlaceSearchSuggestion(Parcel parcel) {}

    public static final Creator<PlaceSearchSuggestion> CREATOR
            = new Creator<PlaceSearchSuggestion>() {
        public PlaceSearchSuggestion createFromParcel(Parcel in) {
            return new PlaceSearchSuggestion(in);
        }
        public PlaceSearchSuggestion[] newArray(int size) {
            return new PlaceSearchSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
