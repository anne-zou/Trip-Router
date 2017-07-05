package vanderbilt.thub.otp.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chinmaya on 1/5/2017.
 */
public class OTPService {

    public static final String ROUTER_ID = "default";

    private static OTPServiceAPI sOtpService = null;

    private static void buildRetrofit(String baseUrl){
        sOtpService = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(OTPServiceAPI.class);
    }

    public static OTPServiceAPI getOtpService(){
        if (sOtpService == null)
            buildRetrofit(OTPServiceAPI.OTP_API_URL);
        return sOtpService;
    }

}
