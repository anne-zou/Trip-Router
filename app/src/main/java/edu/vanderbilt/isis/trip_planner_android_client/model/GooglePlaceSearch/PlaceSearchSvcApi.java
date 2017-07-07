package edu.vanderbilt.isis.trip_planner_android_client.model.GooglePlaceSearch;

import edu.vanderbilt.isis.trip_planner_android_client.model.GooglePlaceSearch.PlaceSearchModel.PlaceSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Anne on 6/14/2017.
 */

public interface PlaceSearchSvcApi {

    public static final String GOOGLE_PLACE_SEARCH_API_URL = "https://maps.googleapis.com/";
    public static final String ESTABLISHMENT_TYPE = "establishment";

    public static final String KEY_PARAMETER = "key";
    public static final String LOCATION_PARAMETER = "location";
    public static final String RADIUS_PARAMETER = "radius";
    public static final String TYPE_PARAMETER = "type";


    @GET("maps/api/place/nearbysearch/json")
    Call<PlaceSearchResponse> searchForPlaces(@Query(KEY_PARAMETER) String key,
                                              @Query(LOCATION_PARAMETER) String location,
                                              @Query(RADIUS_PARAMETER) Integer radius,
                                              @Query(TYPE_PARAMETER) String type);

}
