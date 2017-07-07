package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.vanderbilt.isis.trip_planner_android_client.controller.Controller;
import edu.vanderbilt.isis.trip_planner_android_client.controller.LocationPermissionService;
import edu.vanderbilt.isis.trip_planner_android_client.R;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.common.collect.BiMap;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.HashBiMap;
import com.google.maps.android.PolyUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Itinerary;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Leg;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.TraverseMode;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.TripPlan;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.WalkStep;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPStopsModel.Route;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPStopsModel.Stop;


@SuppressWarnings("JavaDoc")
public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        SensorEventListener {

    private static final String TAG = MainActivity.class.getName();

    enum ActivityState {HOME, HOME_PLACE_SELECTED, HOME_STOP_SELECTED, HOME_BUS_SELECTED, TRIP_PLAN, NAVIGATION}

    enum SearchFieldId {SIMPLE, DETAILED_FROM, DETAILED_TO}

    public static final float DARK_OPACITY_PERCENTAGE = .70f;

    public static final float LIGHT_OPACITY_PERCENTAGE = .54f;

    public static final int DARK_OPACITY = (int) (DARK_OPACITY_PERCENTAGE * 255);

    public static final int LIGHT_OPACITY = (int) (LIGHT_OPACITY_PERCENTAGE * 255);

    private static final int POLYLINE_WIDTH = 7; // dp

    private static final float DEFAULT_ZOOM_LEVEL = 15;

    private static final float NAVIGATION_MODE_ZOOM_LEVEL = 18;

    private static final float NAVIGATION_MODE_TILT_LEVEL = 60;

    private static final float MIN_SHOW_MARKER_ZOOM_LEVEL = 17;

    private static final double MY_CITY_LATITUDE = 36.165890;

    private static final double MY_CITY_LONGITUDE = -86.784440;

    private static final double MY_CITY_RADIUS = 48300; // meters

    private static final double LOCATION_RANGE = 0.0005; // degrees in latitude and longitude

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private ConcurrentLinkedDeque<ActivityState> mStack;

    private GoogleMap mMap;

    private SlidingUpPanelLayout mSlidingPanelLayout;

    private LinearLayout mSlidingPanelHead;

    private ScrollView mSlidingPanelTail;

    private LinearLayout mTabRowLayout;

    private DetailedSearchBarFragment mDetailedSearchBarFragment;

    private TransitStopInfoWindowFragment mTransitStopInfoWindowFragment;

    private ImageButton mFab;

    private ImageButton mRightArrowButton;

    private ImageButton mLeftArrowButton;

    private CardView mSimpleSearchBar;

    private TextView mSimpleSearchBarText;

    private SearchFieldId lastEditedSearchBar;

    private TripPlanPlace mOrigin = null;

    private TripPlanPlace mDestination = null;

    //    private Marker mOriginMarker;

    private static Marker mDestinationMarker;

    private static Marker mPlaceSelectedMarker;

    private static int mCurItineraryIndex;

    private static List<Itinerary> mItineraryList;

    private static List<Polyline> mPolylineList;

    private static List<LatLng> mItineraryPointList; // todo probably change to list of lists of leg points

    private static List<Marker> mNavigationMarkerList;

    private static BiMap<TraverseMode, ImageButton> modeToImageButtonBiMap;

    private static SensorManager mSensorManager;

    private static final float[] mAccelerometerReading = new float[3];

    private static final float[] mMagnetometerReading = new float[3];

    private static List<Stop> mCityTransitStops;

    private static Map<Marker, String> mCityTransitStopMarkers;

    private static volatile double mLastZoomLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Activity created");

        // Set up google play services
        Controller.setUpGooglePlayServices(this);

        // Setup UI
        setUpMap();
        setUpDrawer();
        setUpSimpleSearchBar();
        setUpModes();
        setUpSlidingPanel();
        setUpNavigationButtons();
        setUpSensorManager();
        setUpStateStack();
        ModeUtil.setup(this);
    }

    /**
     * Helper method for setting up the Google Map
     */
    private void setUpMap() {
        // Obtain the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // Acquire the GoogleMap (automatically initializes the maps system and the view)
        // Will trigger the OnMapReady() callback when the map is ready to be used
        mapFragment.getMapAsync(this);
    }

    /**
     * Helper method for setting up the navigation drawer
     */
    private void setUpDrawer() {

        // Get drawer layout and navigation view
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Highlight 'Planner' menu item in the navigation view
        navigationView.getMenu().getItem(0).setChecked(true);

        // Set item selected listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.isChecked())
                            return false;
                        item.setChecked(true);

                        int id = item.getItemId();

                        // TODO: Implement settings screen
                        if (id == R.id.nav_planner) {} else if (id == R.id.nav_settings) {}

                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });
    }

    /**
     * Helper method for setting up the PlaceAutocompleteFragment and simple search bar
     */
    private void setUpSimpleSearchBar() {
        mSimpleSearchBar = (CardView) findViewById(R.id.simple_search_bar_card_view);
        mSimpleSearchBarText = (TextView) findViewById(R.id.simple_search_bar_text_view);
        mSimpleSearchBarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGooglePlacesSearchWidget(SearchFieldId.SIMPLE);
            }
        });

        ImageView burger = (ImageView) findViewById(R.id.burger);
        burger.setAlpha(LIGHT_OPACITY_PERCENTAGE);
        burger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

    }

    /**
     * Helper method for setting up the chosen modes of transportation
     */
    private void setUpModes() {
        // Create the mode-imagebutton bimap
        modeToImageButtonBiMap = HashBiMap.create();

        // TODO: Grab the actual default modes set by the user
        // Initialize default modes & select the default modes
        Controller.setDefaultModes(new HashSet<TraverseMode>());
    }

    /**
     * Helper method for setting up the sliding panel layout
     */
    private void setUpSlidingPanel() {
        mSlidingPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingPanelHead = (LinearLayout) findViewById(R.id.sliding_panel_head);
        mSlidingPanelTail = (ScrollView) findViewById(R.id.sliding_panel_tail);
    }

    /**
     * Helper method for setting up the navigation buttons
     */
    private void setUpNavigationButtons() {
        mFab = (ImageButton) findViewById(R.id.fab);
        mLeftArrowButton = (ImageButton) findViewById(R.id.left_button);
        mRightArrowButton = (ImageButton) findViewById(R.id.right_button);

        mLeftArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeSlidingPanelRight();
            }
        });
        mRightArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeSlidingPanelLeft();
            }
        });
    }

    /**
     * Helper method for setting up the sensor manager
     */
    private void setUpSensorManager() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    /**
     * SensorEventListener overridden method
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * SensorEventListener overridden method
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }

    }

    /**
     * Helper method for setting up the back stack
     */
    private void setUpStateStack() {
        // Initialize state
        mStack = new ConcurrentLinkedDeque<>();
        setState(ActivityState.HOME);
    }

    /**
     * Callback triggered when the map is ready to be used
     * Sets up the Google API Client & enables the compass and location features for the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        UiSettings settings = mMap.getUiSettings();
        settings.setCompassEnabled(true);
        settings.setTiltGesturesEnabled(false);
        settings.setMapToolbarEnabled(false); // toolbar gives quick access to the Google Maps mobile app

        setMapPadding(ActivityState.HOME);

        // Hide built-in transit stop icons on map
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.hide_transit_stops_style_json));
            if (!success)
                Log.e(TAG, "Style parsing failed.");
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Request transit stops for the city to set custom markers to represent
        // transit stops on the map
        LatLng cityCenter = new LatLng(MY_CITY_LATITUDE, MY_CITY_LONGITUDE);
        Controller.requestTransitStopsWithinRadius(this, cityCenter, MY_CITY_RADIUS);
        // Will invoke either updateUIonTransitStopsRequestSuccessful() or
        // updateUIonTransitStopsRequestUnsuccessful()


        // Set the on camera move listener
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                // Get new zoom level
                double zoom = mMap.getCameraPosition().zoom;

                // If we zoomed out below min level, hide relevant markers
                if (zoom < MIN_SHOW_MARKER_ZOOM_LEVEL
                        && mLastZoomLevel > zoom && mLastZoomLevel >= MIN_SHOW_MARKER_ZOOM_LEVEL) {

                    Log.d(TAG, "Zoomed in below mininum level for showing marker");

                    if (getState() == ActivityState.NAVIGATION) {
                        if (mNavigationMarkerList != null)
                            for (Marker marker : mNavigationMarkerList)
                                marker.setVisible(false);
                    } else {
                        Log.d(TAG, "Hiding city transit stops");
                        if (mCityTransitStopMarkers != null)
                            for (Marker marker : mCityTransitStopMarkers.keySet())
                                marker.setVisible(false);
                    }

                    // If we zoomed above min level, show relevant markers
                } else if (zoom >= MIN_SHOW_MARKER_ZOOM_LEVEL
                        && mLastZoomLevel < zoom && mLastZoomLevel < MIN_SHOW_MARKER_ZOOM_LEVEL) {

                    Log.d(TAG, "Zoomed in above mininum level for showing marker");

                    if (getState() == ActivityState.NAVIGATION) {
                        if (mNavigationMarkerList != null)
                            for (Marker marker : mNavigationMarkerList)
                                marker.setVisible(true);
                    } else {
                        Log.d(TAG, "Showing city transit stops");
                        if (mCityTransitStopMarkers != null)
                            for (Marker marker : mCityTransitStopMarkers.keySet())
                                marker.setVisible(true);
                    }
                }

                // Update last zoom level
                mLastZoomLevel = zoom;
            }
        });


        // Set the on point of interest click listener
        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {

                if (!isAHomeState(getState()))
                    return;

                Controller.interruptOngoingRoutesRequests();

                // Center camera on the selected POI
                mMap.animateCamera(CameraUpdateFactory.newLatLng(pointOfInterest.latLng));

                // Get the place & display details
                Controller.requestPlaceById(MainActivity.this, pointOfInterest.placeId);
            }
        });

        // Set the on map click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Log.i(TAG, "Location on map clicked: " + latLng.toString());

                if (!isAHomeState(getState()))
                    return;

                Controller.interruptOngoingRoutesRequests();

                // Center camera on the selected location
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                goToNextScreen(ActivityState.HOME_PLACE_SELECTED);

                NumberFormat formatter = new DecimalFormat("##0.0000");
                String latLngString = "("
                        + formatter.format(Math.round(latLng.latitude * 10000)/10000.0)
                        + ", " + formatter.format(Math.round(latLng.longitude * 10000)/10000.0)
                        + ")";
                selectPlaceOnMap(new TripPlanPlace(latLngString, latLng, " Unnamed location"));
            }
        });


        // Set the on marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                // If any marker was clicked, interrupt ongoing requests
                Controller.interruptOngoingRoutesRequests();

                // Exit if activity is not in a home state
                if (!isAHomeState(getState()))
                    return true;

                // If transit stop marker was clicked
                if (!mCityTransitStopMarkers.keySet().contains(marker))
                    return true;

                // Transition to HOME_STOP_SELECTED mode
                goToNextScreen(ActivityState.HOME_STOP_SELECTED);

                // Set marker
                removeMarker(mPlaceSelectedMarker);
                mPlaceSelectedMarker = mMap.addMarker(new MarkerOptions()
                        .position(marker.getPosition()));

                // Move camera
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                // Show stop info
                String stopId = mCityTransitStopMarkers.get(marker);
                mTransitStopInfoWindowFragment.clear();
                mTransitStopInfoWindowFragment.setTransitStopNameText(marker.getTitle());
                mTransitStopInfoWindowFragment.requestStopInfo(stopId);

                return true;
            }
        });
    }

    /**
     * Callback invoked from the controller layer upon successful receipt of response
     * for getting a place by id
     * @param myPlace
     */
    public void updateUIonGetPlaceByIdRequestResponse(Place myPlace) {

        Log.i(TAG, "Point of interest Place found: "
                + myPlace.getName());

        goToNextScreen(ActivityState.HOME_PLACE_SELECTED);

        selectPlaceOnMap(new TripPlanPlace(myPlace.getName(),
                myPlace.getLatLng(), myPlace.getAddress()));
    }

    /**
     * Callback invoked from the controller layer upon failure of getting a place by id
     */
    public void updateUIonGetPlaceByIdRequestFailure() {
        Log.e(TAG, "Point of interest Place not found");
    }

    /**
     * Callback invoked from the controller layer upon successful receipt of response
     * for a transit stops request
     * @param stops
     */
    public void updateUIonTransitStopsRequestResponse(List<Stop> stops) {

        mCityTransitStops = stops;
        Log.d(TAG, "Number of bus stops:" + mCityTransitStops.size());
        mCityTransitStopMarkers = new HashMap<>();

        // Save the transit stop markers
        for (Stop stop : mCityTransitStops) {
            mCityTransitStopMarkers.put(
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(stop.getLat(), stop.getLon()))
                            .icon(BitmapDescriptorFactory.fromBitmap(
                                    drawableToBitmap(getDrawable(R.drawable.ic_bus_stop))))
                            .title(stop.getName())
                            .visible(false)
                            .anchor(.5f,.5f)),
                    stop.getId());
        }

        // Show the markers if we are at or above the min zoom level
        if (mMap.getCameraPosition().zoom >= MIN_SHOW_MARKER_ZOOM_LEVEL)
            for (Marker marker : mCityTransitStopMarkers.keySet())
                marker.setVisible(true);
    }

    /**
     * Callback invoked from the controller layer upon failure of a transit stops request
     */
    public void updateUIonTransitStopsRequestFailure() {
        Log.e(TAG, "Request for transit stops was unsuccessful");
        Toast.makeText(MainActivity.this, "Failed to get transit stops",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback invoked from the controller layer upon successful receipt of response
     * for a request for routes servicing a stop
     */
    public void updateUIonRoutesRequestResponse(final List<Route> routes) {

        // Show route number icons in info window
        for (Route route : routes) {
            ItineraryLegIconView view = new ItineraryLegIconView(MainActivity.this);
            view.setRouteName(route.getShortName());
            view.setRouteColor(Color.parseColor("#" + route.getColor()));
            view.setShowRoute(true);
            mTransitStopInfoWindowFragment.addRouteIcon(view);
        }
    }

    /**
     * Callback invoked from the controller layer upon failure of a request for
     * routes servicing a stop
     */
    public void updateUIonRoutesRequestFailure() {}

    /**
     * Callback invoked from the controller layer upon connection of the GoogleApiClient
     * @param locationServicesWereEnabled true if location services were successfully enabled
     */
    public void updateUIOnGoogleAPIClientConnected(boolean locationServicesWereEnabled) {

        if (locationServicesWereEnabled) {

            // Start low frequency location updates
            Controller.startLowAccuracyLocationUpdates(this);

            // Enable the My Location button
            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException ignored) {
            }

            // Move map camera to current location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Controller.getCurrentLocation(this)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL));
        }

    }

    @Override
    public void onBackPressed() {

        Log.d(TAG, "Back pressed");

        // Close drawer if open
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        // Collapse sliding panel if expanded
        if (mSlidingPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }

        // Go to previous screen
        goToPreviousScreen();
    }


    /**
     * Helper method to facilitate transition to the next screen in the activity
     * @param newState
     */
    public void goToNextScreen(ActivityState newState) {

        ActivityState oldState = getState();


        // Home --> HOME_STOP_SELECTED
        if (isAHomeState(oldState) && newState == ActivityState.HOME_STOP_SELECTED) {

            // If not already in HOME_STOP_SELECTED mode
            if (oldState != ActivityState.HOME_STOP_SELECTED) {

                // Make HOME_STOP_SELECTED the only state over HOME in the state stack
                while (getState() != ActivityState.HOME)
                    onBackPressed();
                setState(ActivityState.HOME_STOP_SELECTED);

                // Hide sliding layout
                mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                // Set map padding
                setMapPadding(ActivityState.HOME_STOP_SELECTED);

            }

            // Initialize a fragment transaction to show the info window if necessary
            FragmentManager fragmentManager = getFragmentManager();
            if (mTransitStopInfoWindowFragment == null)
                mTransitStopInfoWindowFragment = new TransitStopInfoWindowFragment();
            if (oldState != ActivityState.HOME_STOP_SELECTED)
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_up)
                        .add(R.id.transit_stop_info_window_frame, mTransitStopInfoWindowFragment)
                        .commit();

            return;
        }


        // Home --> HOME_PLACE_SELECTED
        if (isAHomeState(oldState) && newState == ActivityState.HOME_PLACE_SELECTED) {

            // If the previous state was HOME_STOP_SELECTED:
            if (oldState == ActivityState.HOME_STOP_SELECTED) {
                // Remove the transit stop info window
                removeTransitStopInfoWindow();
                // Pop HOME_STOP_SELECTED screen off the back stack
                removeState();
            }

            // Set the state to HOME_PLACE_SELECTED
            if (getState() != ActivityState.HOME_PLACE_SELECTED)
                setState(ActivityState.HOME_PLACE_SELECTED);

            // Set map padding
            setMapPadding(ActivityState.HOME_PLACE_SELECTED);

            // Hide simple search bar
            removeSimpleSearchBar();

            // Clear sliding panel head
            mSlidingPanelHead.removeAllViews();
            mSlidingPanelLayout.setTouchEnabled(false);
            mSlidingPanelHead.setOrientation(LinearLayout.VERTICAL);
            return;
        }


        // Home --> TRIP_PLAN
        if (isAHomeState(oldState) && newState == ActivityState.TRIP_PLAN) {

            Log.d(TAG, "Transitioning to TRIP_PLAN screen");

            // If the previous screen was HOME_STOP_SELECTED
            if (oldState == ActivityState.HOME_STOP_SELECTED)
                // Remove the transit stop info window
                removeTransitStopInfoWindow();

            // Update back stack so that HOME and TRIP_PLAN are the only states on the stack
            while (getState() != ActivityState.HOME)
                removeState();
            setState(ActivityState.TRIP_PLAN);

            // Hide simple search bar
            mSimpleSearchBar.setVisibility(View.GONE);

            // Create detailed search bar fragment
            mDetailedSearchBarFragment = new DetailedSearchBarFragment();

            // Initialize a fragment transaction to show the detailed search bar
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.detailed_search_bar_frame, mDetailedSearchBarFragment)
                    .addToBackStack("Show detailed search bar for trip plan screen")
                    .commit();

            // Clear & show sliding panel
            mSlidingPanelHead.removeAllViews();
            mSlidingPanelTail.removeAllViews();
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            mSlidingPanelLayout.setTouchEnabled(true);

            // Resize map
            setMapPadding(ActivityState.TRIP_PLAN);

            // Set up "start navigation" button (fab)
            mFab.setImageDrawable(getDrawable(R.drawable.ic_navigation_white_24dp));
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mFab.setClickable(false);
                    // FAB becomes clickable again as the "exit navigation mode" button
                    // after the new onClickListener is set in call to goToNextScreen()

                    // Launch navigation if the trip origin matches the current location
                    LatLng tripStartLocation = mOrigin.getLocation();
                    LatLng myLocation = Controller.getCurrentLocation(MainActivity.this);
                    if (tripStartLocation == null ||
                            Math.abs(tripStartLocation.latitude - myLocation.latitude)
                            > LOCATION_RANGE ||
                            Math.abs(tripStartLocation.longitude - myLocation.longitude)
                            > LOCATION_RANGE) {
                        Toast.makeText(MainActivity.this,
                                "Cannot launch navigation mode for trip " +
                                        "that does not begin at the current location",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        goToNextScreen(ActivityState.NAVIGATION);
                    }
                }
            });

            // Navigation button to be activated when itinerary is displayed
            mFab.setClickable(false);

            return;
        }


        // TRIP_PLAN --> NAVIGATION
        if (getState() == ActivityState.TRIP_PLAN && newState == ActivityState.NAVIGATION) {

            Log.d(TAG, "Transitioning to NAVIGATION screen");

            // Exit if there is an error with the trip plan itineraries
            if (mItineraryList == null || mItineraryList.isEmpty() ||
                    mItineraryList.size() <= mCurItineraryIndex)
                return;

            // Update state
            setState(ActivityState.NAVIGATION);

            // Disable my location button
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            // Expand map padding
            setMapPadding(ActivityState.NAVIGATION);

            // Hide city transit stop markers
            for (Marker marker : mCityTransitStopMarkers.keySet())
                marker.setVisible(false);

            // Hide detailed search bar
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .remove(mDetailedSearchBarFragment)
                    .addToBackStack("Hide detailed search bar for navigation screen")
                    .commit();

            // Set up "exit navigation" button (fab)
            Drawable stop = getDrawable(R.drawable.ic_clear_black_24dp);
            stop.setAlpha(LIGHT_OPACITY);
            mFab.setBackground(getDrawable(R.drawable.circle_white));
            mFab.setImageDrawable(stop);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFab.setClickable(false);
                    onBackPressed();
                }
            });

            // Hide arrows
            hideArrowButtons();

            // Hide sliding panel
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

            // Create navigation step and transit stop markers
            mNavigationMarkerList.clear();
            boolean isLeft = true;
            for (Leg leg : mItineraryList.get(mCurItineraryIndex).getLegs()) {

                // Navigation step markers
                for (WalkStep walkStep : leg.getSteps()) {

                    String walkStepDirection = walkStep.getRelativeDirection() == null ?
                            walkStep.getAbsoluteDirection().toString()
                            : walkStep.getRelativeDirection().toString();

                    Bitmap instructionBitmap = createWalkStepBitmap(walkStepDirection
                            + " on " + walkStep.getStreetName(), isLeft);

                    float anchorX = isLeft ? 1f : 0f;
                    isLeft = !isLeft;

                    mNavigationMarkerList.add(mMap.addMarker(new MarkerOptions()
                            .title(walkStep.getRelativeDirection() + " on "
                                    + walkStep.getStreetName())
                            .anchor(anchorX, 1f)
                            .position(new LatLng(walkStep.getLat(), walkStep.getLon()))
                            .icon(BitmapDescriptorFactory.fromBitmap(instructionBitmap))
                            .visible(false))
                    );

                }

                // Transit stop markers
                if (ModeUtil.hasFixedStops(leg.getMode())) {
                    if (leg.getIntermediateStops() != null)
                        for (edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Place place
                                : leg.getIntermediateStops()) {

                            Drawable d = getDrawable(R.drawable.ic_bus_stop);
                            Bitmap b = drawableToBitmap(d);

                            mNavigationMarkerList.add(mMap.addMarker(new MarkerOptions()
                                    .title(place.getName())
                                    .position(new LatLng(place.getLat(), place.getLon()))
                                    .anchor(0.5f, 1f)
                                    .icon(BitmapDescriptorFactory.fromBitmap(b))
                                    .visible(false))
                            );

                        }
                }
            }

            // Show markers if at or above min zoom level
            if (mMap.getCameraPosition().zoom >= MIN_SHOW_MARKER_ZOOM_LEVEL)
                for (Marker marker : mNavigationMarkerList)
                    marker.setVisible(true);

            // Start location requests & register sensor listeners
            Controller.startHighAccuracyLocationUpdates(this);
            registerSensorListeners(); // invokes onSensorChanged which updates camera

            updateNavigationModeCamera();

            return;
        }
    }


    /**
     * Helper method to facilitate transition to the previous screen in the activity
     */
    public void goToPreviousScreen() {
        // Interrupt any ongoing trip plan request
        Controller.interruptOngoingTripPlanRequests();

        // Perform the corresponding necessary actions based on the current state
        switch (removeState()) {
            case TRIP_PLAN:

                // TRIP_PLAN --> HOME

                // Remove state from back stack until back at home screen
                while (getState() != ActivityState.HOME)
                    removeState();

                // Remove origin & destination & place selected marker from the map
                removeMarker(mDestinationMarker);
//                removeMarker(mOriginMarker);
                removeMarker(mPlaceSelectedMarker);

                // Remove previous itinerary from the map if it exists
                if (mPolylineList != null) {
                    for (Polyline polyline : mPolylineList)
                        polyline.remove();
                    mPolylineList = null;
                }

                // Reset MyLocation button functionality (center map on current location when pressed)
                resetMyLocationButton();

                // Revert map shape & center/zoom to current location
                setMapPadding(ActivityState.HOME);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(Controller.getCurrentLocation(this)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL));

                // Hide navigation buttons
                hideArrowButtons();
                mFab.setVisibility(View.GONE);

                // Clear and hide sliding panel
                mSlidingPanelHead.removeAllViews();
                mSlidingPanelTail.removeAllViews();
                mSlidingPanelHead.setOnTouchListener(null);
                mSlidingPanelHead.setOnTouchListener(null);
                mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                // Show simple search bar
                mSimpleSearchBar.setVisibility(View.VISIBLE);
                mSimpleSearchBarText.setText(getResources().getText(R.string.where_to));

                // Remove detailed search bar fragment
                super.onBackPressed();

                break;

            case HOME_PLACE_SELECTED:

                // HOME_PLACE_SELECTED --> HOME

                // Remove place selected marker
                removeMarker(mPlaceSelectedMarker);

                // Set map padding
                setMapPadding(ActivityState.HOME);

                // Hide & clear sliding panel
                mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                mSlidingPanelHead.removeAllViews();

                // Hide fab
                mFab.setVisibility(View.GONE);

                // Revert simple search bar
                mSimpleSearchBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up));
                mSimpleSearchBar.setVisibility(View.VISIBLE);

                // Focus camera back to current location
                mMap.animateCamera(CameraUpdateFactory.newLatLng(Controller.getCurrentLocation(this)));

                break;

            case HOME_STOP_SELECTED:

                // HOME_STOP_SELECTED --> HOME

                // Remove place selected marker
                removeMarker(mPlaceSelectedMarker);

                // Set map padding
                setMapPadding(ActivityState.HOME);

                // Focus camera back to current location
                mMap.animateCamera(CameraUpdateFactory.newLatLng(Controller.getCurrentLocation(this)));

                // Remove the transit stop info window fragment
                removeTransitStopInfoWindow();

                break;

            case NAVIGATION:

                // NAVIGATION --> TRIP_PLAN

                // Re-set-up the "start navigation" button (fab)
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mFab.setClickable(false);

                        // FAB becomes clickable again as the "exit navigation mode" button
                        // after the new onClickListener is set in call to goToNextScreen()

                        // Launch navigation only if the trip origin matches the current location
                        LatLng tripStartLocation = mOrigin.getLocation();
                        LatLng myLocation = Controller.getCurrentLocation(MainActivity.this);

                        if (tripStartLocation == null
                                || Math.abs(tripStartLocation.latitude - myLocation.latitude)
                                > LOCATION_RANGE
                                || Math.abs(tripStartLocation.longitude - myLocation.longitude)
                                > LOCATION_RANGE) {
                            Toast.makeText(MainActivity.this,
                                    "Cannot launch navigation mode for trip " +
                                            "that does not begin at the current location",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            goToNextScreen(ActivityState.NAVIGATION);
                        }
                    }
                });

                mFab.setBackground(getDrawable(R.drawable.circle_colored));
                mFab.setImageDrawable(getDrawable(R.drawable.ic_navigation_white_24dp));

                // Show the appropriate arrow buttons
                showArrowButtons();

                // Show the sliding panel
                mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                // Remove step & bus stop markers and clear lists
                for (Marker marker : mNavigationMarkerList)
                    marker.remove();
                mNavigationMarkerList.clear();

                // Change map padding back
                setMapPadding(ActivityState.TRIP_PLAN);

                // Change map zoom & tilt
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder())
                        .tilt(0)
                        .target(mMap.getCameraPosition().target)
                        .zoom(mMap.getCameraPosition().zoom)
                        .build())
                );
                zoomMapToFitPolylines();

                // Re-enable my location button
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                // Resume low-battery consumption location updates
                Controller.startLowAccuracyLocationUpdates(this);

                // Remove sensor listener
                mSensorManager.unregisterListener(this);

                // Show detailed search bar
                super.onBackPressed();

                break;

            default:
                super.onBackPressed();
        }
    }

    /**
     * Calls planTrip(origin, destination, intermediateStops, time, departOrArriveBy),
     * passing null, null, and false as the last three parameters
     * @param origin
     * @param destination
     * @return
     */
    public boolean planTrip(@NonNull TripPlanPlace origin,
                            @NonNull TripPlanPlace destination) {
        return planTrip(origin, destination, null, null, false);
    }

    /**
     * Calls planTrip(origin, destination, intermediateStops, time, departOrArriveBy),
     * passing null and false as the last two parameters
     * @param origin
     * @param destination
     * @param intermediateStops
     * @return
     */
    public boolean planTrip(@NonNull TripPlanPlace origin,
                            @NonNull TripPlanPlace destination,
                            @NonNull List<TripPlanPlace> intermediateStops) {
        return planTrip(origin, destination, intermediateStops, null, false);
    }

    /**
     * Calls planTrip(origin, destination, intermediateStops, time, departOrArriveBy),
     * passing null as the middle parameter
     * @param origin
     * @param destination
     * @param date
     * @param departOrArriveBy
     * @return
     */
    public boolean planTrip(@NonNull TripPlanPlace origin,
                            @NonNull TripPlanPlace destination,
                            @NonNull Date date, boolean departOrArriveBy) {
        return planTrip(origin, destination, null, date, departOrArriveBy);
    }

    /**
     * Makes a request to the trip planner server for a list of itineraries from
     * mOrigin to mDestination; invokes displayItinerary() on the first itinerary
     *
     * @param origin point of origin for the trip
     * @param destination destination for the trip
     * @param intermediateStops use null for none
     * @param time time by which the trip should depart or arrive by; use null for current time
     * @param departOrArriveBy false for depart time, true for arrive time
     * @return true if the request was successfully made, false otherwise
     */
    public boolean planTrip(@NonNull final TripPlanPlace origin,
                            @NonNull final TripPlanPlace destination,
                            @Nullable List<TripPlanPlace> intermediateStops,
                            @Nullable Date time, boolean departOrArriveBy) {

        // Set up the trip plan screen
        if (getState() != ActivityState.TRIP_PLAN)
            goToNextScreen(ActivityState.TRIP_PLAN);

        // Save origin and destination
        if (origin.shouldUseCurrentLocation())
            origin.setLocation(Controller.getCurrentLocation(this));
        if (destination.shouldUseCurrentLocation())
            destination.setLocation(Controller.getCurrentLocation(this));
        mOrigin = origin;
        mDestination = destination;

        // Add the trip plan attempt to search history database
        Controller.addToSearchHistoryDatabase(this, mOrigin.getName(), mDestination.getName(),
                mOrigin.getLocation(), mDestination.getLocation(),
                Controller.getSelectedModesString(), (new Date()).getTime());

        // Hide the arrow buttons and the start navigation button
        hideArrowButtons();
        mFab.setVisibility(View.GONE);

        // Remove previous markers from the map
        removeMarker(mDestinationMarker);
//        removeMarker(mOriginMarker);

        // Remove previous itinerary polylines from the map
        if (mPolylineList != null) {
            for (Polyline polyline : mPolylineList)
                polyline.remove();
            mPolylineList = null;
        }

        // Clear sliding panel head and tail
        mSlidingPanelHead.removeAllViews();
        mSlidingPanelTail.removeAllViews();
        mSlidingPanelHead.setOnTouchListener(null);
        mSlidingPanelHead.setOnTouchListener(null);

        // CHECK IF APPROPRIATE TO PLAN TRIP:

        // If no modes are selected, prompt user to choose a mode
        if (Controller.getNumSelectedModes() == 0) {
            Toast.makeText(this, "Please select at least one mode of transportation",
                    Toast.LENGTH_SHORT).show();
            removeMarker(mPlaceSelectedMarker);
            return false;
        }

        // If public transport is selected, ensure that walk or bicycle or car is also selected
        Set<TraverseMode> selectedModes = Controller.getSelectedModes();
        if (selectedModes.contains(TraverseMode.BUS)
                && !selectedModes.contains(TraverseMode.WALK)
                && !selectedModes.contains(TraverseMode.BICYCLE)
                && !selectedModes.contains(TraverseMode.CAR)) {
            Toast.makeText(this, "Please select at least one of walk, bike, or car modes",
                    Toast.LENGTH_SHORT).show();
            removeMarker(mPlaceSelectedMarker);
            return false;
        }

        // PLAN TRIP:

        // Get depart/arrive-by date & time
        if (time == null) // Get current time if a time was not provided
            time = new Date();

        // Generate & display depart/arrive-by time
        String displayableTimeString = new SimpleDateFormat("hh:mm a", Locale.US).format(time);
        if (displayableTimeString.charAt(0) == '0')
            displayableTimeString = displayableTimeString.substring(1);

        String departOrArrive = departOrArriveBy ? getString(R.string.arrive)
                : getString(R.string.depart);
        mDetailedSearchBarFragment
                .setDepartArriveTimeText(departOrArrive + " " + displayableTimeString);

        // Reset MyLocation button functionality
        resetMyLocationButton();

        // Display loading text on sliding panel head
        showSlidingPanelHeadMessage("LOADING RESULTS...");

        // Clear sliding panel tail
        mSlidingPanelTail.removeAllViews();

        // Create latlng list of intermediate stops
        List<LatLng> latLngList = new LinkedList<>();
        if (intermediateStops != null) {
            for (TripPlanPlace place : intermediateStops)
                latLngList.add(place.getLocation());
        }

        // Send trip plan request
        Controller.requestTripPlan(this, origin.getLocation(), destination.getLocation(),
                latLngList, time, departOrArriveBy);
        // Will invoke updateUIonTripPlanResponse() or updateUIonTripPlanFailure()

        return true;
    }

    /**
     * Callback invoked upon receipt of response for a trip plan request
     * @param tripPlan
     */
    public void updateUIonTripPlanResponse(TripPlan tripPlan) {

        // Handle case where results are null or empty
        if (tripPlan == null
                || tripPlan.getItineraries() == null
                || tripPlan.getItineraries().isEmpty()) {
            Log.d(TAG, "OTP request result was empty");
            showSlidingPanelHeadMessage("No results");
            hideArrowButtons();
            mFab.setVisibility(View.GONE);
            return;
        }

        // Update local list of trip plan itineraries
        mItineraryList = tripPlan.getItineraries();

        // Initialize the highlighted tab row to indicate that the 1st itinerary is selected
        mTabRowLayout = new LinearLayout(MainActivity.this);
        mTabRowLayout.setOrientation(LinearLayout.HORIZONTAL);
        mTabRowLayout.setLayoutParams(new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (.1 * mSlidingPanelHead.getHeight())));
        for (int i = 0; i < mItineraryList.size(); ++i){
            View view = new View(MainActivity.this);
            if (i == 0) // Highlight the 1st tab
                view.setBackground(getDrawable(R.drawable.rectangle_selected));
            else view.setBackground(getDrawable(R.drawable.rectangle_unselected));
            view.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            mTabRowLayout.addView(view, i);
        }

        // Show highlighted tab bar on sliding panel handle
        mSlidingPanelHead.addView(mTabRowLayout, 0);

        // Display origin and destination markers
        removeMarker(mPlaceSelectedMarker);
        mDestinationMarker = mMap.addMarker(new MarkerOptions()
                .title(mDestination.getName())
                .position(mDestination.getLocation()));
//                mOriginMarker = mMap.addMarker(new MarkerOptions()
//                        .title(mOrigin.getName())
//                        .position(mOrigin.getLocation()));

        // Get the first itinerary in the results & display it
        displayItinerary(0,
                mOrigin.getLocation(), mDestination.getLocation(),
                android.R.anim.slide_in_left);
    }

    /**
     * Callback invoked upon failure of a trip plan request
     */
    public void updateUIonTripPlanFailure() {
        // Display "Request failed" on the sliding panel head
        showSlidingPanelHeadMessage("Request failed");

        // Move the camera to include just the origin and destination
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder()
                .include(mOrigin.getLocation())
                .include(mDestination.getLocation())
                .build(), PixelUtil.pxFromDp(this, 30))
        );

        // Hide selected place marker
        removeMarker(mPlaceSelectedMarker);
    }


    /**
     * Displays an itinerary in polyline on the map and in graphical depiction
     * on the sliding panel layout head and tail.
     * Animates the entrance of the sliding panel contents.
     * Repositions map camera to fit the polyline path if it is out of frame,
     * or if repositionCameraUncondiationally is set to true.
     * This method does not reset the destination marker; that should have been
     * done in planTrip().
     *
     * pre: Activity is in state TRIP_PLAN
     *
     */
    public void displayItinerary(int itineraryIndex, LatLng origin, LatLng destination,
                                 int animationId) {

        Log.d(TAG, "Displaying itinerary");
        long time = System.currentTimeMillis();
        Log.d(TAG, "Sliding panel state: " + mSlidingPanelLayout.getPanelState());

        mCurItineraryIndex = itineraryIndex;

        Itinerary itinerary = mItineraryList.get(itineraryIndex);

        // Remove previous itinerary summary layout
        mSlidingPanelHead.removeView(mSlidingPanelHead.getChildAt(1));

        // Clear sliding panel tail
        mSlidingPanelTail.removeAllViews();

        // Remove polyline of previous itinerary if it exists
        if (mPolylineList != null) {
            for (Polyline polyline : mPolylineList)
                polyline.remove();
            mPolylineList = null;
        }

        if (itinerary == null) {
            Log.d(TAG, "Itinerary is null; failed to display");
            return;
        }

        // Initialize list of points along itinerary
        mItineraryPointList = new LinkedList<>();

        // Initialize list of markers along itinerary
        mNavigationMarkerList = new LinkedList<>();

        // Get the legs of the itinerary and create a list for the corresponding polylines
        List<Leg> legList= itinerary.getLegs();
        List<Polyline> polylineList = new LinkedList<>();

        // Display each leg as a custom view in the itinerary summary and as a polyline on the map
        LinearLayout itinerarySummaryLegsLayout = new LinearLayout(this);
        itinerarySummaryLegsLayout.setPadding(30,0,30,0);
        itinerarySummaryLegsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        int index = 0;
        for (Leg leg : legList) {

            // Get a list of the points that make up the leg
            List<LatLng> points = PolyUtil.decode(leg.getLegGeometry().getPoints());

            // Add those points to the list of points that make up the itinerary
            mItineraryPointList.addAll(points);

            // Create a polyline options object for the leg
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(points)
                    .width(PixelUtil.pxFromDp(this, POLYLINE_WIDTH));

            // Create a new custom view representing this leg of the itinerary
            int paddingBetweenModeIconAndDurationText =
                    (leg.getMode().equals(TraverseMode.BICYCLE.toString())) ? 13 : 0;

            ItineraryLegIconView legIconView = new ItineraryLegIconView(this,
                    paddingBetweenModeIconAndDurationText);

            // Configure the polyline and custom view based on the mode of the leg
            Drawable d = ModeUtil.getDrawableFromString(leg.getMode());
            d.setAlpha(DARK_OPACITY);
            legIconView.setIcon(d);

            switch (leg.getMode()) {
                case ("WALK"):
                    polylineOptions
                            .color(getResources().getColor(R.color.colorPrimary, null))
                            .pattern(Arrays.asList(new Dot(), new Gap(10)));
                    legIconView.setLegDuration((int) Math.ceil(leg.getDuration()/60));
                    break;
                case ("BICYCLE"):
                    polylineOptions
                            .color(getResources().getColor(R.color.colorPrimary, null))
                            .pattern(Arrays.asList(new Dash(30), new Gap(10)));
                    legIconView.setLegDuration((int) Math.ceil(leg.getDuration()/60));
                    break;
                case ("CAR"):
                    polylineOptions.color(getResources().getColor(R.color.colorPrimary, null));

                    break;
                case ("BUS"):
                    polylineOptions.color(Color.parseColor("#" + leg.getRouteColor()));
                    legIconView.setRouteName(leg.getRoute());
                    legIconView.setRouteColor(Color.parseColor("#" + leg.getRouteColor()));
                    legIconView.setRouteNameColor(Color.WHITE);
                    legIconView.setShowRoute(true);
                    break;
                default: polylineOptions.color(Color.GRAY);
            }

            // Draw the polyline leg to the map and save it to the list
            polylineList.add(mMap.addPolyline(polylineOptions));

            // Add chevron icon to the itinerary summary legs layout
            // (except in front of the 1st leg icon view)
            if (index!= 0) {
                ImageView arrow = new ImageView(this);
                arrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
                arrow.setScaleType(ImageView.ScaleType.FIT_CENTER);
                arrow.setAlpha(DARK_OPACITY_PERCENTAGE);
                itinerarySummaryLegsLayout.addView(arrow, index,
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                ++index;
            }

            // Add leg icon view to the itinerary summary legs layout
            itinerarySummaryLegsLayout.addView(legIconView, index,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            ++index;

        }

        // Crete layout for itinerary summary (leg icons and duration)
        LinearLayout itinerarySummaryLayout = new LinearLayout(this);

        // Add itinerary summary legs layout to the itinerary summary layout
        itinerarySummaryLayout.addView(itinerarySummaryLegsLayout, new LinearLayout
                .LayoutParams(mSlidingPanelHead.getWidth() - 230,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Add view showing the itinerary duration to the itinerary summary layout
        TextView duration = new TextView(this);
        duration.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        duration.setTextColor(Color.BLACK);
        duration.setHorizontallyScrolling(false);
        duration.setAlpha(DARK_OPACITY_PERCENTAGE);
        duration.setTextSize(13);
        duration.setText(getDurationString(itinerary.getDuration()));
        itinerarySummaryLayout.addView(duration, new LinearLayout
                .LayoutParams(230, ViewGroup.LayoutParams.MATCH_PARENT));

        // Add itinerary summary layout to sliding panel head
        itinerarySummaryLayout.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (.9 * mSlidingPanelHead.getHeight())
        ));
        mSlidingPanelHead.addView(itinerarySummaryLayout, 1);


        // Create expanded itinerary view
        ExpandedItineraryView expandedItineraryView = new ExpandedItineraryView(this);
        expandedItineraryView.setPadding(0,50,0,150);

        // Assign location and name of the 1st and last points in the itinerary
        // So that the expanded itinerary view display the correct info
        if (legList.size() != 0) {
            legList.get(0).setFrom(new edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Place(origin.latitude, origin.longitude,
                    mDetailedSearchBarFragment.getOriginText()));
            legList.get(legList.size() - 1).setTo(new edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Place(destination.latitude, destination.longitude,
                    mDetailedSearchBarFragment.getDestinationText()));
        }
        expandedItineraryView.setItinerary(itinerary);
        mSlidingPanelTail.addView(expandedItineraryView, new ScrollView
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Animate the sliding panel components into view
        mSlidingPanelHead.getChildAt(1)
                .startAnimation(AnimationUtils.loadAnimation(this, animationId));
        mSlidingPanelTail.startAnimation(AnimationUtils.loadAnimation(this, animationId));

        // Save the list of polylines drawn on the map
        mPolylineList =  polylineList;

        // Reconfigure map camera & My Location button
        zoomMapToFitPolylines();
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                zoomMapToFitPolylines();
                return true;
            }
        });

        // Show the appropriate arrow buttons
        showArrowButtons();
        // Activate the "start navigation" button
        mFab.setVisibility(View.VISIBLE);
        mFab.setClickable(true);

        // Set up on swipe listeners for the sliding panel
        mSlidingPanelHead.setOnTouchListener(new SlidingPanelHeadOnSwipeTouchListener(this, this));
        mSlidingPanelTail.setOnTouchListener(new SlidingPanelTailOnSwipeTouchListener(this, this));

        Log.d(TAG, "Done displaying itinerary. Time: " + (System.currentTimeMillis() - time));
        Log.d(TAG, "Sliding panel state: " + mSlidingPanelLayout.getPanelState());

    }

    public void updateUIOnLocationChanged(Location location) {
        if (getState() == ActivityState.NAVIGATION)
            updateNavigationModeCamera();
    }


    /**
     * Helper method to remove the transit stop info window from the HOME_STOP_SELECTED screen
     */
    private void removeTransitStopInfoWindow() {
        // Animate exit of transit stop info window & remove
        Animation exit = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
        exit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .remove(mTransitStopInfoWindowFragment)
                        .commit();
                mTransitStopInfoWindowFragment = null;
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mTransitStopInfoWindowFragment.getView().startAnimation(exit);
    }

    /**
     * Helper method to remove the simple search bar
     */
    private void removeSimpleSearchBar() {
        if (mSimpleSearchBar.getVisibility() != View.GONE) {
            Animation hide = AnimationUtils.loadAnimation(this, R.anim.slide_out_up);
            hide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    mSimpleSearchBar.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            mSimpleSearchBar.startAnimation(hide);
        }
    }


    /**
     * Helper method that launches the google places autocomplete search widget
     * Will invoke onActivityResult when the user selects a place
     */
    public void launchGooglePlacesSearchWidget(SearchFieldId id) {

        // Record which search bar was clicked
        setLastEditedSearchBar(id);

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(getBoundsBias())
                            .build(MainActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE); // invokes onActivityResult()
        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "Error launching PlaceAutocomplete intent");
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult invoked");

        // The user has selected a place from the google places autocomplete search widget
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Log.d(TAG, "Place Autocomplete request result received");

            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Result is ok");

                // Interrupt any ongoing trip plan request
                Controller.interruptOngoingTripPlanRequests();

                // Get the place selected
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.d(TAG, "Place selected: " + place.getName());

                TripPlanPlace tripPlanPlace = new TripPlanPlace(place.getName(), place.getLatLng(),
                        place.getAddress());

                // Make updates according to which search bar was edited
                if (lastEditedSearchBar == SearchFieldId.SIMPLE) {

                    if (getState() == ActivityState.HOME_STOP_SELECTED) {
                        ArrayList<TripPlanPlace> intermediateStops = new ArrayList<>();
                        intermediateStops.add(0, new TripPlanPlace(mPlaceSelectedMarker.getTitle(),
                                mPlaceSelectedMarker.getPosition()));
                        planTrip(new TripPlanPlace(), tripPlanPlace, intermediateStops);
                    } else {
                        planTrip(new TripPlanPlace(), tripPlanPlace);
                    }

                } else if (lastEditedSearchBar == SearchFieldId.DETAILED_FROM) {

                    // Set the text in the detailed from search bar
                    mDetailedSearchBarFragment.setOriginText(place.getName());
                    planTrip(tripPlanPlace, mDestination);

                } else if (lastEditedSearchBar == SearchFieldId.DETAILED_TO) {

                    // Set the text in the detailed to search bar
                    mDetailedSearchBarFragment.setDestinationText(place.getName());
                    planTrip(mOrigin, tripPlanPlace);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());

            }
        }
    }

    /**
     * Helper method to configure the directions button and show info about a location in
     * the sliding panel head, when a location is clicked on the map
     * @param place
     * @pre the activity is in HOME_PLACE_SELECTED state
     */
    private void selectPlaceOnMap(final TripPlanPlace place) {

        // Show marker
        removeMarker(mPlaceSelectedMarker);
        mPlaceSelectedMarker = mMap.addMarker(new MarkerOptions()
                .position(place.getLocation())
                .title(place.getName())
        );

        // Set up "directions" button (fab)
        mFab.setImageDrawable(getDrawable(R.drawable.ic_directions_white_24dp));
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFab.setClickable(false);

                // FAB becomes clickable again in the form of the "start navigation" button
                // after the itineraries of the trip plan are acquired

                planTrip(new TripPlanPlace(), place);
            }
        });
        hideArrowButtons();
        mFab.setVisibility(View.VISIBLE);

        // Name text view
        TextView placeNameText = new TextView(this);
        placeNameText.setId(R.id.place_name_text_view);
        placeNameText.setHorizontallyScrolling(true);
        placeNameText.setEllipsize(TextUtils.TruncateAt.END);
        placeNameText.setGravity(Gravity.BOTTOM);
        placeNameText.setText(place.getName());
        placeNameText.setTextSize(18);
        placeNameText.setPadding(40,0,40,0);
        placeNameText.setTextColor(Color.BLACK);
        placeNameText.setAlpha(DARK_OPACITY_PERCENTAGE);

        // Address text view
        TextView placeAddressText = new TextView(this);
        placeAddressText.setHorizontallyScrolling(true);
        placeAddressText.setEllipsize(TextUtils.TruncateAt.END);
        placeAddressText.setGravity(Gravity.TOP);
        placeAddressText.setText(place.getAddress());
        placeAddressText.setPadding(40,0,40,0);
        placeAddressText.setTextSize(12);
        placeAddressText.setMaxLines(1);

        // Show on sliding panel head
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        mSlidingPanelHead.addView(placeNameText, layoutParams);
        mSlidingPanelHead.addView(placeAddressText, layoutParams);
        mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

    }

    /**
     * Get the activity's current screen
     * @return
     */
    public ActivityState getState() { return mStack.peek();}

    /**
     * Get and pop the activity's current screen off the back stack
     * @return
     */
    public ActivityState removeState() {
        return mStack.pop();
    }

    /**
     * Set the current screen of the activity by pushing it on the back stack
     * @param state
     */
    public void setState(ActivityState state) {
        mStack.push(state);
    }

    /**
     * Helper method to convert a drawable to a bitmap
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Helper method to generate a speech balloon bitmap for a navigation instruction
     * @return
     */
    private Bitmap createWalkStepBitmap(String instruction, boolean left) {

        Paint paint = new Paint();
        TextPaint textPaint = new TextPaint();
        Rect textDimensions = new Rect();
        Rect roundedRectDimensions = new Rect();

        paint.setColor(getColor(R.color.colorPrimary));

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(PixelUtil.pxFromDp(this, 12));
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.getTextBounds(instruction,0,instruction.length(),textDimensions);

        int TEXT_PADDING = PixelUtil.pxFromDp(this, 5);
        int BALLOON_TAIL_SIZE = PixelUtil.pxFromDp(this, 10);
        float ROUNDED_RECT_CORNER_RADIUS = PixelUtil.pxFromDp(this, 2);

        roundedRectDimensions.set(0,0,textDimensions.width() + 2 * TEXT_PADDING,
                textDimensions.height() + 2 * TEXT_PADDING);

        int w = roundedRectDimensions.width() + BALLOON_TAIL_SIZE;
        int h = roundedRectDimensions.height() + BALLOON_TAIL_SIZE;

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        if (left) {
            Path tail = new Path();
            tail.setFillType(Path.FillType.EVEN_ODD);
            tail.moveTo(w, h);
            tail.lineTo(roundedRectDimensions.width(),
                    roundedRectDimensions.height() - ROUNDED_RECT_CORNER_RADIUS);
            tail.lineTo(roundedRectDimensions.width() - ROUNDED_RECT_CORNER_RADIUS,
                    roundedRectDimensions.height());
            tail.close();

            canvas.drawRoundRect(0f, 0f, (float)roundedRectDimensions.width(),
                    (float)(roundedRectDimensions.height()),
                    ROUNDED_RECT_CORNER_RADIUS, ROUNDED_RECT_CORNER_RADIUS, paint);
            canvas.drawPath(tail, paint);
            canvas.drawText(instruction, TEXT_PADDING,
                    TEXT_PADDING + textDimensions.height(), textPaint);

        } else {
            Path tail = new Path();
            tail.setFillType(Path.FillType.EVEN_ODD);
            tail.moveTo(0, h);
            tail.lineTo(BALLOON_TAIL_SIZE,
                    roundedRectDimensions.height() - ROUNDED_RECT_CORNER_RADIUS);
            tail.lineTo(BALLOON_TAIL_SIZE + ROUNDED_RECT_CORNER_RADIUS,
                    roundedRectDimensions.height());
            tail.close();

            canvas.drawRoundRect(BALLOON_TAIL_SIZE, 0f,
                    (float)(BALLOON_TAIL_SIZE + roundedRectDimensions.width()),
                    (float)(roundedRectDimensions.height()),
                    ROUNDED_RECT_CORNER_RADIUS, ROUNDED_RECT_CORNER_RADIUS, paint);
            canvas.drawPath(tail, paint);
            canvas.drawText(instruction, BALLOON_TAIL_SIZE + TEXT_PADDING,
                    TEXT_PADDING + textDimensions.height(), textPaint);
        }

        return bmp;
    }


    /**
     * Helper method to initialize the mode buttons in the detailed search bar
     * Called from the onCreateView method of the DetailedSearchBar fragment
     * pre: mode to image button bimap has already been set up
     */
    public void initializeModeButtons() {

        // Get the current selected modes
        Set<TraverseMode> selectedModes = Controller.getSelectedModes();

        // Loop through the TraverseMode-ImageButtonId bimap
        for (Map.Entry<TraverseMode,ImageButton> entry: modeToImageButtonBiMap.entrySet()) {

            final TraverseMode traverseMode = entry.getKey();
            ImageButton button = entry.getValue();

            // Initialize each button as selected or deselected
            if (selectedModes.contains(traverseMode))
                selectModeButton(button);
            else
                deselectModeButton(button);

            // Set on click listener for each button
            if (entry.getKey() == TraverseMode.BICYCLE) // TODO un-omit bike mode when available
                button.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageButton button = (ImageButton) v;
                                Toast.makeText(MainActivity.this,
                                        "Bike mode not yet available", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            else {

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Controller.interruptOngoingTripPlanRequests();

                        ImageButton button = (ImageButton) v;

                        if (button.isSelected())
                            deselectModeButton(button);
                        else
                            selectModeButton(button);

                        // Refresh the trip plan
                        planTrip(mOrigin, mDestination);
                    }
                });

                // Set on long click listener for each button
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Controller.interruptOngoingTripPlanRequests();

                        // Vibrate
                        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vib.vibrate(30);

                        TraverseMode buttonMode = modeToImageButtonBiMap.inverse().get(v);

                        // Select as normal if already selected as first, else select as first
                        if (buttonMode == Controller.getFirstMode()) {
                            Controller.removeFirstMode();
                            selectModeButton((ImageButton) v);
                        } else {
                            selectModeButtonAsFirstMode((ImageButton) v);
                        }

                        // Refresh the trip plan
                        planTrip(mOrigin, mDestination);

                        // Prevent invocation of onClick
                        return true;
                    }
                });
            }
        }
    }

    /**
     * Helper function for selecting a mode button
     */
    private void selectModeButton(ImageButton button) {
        Log.d(TAG, "Mode button was selected");

        TraverseMode mode = modeToImageButtonBiMap.inverse().get(button);

        // Select button and add corresponding mode to list of selected modes
        button.setSelected(true);
        Controller.selectMode(mode);

        // Set color
        button.setBackgroundResource(R.drawable.rounded_rectangle_accent);
        button.setColorFilter(getResources().getColor(R.color.white, null));
    }

    /**
     * Helper function for deselecting a mode button
     */
    private void deselectModeButton(ImageButton button) {
        Log.d(TAG, "Mode button was deselected");

        TraverseMode buttonMode = modeToImageButtonBiMap.inverse().get(button);

        // Deselect button and remove corresponding mode from list of selected modes
        button.setSelected(false);
        Controller.deselectMode(buttonMode);

        // Set color
        button.setBackgroundResource(R.drawable.rounded_rectangle_primary);
        button.setColorFilter(Color.WHITE);
    }

    /**
     * Helper function for selecting a mode button as the first mode in the trip plan
     * @param button
     */
    private void selectModeButtonAsFirstMode(ImageButton button) {

        TraverseMode oldFirstMode = Controller.getFirstMode();
        TraverseMode newFirstMode = modeToImageButtonBiMap.inverse().get(button);

        // Set the old first mode button as selected the regular way
        if (oldFirstMode != null)
            selectModeButton(modeToImageButtonBiMap.get(oldFirstMode));

        // Select the given button as the first mode button
        button.setSelected(true);
        Controller.setFirstMode(newFirstMode);

        // Set color
        button.setBackgroundResource(R.drawable.rounded_rectangle_white);
        button.setColorFilter(getResources().getColor(R.color.colorPrimary, null));
    }


    private void zoomMapToFitPolylines() {

        if (!mPolylineList.isEmpty()) {
            LatLngBounds bounds = calculateLatLngPolylineBounds();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    bounds, PixelUtil.pxFromDp(this, 30)));
        }
    }

    private LatLngBounds calculateLatLngPolylineBounds() {
        if (!mPolylineList.isEmpty()) {

            LatLng origin = mOrigin.getLocation();
            LatLng destination = mDestination.getLocation();

            // Find the topmost, bottommost, leftmost, and rightmost points in all the PolyLines
            LatLng top = (origin.latitude >= destination.latitude) ? origin : destination;
            LatLng bottom = (origin.latitude <= destination.latitude) ? origin : destination;
            LatLng right = (origin.longitude >= destination.longitude) ? origin : destination;
            LatLng left = (origin.longitude <= destination.longitude) ? origin : destination;

            for (Polyline polyline : mPolylineList) {
                for (LatLng point : polyline.getPoints()) {
                    top = (top.latitude >= point.latitude) ? top : point;
                    bottom = (bottom.latitude <= point.latitude) ? bottom : point;
                    right = (right.longitude >= point.longitude) ? right : point;
                    left = (left.longitude <= point.longitude) ? left : point;
                }
            }

            double dy = top.latitude - bottom.latitude;
            top = new LatLng(top.latitude + .125 * dy, top.longitude);

            return new LatLngBounds.Builder()
                    .include(top)
                    .include(bottom)
                    .include(left)
                    .include(right)
                    .build();

        }

        throw new RuntimeException("Polyline list is empty");
    }

    /**
     * Helper method that resets the functionality of the map's My Location button
     * to centering the camera on the current location
     *
     * pre: mMap and location services are set up and working
     */
    private void resetMyLocationButton() {
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.animateCamera(CameraUpdateFactory
                        .newLatLng(Controller.getCurrentLocation(MainActivity.this)));
                return false;
            }
        });
    }

    /**
     * Helper method that displays a message on the sliding panel head
     *
     * pre: mSlidingPanelHead has been initialized
     */
    private void showSlidingPanelHeadMessage(String message) {
        TextView textView = new TextView(MainActivity.this);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(15);
        textView.setText(message);
        mSlidingPanelHead.removeAllViews();
        mSlidingPanelHead.addView(textView,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
    }

    /**
     * Helper function that returns a string representing time in terms of
     * days, hours, and minutes, or seconds if shorter than a single minute
     * @param seconds
     * @return
     */
    public static String getDurationString(double seconds) {

        long totalMins = (long) Math.ceil(seconds/60.0);

        long totalHours = totalMins/60;
        long remainderMins = totalMins%60;

        long days = totalHours/24;
        long remainderHours = totalHours%24;

        String duration = "";

        if (days == 1)
            duration += (days + " day ");
        else if (days != 0)
            duration += (days + " days ");
        if (remainderHours != 0)
            duration += (remainderHours + " h ");
        if (remainderMins != 0)
            duration += (remainderMins + " m ");

        // Only display seconds if total time is less than 1 minute
        if (duration.equals(""))
            duration = seconds + " sec ";

        // Slice off the extra space at the end
        duration = duration.substring(0, duration.length() - 1);

        return duration;
    }

    /**
     *  Helper function to generate latitude and longitude bounds to bias the results of a Google
     *  Places autocomplete prediction to a 20-mile-wide square centered at the current location
     *  If the current location is unavailable, returns bounds encompassing the whole globe
     */
    public LatLngBounds getBoundsBias() {

        // Get current location
        LatLng location = Controller.getCurrentLocation(this);

        if (location != null) {
            // Return bounds for a 20-mile-wide square centered at the current location
            double latitude = location.latitude;
            double longitude = location.longitude;
            return new LatLngBounds(new LatLng(latitude - .145, longitude - .145),
                    new LatLng(latitude + .145, longitude - .145));

        } else {
            // If we cannot access location, return bounds for the whole globe
            return new LatLngBounds(new LatLng(-90,-180), new LatLng(90, 180));
        }

    }

    public void onSwipeSlidingPanelLeft() {

        Log.d(TAG, "Handling swipe left");

        // Do nothing if we are already displaying the last itinerary
        if (mItineraryList == null || mCurItineraryIndex == mItineraryList.size() - 1)
            return;
        if (mSlidingPanelHead == null || mSlidingPanelTail == null)
            return;

        ++mCurItineraryIndex;
        Animation slideOutLeft = AnimationUtils
                .loadAnimation(this, R.anim.slide_out_left);
        slideOutLeft.setAnimationListener(new SwipeLeftAnimationListener());
        mSlidingPanelHead.getChildAt(1).startAnimation(slideOutLeft);
        mSlidingPanelTail.startAnimation(slideOutLeft);

    }

    private class SwipeLeftAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            // Update highlighted tab layout
            mTabRowLayout.getChildAt(mCurItineraryIndex)
                    .setBackground(getDrawable(R.drawable.rectangle_selected));
            mTabRowLayout.getChildAt(mCurItineraryIndex - 1)
                    .setBackground(getDrawable(R.drawable.rectangle_unselected));
            // Display next itinerary
            displayItinerary(mCurItineraryIndex,
                    mOrigin.getLocation(), mDestination.getLocation(), R.anim.slide_in_right);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }

    public void onSwipeSlidingPanelRight() {

        Log.d(TAG, "Handling swipe right");

        // Do nothing if we are already displaying the first itinerary
        if (mItineraryList == null || mCurItineraryIndex == 0)
            return;
        if (mSlidingPanelHead == null || mSlidingPanelTail == null)
            return;

        --mCurItineraryIndex;
        Animation slideOutRight = AnimationUtils
                .loadAnimation(this, R.anim.slide_out_right);
        slideOutRight.setAnimationListener(new SwipeRightAnimationListener());
        mSlidingPanelHead.getChildAt(1).startAnimation(slideOutRight);
        mSlidingPanelTail.startAnimation(slideOutRight);

    }

    private class SwipeRightAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            // Update highlighted tab layout
            mTabRowLayout.getChildAt(mCurItineraryIndex)
                    .setBackground(getDrawable(R.drawable.rectangle_selected));
            mTabRowLayout.getChildAt(mCurItineraryIndex + 1)
                    .setBackground(getDrawable(R.drawable.rectangle_unselected));
            // Display next itinerary
            displayItinerary(mCurItineraryIndex,
                    mOrigin.getLocation(), mDestination.getLocation(), R.anim.slide_in_left);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }

    public TripPlanPlace getmOrigin() {
        return mOrigin;
    }

    public TripPlanPlace getmDestination() {
        return mDestination;
    }

    public void addToModeButtonBiMap(TraverseMode mode, ImageButton button) {
        modeToImageButtonBiMap.forcePut(mode, button);
    }

    private void setLastEditedSearchBar(SearchFieldId id) {lastEditedSearchBar = id;}

    /**
     * Helper method to toggle the sliding panel between expanded and collapsed
     */
    public void toggleSlidingPanel() {
        if (mSlidingPanelLayout == null)
            throw new RuntimeException("Sliding panel layout reference is null");
        SlidingUpPanelLayout.PanelState panelState = mSlidingPanelLayout.getPanelState();

        if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED)
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED)
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    /**
     * Helper method to set the padding for the Google Map
     * @param state
     */
    public void setMapPadding(ActivityState state) {
        int a = PixelUtil.pxFromDp(this, 3);
        int b = PixelUtil.pxFromDp(this, 52);
        int c = PixelUtil.pxFromDp(this, 70);
        int d = PixelUtil.pxFromDp(this, 110);
        int e = PixelUtil.pxFromDp(this, 185);

        switch (state) {
            case HOME:
                mMap.setPadding(a,b,a,a);
                break;
            case HOME_PLACE_SELECTED:
                mMap.setPadding(a,a,a,c);
                break;
            case HOME_STOP_SELECTED:
                mMap.setPadding(a,b,a,d);
                break;
            case HOME_BUS_SELECTED:
                mMap.setPadding(a,a,a,a);
                break;
            case TRIP_PLAN:
                mMap.setPadding(a,e,a,c);
                break;
            case NAVIGATION:
                mMap.setPadding(a,a,a,a);
        }
    }

    /**
     * Helper method to register sensor listeners to detect device rotation
     */
    private void registerSensorListeners() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

    }

    private void hideLeftArrowButton() {
        if (mLeftArrowButton != null) {
            mLeftArrowButton.setClickable(false);
            mLeftArrowButton.setVisibility(View.INVISIBLE);
        }
    }

    private void showLeftArrowButton() {
        if (mLeftArrowButton != null) {
            mLeftArrowButton.setVisibility(View.VISIBLE);
            mLeftArrowButton.setClickable(true);
        }
    }

    private void hideRightArrowButton() {
        if (mRightArrowButton != null) {
            mRightArrowButton.setClickable(false);
            mRightArrowButton.setVisibility(View.INVISIBLE);
        }
    }

    private void showRightArrowButton() {
        if (mRightArrowButton != null) {
            mRightArrowButton.setVisibility(View.VISIBLE);
            mRightArrowButton.setClickable(true);
        }
    }

    /**
     * Hides both arrow buttons
     */
    private void hideArrowButtons() {
        hideLeftArrowButton();
        hideRightArrowButton();
    }

    /**
     * Shows the arrow buttons according to the current itinerary on screen
     */
    private void showArrowButtons() {

        // Only show in trip plan mode
        if (getState() != ActivityState.TRIP_PLAN)
            return;

        // Show the left arrow button if this is not the first itinerary
        if (mCurItineraryIndex != 0) {
            showLeftArrowButton();
        }
        // Show the right arrow button if this is not the last itinerary
        if (mCurItineraryIndex != mItineraryList.size() - 1) {
            showRightArrowButton();
        }
    }

    /**
     * Helper method that returns true if the parameter ActivityState is one of HOME,
     * HOME_PLACE_SELECTED, HOME_STOP_SELECTED, or HOME_BUS_SELECTED
     * @param state
     * @return
     */
    private boolean isAHomeState(ActivityState state) {
        return (state == ActivityState.HOME ||
                state == ActivityState.HOME_PLACE_SELECTED ||
                state == ActivityState.HOME_STOP_SELECTED ||
                state == ActivityState.HOME_BUS_SELECTED);
    }

     /**
     * Helper method to remove a marker from the map
     * @param marker
     */
    private void removeMarker(Marker marker) {
        if (marker != null) {
            marker.remove();
            marker = null;
        }
    }

    /**
     * Helper method to update the camera in navigation mode;
     * will move the camera to match the device's current location & orientation
     */
    private void updateNavigationModeCamera() {

        // Rotation matrix based on current readings from accelerometer and magnetometer.
        final float[] rotationMatrix = new float[9];

        SensorManager.getRotationMatrix(rotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // Express the updated rotation matrix as three orientation angles.
        final float[] orientationAngles = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder())
                .target(Controller.getCurrentLocation(this))
                .zoom(NAVIGATION_MODE_ZOOM_LEVEL)
                .tilt(NAVIGATION_MODE_TILT_LEVEL)
                .bearing((float)Math.toDegrees(orientationAngles[0]))
                .build())
        );
    }



    /**
     * Callback method invoked when user responds to a permissions request
     * made from the controller layer
     * Must be overridden & implemented in the main activity
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // This method MUST be called for Google Play Services to be properly set up
                LocationPermissionService
                        .handleLocationRequestPermissionsResult(this, grantResults);
                break;
        }
    }

}
