package edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chinmaya on 1/5/2017.
 */
public class TPService {

    public static final String ROUTER_ID = "default";

    private static TPServiceAPI sOtpService = null;

    private static void buildRetrofit(String baseUrl){
        sOtpService = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(TPServiceAPI.class);
    }

    public static TPServiceAPI getOtpService(){
        if (sOtpService == null)
            buildRetrofit(TPServiceAPI.OTP_API_URL);
        return sOtpService;
    }

}
