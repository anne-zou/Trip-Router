package vanderbilt.thub.otp.service;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vanderbilt.thub.otp.model.OTPPlanModel.Response;
import vanderbilt.thub.otp.model.OTPStopsModel.Route;
import vanderbilt.thub.otp.model.OTPStopsModel.Stop;

/**
 * Created by Anne on 6/3/2017.
 */

public interface OTPStopsSvcApi {

    public static final String OTP_API_URL = "http://129.59.107.171:8080/otp/";

    // boolean: Choose short or long from of results
    public static final String DETAIL_PARAMETER = "detail";
    // boolean: Include GTFS entities referenced by ID in the result
    public static final String REFS_PARAMETER = "refs";

//    // Return all stops in any pattern on a given route
//    @GET("routers/{routerId}/index/routes/{routeId}/stops")
//    Call<ArrayList<Stop>> getStopsByRouteId(@Path("routerId") String routerId,
//                                            @Path("routeId") String routeId,
//                                            @Query(DETAIL_PARAMETER) String detail,
//                                            @Query(REFS_PARAMETER) String refs);


    // double
    public static final String LAT_PARAMETER = "lat";
    // double
    public static final String LON_PARAMETER = "lon";
    // double
    public static final String RADIUS_PARAMETER = "radius";

    // Return a list of all stops within a circle around the given coordinate
    @GET("routers/{routerId}/index/stops")
    Call<ArrayList<Stop>> getStopsByRadius(@Path("routerId") String routerId,
                                           @Query(LAT_PARAMETER) String lat,
                                           @Query(LON_PARAMETER) String lon,
                                           @Query(RADIUS_PARAMETER) String radius,
                                           @Query(DETAIL_PARAMETER) String detail,
                                           @Query(REFS_PARAMETER) String refs
    );


    @GET("routers/{routerId}/index/stops/{stopId}/routes")
    Call<ArrayList<Route>> getRoutesByStop(@Path("routerId") String routerId,
                                           @Path("stopId") String stopId,
                                           @Query(DETAIL_PARAMETER) String detail,
                                           @Query(REFS_PARAMETER) String refs);

}
