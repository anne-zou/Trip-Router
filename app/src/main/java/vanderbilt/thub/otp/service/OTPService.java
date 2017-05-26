package vanderbilt.thub.otp.service;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import vanderbilt.thub.otp.model.PlannerRequest;
import vanderbilt.thub.otp.model.Response;
import vanderbilt.thub.otp.model.TripPlan;

import java.io.IOException;

/**
 * Created by chinmaya on 1/5/2017.
 */
public class OTPService {


    public static final String ROUTER_ID = "default";

    private static OTPSvcApi sOtpService;


    public static void buildRetrofit(String baseUrl){
        sOtpService = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OTPSvcApi.class);
    }

    public static OTPSvcApi getOtpService(){
        return sOtpService;
    }

}
