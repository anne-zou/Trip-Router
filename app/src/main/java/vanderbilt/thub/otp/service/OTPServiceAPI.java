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
 * Created by chinmaya on 1/7/2017.
 */
public interface OTPServiceAPI {


    public static final String OTP_API_URL = "http://129.59.107.171:8080/otp/";

    public static final String FROM_PLACE_PARAMETER = "fromPlace";
    public static final String TO_PLACE_PARAMETER = "toPlace";
    public static final String INTERMEDIATE_PLACES_PARAMETER = "intermediatePlaces";
    public static final String MODE_PARAMETER = "mode";
    public static final String SHOW_INTERMEDIATE_STOPS_PARAMETER = "showIntermediateStops";
    public static final String OPTIMIZE_PARAMETER = "optimize";
    public static final String DATE_PARAMETER = "date"; // date to depart or arrive by
    public static final String TIME_PARAMETER = "time"; // time to depart or arrive by
    public static final String ARRIVE_BY_PARAMETER = "arriveBy"; // true for arrive, false for depart


    // Return a list of itineraries for a trip plan restricted by the given parameters - no intermediate stops
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

    // Return a list of itineraries for a trip plan restricted by the given parameters - with intermediate stops
    @GET("routers/{routerId}/plan")
    Call<Response> getTripPlan(@Path("routerId") String routerId,
                               @Query(FROM_PLACE_PARAMETER) String fromPlace,
                               @Query(TO_PLACE_PARAMETER) String toPlace,
                               @Query(INTERMEDIATE_PLACES_PARAMETER) String intermediatePlaces,
                               @Query(SHOW_INTERMEDIATE_STOPS_PARAMETER) Boolean showIntermediateStops,
                               @Query(MODE_PARAMETER) String modes,
                               @Query(OPTIMIZE_PARAMETER) String optimize,
                               @Query(DATE_PARAMETER) String date,
                               @Query(TIME_PARAMETER) String time,
                               @Query(ARRIVE_BY_PARAMETER) Boolean arriveBy);


    // boolean: Choose short or long from of results
    public static final String DETAIL_PARAMETER = "detail";
    // boolean: Include GTFS entities referenced by ID in the result
    public static final String REFS_PARAMETER = "refs";
    // double: Latitude
    public static final String LAT_PARAMETER = "lat";
    // double: Longitude
    public static final String LON_PARAMETER = "lon";
    // double: Radius
    public static final String RADIUS_PARAMETER = "radius";

    // Return a list of all stops within a certain radius of a given location
    @GET("routers/{routerId}/index/stops")
    Call<ArrayList<Stop>> getStopsByRadius(@Path("routerId") String routerId,
                                           @Query(LAT_PARAMETER) String lat,
                                           @Query(LON_PARAMETER) String lon,
                                           @Query(RADIUS_PARAMETER) String radius,
                                           @Query(DETAIL_PARAMETER) String detail,
                                           @Query(REFS_PARAMETER) String refs);

    // Return a list of routes that service a particular stop
    @GET("routers/{routerId}/index/stops/{stopId}/routes")
    Call<ArrayList<Route>> getRoutesByStop(@Path("routerId") String routerId,
                                           @Path("stopId") String stopId,
                                           @Query(DETAIL_PARAMETER) String detail,
                                           @Query(REFS_PARAMETER) String refs);

    // Return the list of stops serviced by a given route
    @GET("routers/{routerId}/index/routes/{routeId}/stops")
    Call<ArrayList<Stop>> getStopsByRouteId(@Path("routerId") String routerId,
                                            @Path("routeId") String routeId,
                                            @Query(DETAIL_PARAMETER) String detail,
                                            @Query(REFS_PARAMETER) String refs);



}
