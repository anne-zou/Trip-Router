package vanderbilt.thub.otp.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vanderbilt.thub.otp.model.OTPPlanModel.Response;

import static vanderbilt.thub.otp.service.OTPPlanSvcApi.SHOW_INTERMEDIATE_STOPS_PARAMETER;

/**
 * Created by chinmaya on 1/7/2017.
 */
public interface OTPPlanSvcApi {


    public static final String OTP_API_URL = "http://129.59.107.171:8080/otp/";


    public static final String FROM_PLACE_PARAMETER = "fromPlace";
    public static final String TO_PLACE_PARAMETER = "toPlace";
    public static final String MODE_PARAMETER = "mode";
    public static final String SHOW_INTERMEDIATE_STOPS_PARAMETER = "showIntermediateStops";
    public static final String OPTIMIZE_PARAMETER = "optimize";
    public static final String DATE_PARAMETER = "date"; // date to depart or arrive by
    public static final String TIME_PARAMETER = "time"; // time to depart or arrive by
    public static final String ARRIVE_BY_PARAMETER = "arriveBy"; // true for arrive, false for depart


    //Example:- http://localhost:8080/otp/routers/default/plan?fromPlace=36.146324,-86.809687&toPlace=36.148767,-86.804569&mode=WALK,BICYCLE,TRANSIT&optimize=QUICK
    @GET("routers/{routerId}/plan")
    Call<Response> getTripPlan(@Path("routerId") String routerId,
                               @Query(FROM_PLACE_PARAMETER) String fromPlace,
                               @Query(TO_PLACE_PARAMETER) String toPlace,
                               @Query(MODE_PARAMETER) String modes,
                               @Query(SHOW_INTERMEDIATE_STOPS_PARAMETER) Boolean showIntermediateStops,
                               @Query(OPTIMIZE_PARAMETER) String optimize,
                               @Query(DATE_PARAMETER) String date,
                               @Query(TIME_PARAMETER) String time,
                               @Query(ARRIVE_BY_PARAMETER) Boolean arriveBy);





}
