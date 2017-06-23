package vanderbilt.thub.otp.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Anne on 6/3/2017.
 */

public class OTPStopsService {

    public static final String ROUTER_ID = "default";

    private static OTPStopsSvcApi otpStopsService;

    public static void buildRetrofit(String baseUrl){
        otpStopsService = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OTPStopsSvcApi.class);
    }

    public static OTPStopsSvcApi getOtpService(){
        return otpStopsService;
    }

}
