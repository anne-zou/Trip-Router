package google_places_place_search.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Anne on 6/14/2017.
 */

public class PlaceSearchService {

    private static PlaceSearchSvcApi placeSearchSvc;

    public static void buildRetrofit(String baseUrl){
        placeSearchSvc = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PlaceSearchSvcApi.class);
    }

    public static PlaceSearchSvcApi getPlaceSearchSvc() {
        return placeSearchSvc;
    }
}
