package com.example.anne.otp_android_client_v3.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.example.anne.otp_android_client_v3.view.MainActivity;
import com.example.anne.otp_android_client_v3.R;

import java.util.HashMap;
import java.util.Map;

import vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode;

/**
 * Created by Anne on 6/6/2017.
 */

public final class ModeUtils {

    public static boolean isSetUp = false;

    private static HashMap<TraverseMode, Drawable> mModeToDrawableDictionary;

    private static HashMap<String, TraverseMode> mStringToModeDictionary;

    public static void setup(Context context) {
        mModeToDrawableDictionary = new HashMap<>();
        mModeToDrawableDictionary.put(TraverseMode.WALK, ContextCompat.getDrawable(context,
                R.drawable.ic_directions_walk_black_24dp));
        mModeToDrawableDictionary.put(TraverseMode.CAR, ContextCompat.getDrawable(context,
                R.drawable.ic_directions_car_black_24dp));
        mModeToDrawableDictionary.put(TraverseMode.BICYCLE, ContextCompat.getDrawable(context,
                R.drawable.ic_directions_bike_black_24dp));
        mModeToDrawableDictionary.put(TraverseMode.BUS, ContextCompat.getDrawable(context,
                R.drawable.ic_directions_bus_black_24dp));
        mModeToDrawableDictionary.put(TraverseMode.SUBWAY, ContextCompat.getDrawable(context,
                R.drawable.ic_directions_subway_black_24dp));

        for (Map.Entry<TraverseMode,Drawable> entry : mModeToDrawableDictionary.entrySet())
            entry.getValue().setAlpha(MainActivity.DARK_OPACITY);

        isSetUp = true;
    }

    static {
        mStringToModeDictionary = new HashMap<>();
        mStringToModeDictionary.put("WALK", TraverseMode.WALK);
        mStringToModeDictionary.put("BICYCLE", TraverseMode.BICYCLE);
        mStringToModeDictionary.put("CAR", TraverseMode.CAR);
        mStringToModeDictionary.put("BUS", TraverseMode.BUS);
        mStringToModeDictionary.put("SUBWAY", TraverseMode.SUBWAY);
        mStringToModeDictionary.put("AIRPLANE", TraverseMode.AIRPLANE);
        mStringToModeDictionary.put("TRAM", TraverseMode.TRAM);
        mStringToModeDictionary.put("RAIL", TraverseMode.RAIL);
        mStringToModeDictionary.put("FERRY", TraverseMode.FERRY);
        mStringToModeDictionary.put("CABLE_CAR", TraverseMode.GONDOLA);
        mStringToModeDictionary.put("GONDOLA", TraverseMode.GONDOLA);
        mStringToModeDictionary.put("FUNICULAR", TraverseMode.FUNICULAR);
        mStringToModeDictionary.put("TRANSIT", TraverseMode.TRANSIT);
        mStringToModeDictionary.put("LEG_SWITCH", TraverseMode.LEG_SWITCH);
    }

    public static TraverseMode getModeFromString(String string) {
        return mStringToModeDictionary.get(string);
    }

    public static Drawable getDrawableFromTraverseMode(TraverseMode mode) {
        Drawable d = mModeToDrawableDictionary.get(mode);
        if (d != null) return d.getConstantState().newDrawable();
        else return null;
    }

    public static Drawable getDrawableFromString(String string) {
        Drawable d = getDrawableFromTraverseMode(mStringToModeDictionary.get(string));
        if (d != null) return d.getConstantState().newDrawable();
        else return null;
    }

    public static boolean isTransit(String string) {
        return isTransit(mStringToModeDictionary.get(string));
    }

    public static boolean isTransit(TraverseMode mode) {
        // Update with other types of transit modes (i.e. has stops)
        return (mode == TraverseMode.BUS);
    }

    public static boolean containsMode(TraverseMode mode) {
        return mModeToDrawableDictionary.containsKey(mode);
    }

    public static boolean containsString(String string) {
        return mStringToModeDictionary.containsKey(string);
    }

    public static String toString(TraverseMode mode) {
        switch (mode) {
            case WALK:
                return "WALK";
            case BICYCLE:
                return "BICYCLE";
            case CAR:
                return "CAR";
            case TRAM:
                return "TRAM";
            case SUBWAY:
                return "SUBWAY";
            case RAIL:
                return "RAIL";
            case BUS:
                return "BUS";
            case FERRY:
                return "FERRY";
            case CABLE_CAR:
                return "CAR";
            case GONDOLA:
                return "GONDOLA";
            case FUNICULAR:
                return "FUNICULAR";
            case TRANSIT:
                return "TRANSIT";
            case LEG_SWITCH:
                return "SWITCH";
            case AIRPLANE:
                return "AIRPLANE";
            default: return "";
        }
    }


}
