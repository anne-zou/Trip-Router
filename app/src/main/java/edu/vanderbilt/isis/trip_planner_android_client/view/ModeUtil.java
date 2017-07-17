package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import edu.vanderbilt.isis.trip_planner_android_client.R;

import java.util.HashMap;
import java.util.Map;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.TraverseMode;

/**
 * Created by Anne on 6/6/2017.
 */

public final class ModeUtil {

    /** Maps strings to their corresponding TraverseModes */
    private static HashMap<String, TraverseMode> mStringToModeDictionary;

    /** Maps drawable resources to their corresponding TraverseModes */
    private static HashMap<TraverseMode, Drawable> mModeToDrawableDictionary;

    /**
     * Flag indicating whether the setup() method (which initializes the TraverseMode-Drawable
     * HashMap) has been called
     */
    public static boolean isSetUp = false;

    /*
     * Static initializer. This is run the first time anything is called from this class.
     * Initializes HashMap mapping strings to their corresponding TraverseModes
     */
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

    /**
     * Initializes HashMap mapping drawable resources to their corresponding TraverseModes
     * @param context used to access the drawable resources
     */
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

    /**
     * Returns the TraverseMode corresponding to a string
     * @param string a valid string representing a traverse mode
     * @return
     */
    public static TraverseMode getModeFromString(String string) {
        return mStringToModeDictionary.get(string);
    }

    /**
     * Returns a copy of the drawable corresponding to a TraverseMode
     * @param mode a TraverseMode that has a corresponding drawable resource
     * @return
     */
    public static Drawable getDrawableFromTraverseMode(TraverseMode mode) {
        Drawable d = mModeToDrawableDictionary.get(mode);
        if (d != null) return d.getConstantState().newDrawable();
        else return null;
    }

    /**
     * Returns a copy of the drawable corresponding to the TraverseMode represented
     * by a string
     * @param string a valid string representing a TraverseMode that has a corresponding
     *               drawable resource
     * @return
     */
    public static Drawable getDrawableFromString(String string) {
        Drawable d = getDrawableFromTraverseMode(mStringToModeDictionary.get(string));
        if (d != null) return d.getConstantState().newDrawable();
        else return null;
    }

    /**
     * @param string  valid string representing a TraverseMode
     * @return true if the corresponding TraverseMode type has fixed stops, false otherwise
     */
    public static boolean hasStops(String string) {
        return hasStops(mStringToModeDictionary.get(string));
    }

    /**
     * @param mode
     * @return true if the TraverseMode type has fixed stops, false otherwise
     */
    public static boolean hasStops(TraverseMode mode) {
        // Update with other types of transit modes (i.e. has stops)
        return (mode == TraverseMode.BUS);
    }

    /**
     * @param mode
     * @return true if the TraverseMode has a corresponding drawable resource stored in the
     * HashMap, false otherwise
     */
    public static boolean hasCorrespondingDrawable(TraverseMode mode) {
        return mModeToDrawableDictionary.containsKey(mode);
    }

    /**
     * @param string
     * @return true if the string validly represents a TraverseMode, false otherwise
     */
    public static boolean isValidMode(String string) {
        return mStringToModeDictionary.containsKey(string);
    }

    /**
     * Returns the string representing a TraverseMode
     * @param mode
     * @return
     */
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
