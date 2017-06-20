package vanderbilt.thub.otp.service;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vanderbilt.thub.otp.model.OTPPlanModel.Response;
import vanderbilt.thub.otp.model.OTPStopsModel.Stop;

/**
 * Created by Anne on 6/3/2017.
 */

public interface OTPRouteStopsSvcApi {

    public static final String OTP_API_URL = "http://129.59.107.171:8080/otp/";

    // boolean: Choose short or long from of results
    public static final String DETAIL_PARAMETER = "detail";
    // boolean: Include GTFS entities referenced by ID in the result
    public static final String REFS_PARAMETER = "refs";

    @GET("routers/{routerId}/index/routes/{routeId}/stops")
    Call<ArrayList<Stop>> geTripPlan(@Path("routerId") String routerId,
                                     @Path("routeId") String routeId,
                                     @Query(DETAIL_PARAMETER) String detail,
                                     @Query(REFS_PARAMETER) String refs);

}
