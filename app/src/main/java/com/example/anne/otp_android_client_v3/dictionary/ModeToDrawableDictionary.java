package com.example.anne.otp_android_client_v3.dictionary;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.example.anne.otp_android_client_v3.MainActivity;
import com.example.anne.otp_android_client_v3.R;

import java.util.HashMap;
import java.util.Map;

import vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode;

/**
 * Created by Anne on 6/6/2017.
 */

public class ModeToDrawableDictionary {

    private static boolean isSetUp = false;

    private static HashMap<TraverseMode, Drawable> mDictionary;

    public static void setup(Context context) {
        mDictionary = new HashMap<>();
        mDictionary.put(TraverseMode.WALK, ContextCompat.getDrawable(context,
                R.drawable.ic_directions_walk_black_24dp));
        mDictionary.put(TraverseMode.CAR, ContextCompat.getDrawable(context,
                R.drawable.ic_directions_car_black_24dp));
        mDictionary.put(TraverseMode.BICYCLE, ContextCompat.getDrawable(context,
                R.drawable.ic_directions_bike_black_24dp));
        mDictionary.put(TraverseMode.BUS, ContextCompat.getDrawable(context,
                R.drawable.ic_directions_bus_black_24dp));
        mDictionary.put(TraverseMode.SUBWAY, ContextCompat.getDrawable(context,
                R.drawable.ic_directions_subway_black_24dp));

        for (Map.Entry<TraverseMode,Drawable> entry : mDictionary.entrySet()) {
            entry.getValue().setAlpha(MainActivity.DARK_OPACITY);
        }

        isSetUp = true;
    }

    public static Drawable getDrawable(TraverseMode mode) {
        return mDictionary.get(mode);
    }

    public static Drawable getDrawable(String string) {
        return getDrawable(StringToModeDictionary.getMode(string)).getConstantState().newDrawable();
    }

    public static boolean contains(TraverseMode mode) { return mDictionary.containsKey(mode);}



}
