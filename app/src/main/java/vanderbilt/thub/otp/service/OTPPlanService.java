package vanderbilt.thub.otp.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chinmaya on 1/5/2017.
 */
public class OTPPlanService {

    public static final String ROUTER_ID = "default";

    private static OTPPlanSvcApi sOtpService;


    public static void buildRetrofit(String baseUrl){
        sOtpService = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OTPPlanSvcApi.class);
    }

    public static OTPPlanSvcApi getOtpService(){
        return sOtpService;
    }

}
