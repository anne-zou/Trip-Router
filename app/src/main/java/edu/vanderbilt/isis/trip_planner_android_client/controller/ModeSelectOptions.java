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

class ModeSelectOptions {

    private static TraverseMode firstMode;
    private static Set<TraverseMode> selectedModes = new HashSet<>();
    // todo initialize selected modes by querying the default modes database (does not exist yet)

    private ModeSelectOptions() {}

    static boolean addDefaultMode(TraverseMode mode) {
        //todo insert mode into default modes table in the database
        return false;
    }

    static boolean removeDefaultMode(TraverseMode mode) {
        //todo remove mode from default modes table in the database
        return false;
    }

    static void isDefaultMode(TraverseMode mode) {
        // todo query the database
    }

    static void getDefaultModes(Set<TraverseMode> modeList) {
        // todo query the database
    }

    static void setDefaultModes(Set<TraverseMode> modeList) {
        //todo clear default modes table in database
    }

    /**
     * Add a mode to the set of selected modes
     * @param mode the mode to select
     * @return true if the set did not already contain the specified mode
     */
    static boolean selectMode(TraverseMode mode) {
        return selectedModes.add(mode);
    }

    /**
     * Remove a mode from the set of selected modes
     * @param mode the mode to deselect
     * @return true if the set contained the specified mode
     */
    static boolean deselectMode(TraverseMode mode) {
        if (mode == firstMode)
            firstMode = null;
        return selectedModes.remove(mode);
    }

    /**
     * Check if a mode is currently selected
     * @param mode the mode to examine
     * @return true if the specified mode is in the set of selected modes
     */
    static boolean isSelected(TraverseMode mode) {
        return selectedModes.contains(mode);
    }

    /**
     * Get the currently selected modes
     * @return the set of selected modes
     */
    static Set<TraverseMode> getSelectedModes() {
        return selectedModes;
    }

    /**
     * Get a string representation of the currently selected modes
     * @return a comma separated list of strings representing the currently selected modes
     *         e.g. "BUS", "CAR,WALK", "BICYCLE,BUS"
     */
    static String getSelectedModesString() {
        // Return a comma separated list of modes in String format
        String str = "";
        for (TraverseMode mode : selectedModes)
            str+= ("," + ModeUtil.toString(mode));
        // Remove the extra comma
        return str.isEmpty() ? "" : str.substring(1);
    }

    /**
     * Get the number of currently selected modes
     * @return the number of currently selected modes
     */
    static int getNumSelectedModes() {
        return selectedModes.size();
    }

    /**
     * Get the currently selected first mode for the trip plan
     * @return the currently selected first mode, or null if there is no first mode selected
     */
    static TraverseMode getFirstMode() {
        return firstMode;
    }

    /**
     * Set the specified mode as the first mode for the trip plan
     * @param firstMode the mode to set as the first mode
     */
    static void setFirstMode(TraverseMode firstMode) {
        selectMode(firstMode);
        ModeSelectOptions.firstMode = firstMode;
    }

    /**
     * Remove the currently selected first mode for the trip plan
     */
    static void removeFirstMode() {
        ModeSelectOptions.firstMode = null;
    }
}
