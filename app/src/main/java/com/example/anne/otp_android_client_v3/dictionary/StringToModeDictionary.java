package com.example.anne.otp_android_client_v3.dictionary;

import java.util.HashMap;

import vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode;

/**
 * Created by Anne on 6/6/2017.
 */

public class StringToModeDictionary {

    public static HashMap<String, TraverseMode> mDictionary;

    static {
        mDictionary = new HashMap<>();
        mDictionary.put("WALK", TraverseMode.WALK);
        mDictionary.put("BICYCLE", TraverseMode.BICYCLE);
        mDictionary.put("CAR", TraverseMode.CAR);
        mDictionary.put("BUS", TraverseMode.BUS);
        mDictionary.put("SUBWAY", TraverseMode.SUBWAY);
    }

    public static TraverseMode getMode(String string) {
        return mDictionary.get(string);
    }

    public static boolean isTransit(String string) {
        return isTransit(mDictionary.get(string));
    }

    public static boolean isTransit(TraverseMode mode) {
        return (mode == TraverseMode.SUBWAY || mode == TraverseMode.BUS);
    }

    public static boolean contains(String string) {
        return mDictionary.containsKey(string);
    }
}
