package edu.vanderbilt.isis.trip_planner_android_client.controller;

import java.util.HashSet;
import java.util.Set;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.TraverseMode;
import edu.vanderbilt.isis.trip_planner_android_client.view.ModeUtil;

/**
 * ModeSelectOptions class for the application
 * Keeps track of the default modes and the currently selected modes
 * Does not handle button clicks or button state
 */

public class ModeSelectOptions {

    private static TraverseMode firstMode;
    private static Set<TraverseMode> selectedModes = new HashSet<>();
    // todo initialize selected modes by querying the database for the default modes

    private ModeSelectOptions() {}

    public static boolean addDefaultMode(TraverseMode mode) {
        //todo
        return false;
    }

    public static boolean removeDefaultMode(TraverseMode mode) {
        //todo
        return false;
    }

    public static void setDefaultModes(Set<TraverseMode> modeList) {
        //todo
    }

    public static boolean selectMode(TraverseMode mode) {
        return selectedModes.add(mode);
    }

    public static boolean deselectMode(TraverseMode mode) {
        if (mode == firstMode)
            firstMode = null;
        return selectedModes.remove(mode);
    }

    public static boolean isSelected(TraverseMode mode) {
        return selectedModes.contains(mode);
    }

    public static Set<TraverseMode> getSelectedModes() {
        return selectedModes;
    }

    public static String getSelectedModesString() {
        // Return a comma separated list of modes in String format
        String str = "";
        for (TraverseMode mode : selectedModes)
            str+= ("," + ModeUtil.toString(mode));

        return str.isEmpty() ? "" : str.substring(1);
    }


    public static int getNumSelectedModes() {
        return selectedModes.size();
    }

    public static TraverseMode getFirstMode() {
        return firstMode;
    }

    public static void setFirstMode(TraverseMode firstMode) {
        selectMode(firstMode);
        ModeSelectOptions.firstMode = firstMode;
    }

    public static void removeFirstMode() {
        ModeSelectOptions.firstMode = null;
    }
}
