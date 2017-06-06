package com.example.anne.otp_android_client_v3;

import java.util.HashMap;

import vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode;

/**
 * Created by Anne on 6/6/2017.
 */

public class StringToModeDictionary {

    public static HashMap<String, TraverseMode> mDictionary;

    static {
        mDictionary.put("WALK", TraverseMode.WALK);
        mDictionary.put("BICYCLE", TraverseMode.BICYCLE);
        mDictionary.put("CAR", TraverseMode.CAR);
        mDictionary.put("BUS", TraverseMode.BUS);
        mDictionary.put("SUBWAY", TraverseMode.SUBWAY);
    }

    public static TraverseMode getTraverseMode(String string) {
        return mDictionary.get(string);
    }

    public static boolean contains(String string) {
        return mDictionary.containsKey(string);
    }

}
