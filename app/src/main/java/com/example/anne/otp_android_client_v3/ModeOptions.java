package com.example.anne.otp_android_client_v3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vanderbilt.thub.otp.model.TraverseMode;

/**
 * Singleton ModeOptions class for the application
 * Keeps track of the default modes and the currently selected modes
 * Does not handle button clicks or button state
 */

public class ModeOptions {

    private static ModeOptions instance = null;

    private static Set<TraverseMode> selectedModes = new HashSet<>();
    private static Set<TraverseMode> defaultModes = new HashSet<>();

    public static ModeOptions getInstance() {
        if(instance == null) {
            instance = new ModeOptions();
        }
        return instance;
    }

    protected ModeOptions() {}

    public static void selectDefaultModes() {
        selectedModes.clear();
        selectedModes.addAll(defaultModes);
    }

    public static boolean addDefaultMode(TraverseMode mode) {
        return defaultModes.add(mode);
    }

    public static boolean removeDefaultMode(TraverseMode mode) {
        return defaultModes.remove(mode);
    }

    public static void setDefaultModes(List<TraverseMode> modeList) {
        defaultModes.clear();
        defaultModes.addAll(modeList);
    }

    public static boolean addSelectedMode(TraverseMode mode) {
        return selectedModes.add(mode);
    }

    public static boolean removeSelectedMode(TraverseMode mode) {
        return selectedModes.remove(mode);
    }

    public static Set<TraverseMode> getSelectedModes() {
        return selectedModes;
    }

    public static String getSelectedModesString() {
        // Return a comma separated list of modes in String format
        String str = "";
        for (TraverseMode mode : selectedModes)
            str+= ("," + getCorrespondingString(mode));

        return str.isEmpty() ? "" : str.substring(1);
    }

    public static String getCorrespondingString(TraverseMode mode) {
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

    public static int getNumSelectedModes() {
        return selectedModes.size();
    }

}
