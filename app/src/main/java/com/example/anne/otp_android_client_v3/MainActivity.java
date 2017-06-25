package com.example.anne.otp_android_client_v3;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.anne.otp_android_client_v3.dictionary.ModeToDrawableDictionary;
import com.example.anne.otp_android_client_v3.custom_views.ExpandedItineraryView;
import com.example.anne.otp_android_client_v3.custom_views.ItineraryLegIconView;
import com.example.anne.otp_android_client_v3.dictionary.StringToModeDictionary;
import com.example.anne.otp_android_client_v3.fragments.DetailedSearchBarFragment;
import com.example.anne.otp_android_client_v3.fragments.TransitStopInfoWindowFragment;
import com.example.anne.otp_android_client_v3.listeners.SlidingPanelHeadOnSwipeTouchListener;
import com.example.anne.otp_android_client_v3.listeners.SlidingPanelTailOnSwipeTouchListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.common.collect.BiMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

import retrofit2.Call;
import retrofit2.Callback;
import vanderbilt.thub.otp.model.OTPPlanModel.GenericLocation;
import vanderbilt.thub.otp.model.OTPPlanModel.Itinerary;
import vanderbilt.thub.otp.model.OTPPlanModel.Leg;
import vanderbilt.thub.otp.model.OTPPlanModel.PlannerRequest;
import vanderbilt.thub.otp.model.OTPPlanModel.Response;
import vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode;
import vanderbilt.thub.otp.model.OTPPlanModel.WalkStep;
import vanderbilt.thub.otp.model.OTPStopsModel.Stop;
import vanderbilt.thub.otp.service.OTPPlanService;
import vanderbilt.thub.otp.service.OTPPlanSvcApi;
import vanderbilt.thub.otp.service.OTPStopsService;
import vanderbilt.thub.otp.service.OTPStopsSvcApi;

// TODO: Implement tab bar that shows which itinerary we are on
// TODO: Turn off sliding panel overlay

@SuppressWarnings("JavaDoc")
public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        SensorEventListener{

    public static final float DARK_OPACITY_PERCENTAGE = .70f;

    public static final float LIGHT_OPACITY_PERCENTAGE = .54f;

    public static final int DARK_OPACITY = (int) (DARK_OPACITY_PERCENTAGE * 255);

    public static final int LIGHT_OPACITY = (int) (LIGHT_OPACITY_PERCENTAGE * 255);

    public enum ActivityState {HOME, HOME_PLACE_SELECTED, HOME_STOP_SELECTED, HOME_BUS_SELECTED, TRIP_PLAN, NAVIGATION}

    public enum SearchBarId {SIMPLE, DETAILED_FROM, DETAILED_TO}

    private final String TAG = "MainActivity.java";

    private final double MY_CITY_LATITUDE = 36.165890;

    private final double MY_CITY_LONGITUDE = -86.784440;

    private final double MY_CITY_RADIUS = 48300;

    private final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private final int POLYLINE_WIDTH = 23;

    private final int LOCATION_INTERVAL = 5000;

    private static final float DEFAULT_ZOOM_LEVEL = 15;

    private static final float NAVIGATION_MODE_ZOOM_LEVEL = 18;

    private static final float NAVIGATION_MODE_TILT_LEVEL = 60;

    private static final float MIN_SHOW_MARKER_ZOOM_LEVEL = 17;

    private SensorManager mSensorManager;

    private List<Stop> mCityTransitStops;

    private Map<Marker, String> mCityTransitStopMarkers;

    private final float[] mAccelerometerReading = new float[3];

    private final float[] mMagnetometerReading = new float[3];

    private boolean updateCameraOnSensorChange = true;

    private GoogleMap mMap;

    private GoogleApiClient mGoogleAPIClient;

    private SlidingUpPanelLayout mSlidingPanelLayout;

    private LinearLayout mSlidingPanelHead;

    private ScrollView mSlidingPanelTail;

    private LinearLayout mNavButtonsLayout;

    private ImageButton mFab;

    private ImageButton mRightArrowButton;

    private ImageButton mLeftArrowButton;

    private ConcurrentLinkedDeque<ActivityState> mStateStack;

    private SearchBarId lastEditedSearchBar;

    private CardView mSimpleSearchBar;

    private TextView mSimpleSearchBarText;

    private TripPlanPlace mOrigin = null;

    private TripPlanPlace mDestination = null;

    private DetailedSearchBarFragment mDetailedSearchBarFragment;

    private TransitStopInfoWindowFragment mTransitStopInfoWindowFragment;

    private volatile LatLngBounds mMapBounds;

    private BiMap<TraverseMode, ImageButton> modeToImageButtonBiMap;

    private int mCurItineraryIndex;

    private List<Itinerary> mItineraryList;

    private List<Polyline> mPolylineList;

    private List<LatLng> mItineraryPointList; // todo probably change to list of lists

    private List<Marker> mNavigationMarkerList;

    private Marker mDestinationMarker;

//    private Marker mOriginMarker;

    private Marker mPlaceSelectedMarker;

    private volatile double mLastZoomLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Activity created");

        // Setup
        setUpMap();
        setUpDrawer();
        setUpSearch();
        setUpModes();
        setUpSlidingPanel();
        setUpNavigationButtons();
        setUpSensorManager();
        setUpOTPServices();
        ModeToDrawableDictionary.setup(this);

        // Initialize state
        mStateStack = new ConcurrentLinkedDeque<>();
        setState(ActivityState.HOME);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Callback that handles back button press
     */
    @Override
    public void onBackPressed() {

        Log.d(TAG, "Back pressed");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (mSlidingPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }

        Log.d(TAG, getState().toString());

        // Perform the corresponding necessary actions based on the current state
        switch (removeState()) {
            case TRIP_PLAN:

                // TRANSITIONING BACK FROM TRIP_PLAN SCREEN

                // Remove until back at home screen
                while (getState() != ActivityState.HOME)
                    removeState();

                // Remove origin & destination marker from the map
                if (mDestinationMarker != null)
                    mDestinationMarker.remove();
//                if (mOriginMarker != null)
//                    mOriginMarker.remove();

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
                mMap.moveCamera(CameraUpdateFactory.newLatLng(getCurrentCoordinates()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL));

                // Hide navigation buttons
                hideArrowButtons();
                mNavButtonsLayout.setVisibility(View.GONE);

                // Clear and hide sliding panel
                mSlidingPanelHead.removeAllViews();
                mSlidingPanelTail.removeAllViews();
                mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                // Show simple search bar
                mSimpleSearchBar.setVisibility(View.VISIBLE);
                mSimpleSearchBarText.setText(getResources().getText(R.string.where_to));

                // Remove detailed search bar fragment
                super.onBackPressed();

                break;

            case HOME_PLACE_SELECTED:

                // TRANSITIONING BACK FROM HOME_PLACE_SELECTED SCREEN

                // Remove place selected marker and set to null
                if (mPlaceSelectedMarker != null) {
                    mPlaceSelectedMarker.remove();
                    mPlaceSelectedMarker = null;
                }

                // Set map padding
                setMapPadding(ActivityState.HOME);

                // Hide & clear sliding panel
                mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                mSlidingPanelHead.removeAllViews();

                // Hide fab
                mNavButtonsLayout.setVisibility(View.INVISIBLE);

                // Revert simple search bar
                mSimpleSearchBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up));
                mSimpleSearchBar.setVisibility(View.VISIBLE);
                setMapPadding(ActivityState.HOME);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(getCurrentCoordinates()));

                // Focus camera back to current location
                mMap.animateCamera(CameraUpdateFactory.newLatLng(getCurrentCoordinates()));

                break;

            case HOME_STOP_SELECTED:

                // TRANSITIONING BACK FROM TRIP_PLAN SCREEN

                // Remove place selected marker and set to null
                if (mPlaceSelectedMarker != null) {
                    mPlaceSelectedMarker.remove();
                    mPlaceSelectedMarker = null;
                }

                // Set map padding
                setMapPadding(ActivityState.HOME);

                // Focus camera back to current location
                mMap.animateCamera(CameraUpdateFactory.newLatLng(getCurrentCoordinates()));

                // Remove the transit stop info window fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .remove(mTransitStopInfoWindowFragment)
                        .commit();

                break;

            case NAVIGATION:

                // TRANSITIONING BACK FROM NAVIGATION SCREEN

                // Re-set-up the "start navigation" button (fab)
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mFab.setClickable(false);

                        // FAB becomes clickable again as the "exit navigation mode" button
                        // after the new onClickListener is set

                        LatLng tripStartLocation = mOrigin.getLocation();
                        LatLng myLocation = getCurrentCoordinates();

                        if (tripStartLocation == null
                                || Math.abs(tripStartLocation.latitude - myLocation.latitude) > 0.0005
                                || Math.abs(tripStartLocation.longitude - myLocation.longitude) > 0.0005) {
                            Toast.makeText(MainActivity.this,
                                    "Cannot launch navigation mode for trip " +
                                            "that does not begin at the current location",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            transitionState(ActivityState.TRIP_PLAN, ActivityState.NAVIGATION);
                        }
                    }
                });

                mFab.setBackground(getDrawable(R.drawable.color_circle));
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
                configureZoomByPolylineBounds();

                // Re-enable my location button
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                // Resume low-battery consumption location updates
                startLocationUpdates(false);

                // Remove sensor listener
                mSensorManager.unregisterListener(this);

                // Show detailed search bar
                super.onBackPressed();

                updateCameraOnSensorChange = true;

                break;

            default:
                super.onBackPressed();
        }
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
                        if (id == R.id.nav_planner) {
                        } else if (id == R.id.nav_settings) {
                        }

                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });
    }

    /**
     * Helper method for setting up the PlaceAutocompleteFragment and simple search bar
     */
    private void setUpSearch() {
        mSimpleSearchBar = (CardView) findViewById(R.id.simple_search_bar_card_view);
        mSimpleSearchBarText = (TextView) findViewById(R.id.simple_search_bar_text_view);
        mSimpleSearchBarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGooglePlacesSearchWidget(SearchBarId.SIMPLE);
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
     * Helper method that launches the google places autocomplete search widget
     * Will invoke onActivityResult when the user selects a place
     */
    public void launchGooglePlacesSearchWidget(SearchBarId id) {

        // Record which search bar was clicked
        setLastEditedSearchBar(id);

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(getBoundsBias())
                            .build(MainActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "Error launching PlaceAutocomplete intent");
        }
    }

    /**
     * Invoked when when the user selects a place from the google places widget
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult invoked");

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Log.d(TAG, "Place Autocomplete request result received");

            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Result is ok");

                // Get the place selected
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.d(TAG, "Place selected: " + place.getName());

                TripPlanPlace tripPlanPlace = new TripPlanPlace(place.getName(), place.getLatLng(),
                        place.getAddress());

                // Make updates according to which search bar was edited
                if (lastEditedSearchBar == SearchBarId.SIMPLE) {

                    transitionState(getState(), ActivityState.TRIP_PLAN);

                    if (getState() == ActivityState.HOME_STOP_SELECTED) {
                        ArrayList<TripPlanPlace> intermediateStops = new ArrayList<>();
                        intermediateStops.add(0, new TripPlanPlace(mPlaceSelectedMarker.getTitle(),
                                mPlaceSelectedMarker.getPosition()));
                        planTrip(new TripPlanPlace(), tripPlanPlace, intermediateStops);
                    } else {
                        planTrip(new TripPlanPlace(), tripPlanPlace);
                    }

                } else if (lastEditedSearchBar == SearchBarId.DETAILED_FROM) {

                    // Set the text in the detailed from search bar
                    mDetailedSearchBarFragment.setOriginText(place.getName());
                    planTrip(tripPlanPlace, mDestination);

                } else if (lastEditedSearchBar == SearchBarId.DETAILED_TO) {

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
     * Helper method for setting up the Google Map
     */
    private void setUpMap() {
        // Obtain the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // Acquire the GoogleMap (automatically initializes the maps system and the view)
        // Will trigger the OnMapReady callback when the map is ready to be used
        mapFragment.getMapAsync(this);
    }

    /**
     * Helper method for setting up the chosen modes of transportation
     */
    private void setUpModes() {
        // Create the mode-imagebutton bimap
        modeToImageButtonBiMap = HashBiMap.create();

        // TODO: Grab the actual default modes set by the user
        // Initialize default modes & select the default modes
        ModeSelectOptions.setDefaultModes(Arrays.asList(TraverseMode.WALK, TraverseMode.BUS));
        ModeSelectOptions.selectDefaultModes();
    }

    /**
     * Helper method for setting up the sliding panel layout
     */
    private void setUpSlidingPanel() {
        mSlidingPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingPanelHead = (LinearLayout) findViewById(R.id.sliding_panel_head);
        mSlidingPanelTail = (ScrollView) findViewById(R.id.sliding_panel_tail);

        mSlidingPanelHead.setOnTouchListener(new SlidingPanelHeadOnSwipeTouchListener(this, this));
        mSlidingPanelTail.setOnTouchListener(new SlidingPanelTailOnSwipeTouchListener(this, this));

    }

    private void setUpNavigationButtons() {
        mNavButtonsLayout = (LinearLayout) findViewById(R.id.navigation_buttons_layout);
        mFab = (ImageButton) findViewById(R.id.navigation_fab);
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

        if (mAccelerometerReading[0] != 0 && mMagnetometerReading[0] !=0
                && updateCameraOnSensorChange) {
            updateNavigationModeCamera();
            updateCameraOnSensorChange = false;
        }
    }

    private void setUpOTPServices() {
        // Set up the retrofit services
        OTPPlanService.buildRetrofit(OTPPlanSvcApi.OTP_API_URL);
        OTPStopsService.buildRetrofit(OTPStopsSvcApi.OTP_API_URL);
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
        settings.setMapToolbarEnabled(false);

        setMapPadding(ActivityState.HOME);

        // Hide built-in transit icons on map
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success)
                Log.e(TAG, "Style parsing failed.");
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Build the GoogleApiClient
        if (checkLocationPermission()) {
            // Permission was already granted
            buildGoogleApiClient();
        }

        // Set the on camera idle listener
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Update current bounds
                mMapBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            }
        });

        // Set the on camera move listener
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                double zoom = mMap.getCameraPosition().zoom;

                // If we zoomed out below min level, hide markers
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

                // If we zoomed above min level, show markers
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

                if (!isHomeState(getState()))
                    return;

                // Center camera on the selected POI
                mMap.animateCamera(CameraUpdateFactory.newLatLng(pointOfInterest.latLng));

                // Get the place & display details
                String id = pointOfInterest.placeId;
                Places.GeoDataApi.getPlaceById(mGoogleAPIClient, id).setResultCallback(
                        new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(@NonNull PlaceBuffer places) {
                                if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                    Place myPlace = places.get(0);
                                    Log.i(TAG, "Point of interest Place found: "
                                            + myPlace.getName());

                                    transitionState(getState(), ActivityState.HOME_PLACE_SELECTED);

                                    selectPlaceOnMap(new TripPlanPlace(myPlace.getName(),
                                            myPlace.getLatLng(), myPlace.getAddress()));

                                } else {
                                    Log.e(TAG, "Point of interest Place not found");
                                }
                                places.release();
                            }
                        });
            }
        });

        // Set the on map click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i(TAG, "Location on map clicked: " + latLng.toString());

                if (!isHomeState(getState()))
                    return;

                // Center camera on the selected POI
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                transitionState(getState(), ActivityState.HOME_PLACE_SELECTED);

                NumberFormat formatter = new DecimalFormat("##0.0000");
                String latLngString = "("
                        + formatter.format(Math.round(latLng.latitude * 10000)/10000.0)
                        + ", " + formatter.format(Math.round(latLng.longitude * 10000)/10000.0)
                        + ")";
                selectPlaceOnMap(new TripPlanPlace(latLngString, latLng, " Unnamed location"));
            }
        });


        // Initialize the markers representing the transit stops in the city

        Call<ArrayList<Stop>> call = OTPStopsService.getOtpService().getStopsByRadius(
                OTPStopsService.ROUTER_ID,
                Double.toString(MY_CITY_LATITUDE),
                Double.toString(MY_CITY_LONGITUDE),
                Double.toString(MY_CITY_RADIUS),
                "true", "true"
        );

        call.enqueue(new Callback<ArrayList<Stop>>() {
            @Override
            public void onResponse(Call<ArrayList<Stop>> call,
                                   retrofit2.Response<ArrayList<Stop>> response) {

                if (!response.isSuccessful()) {
                    Log.e(TAG, "Request for transit stops was unsuccessful");
                    Toast.makeText(MainActivity.this, "Failed to load transit stops",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mCityTransitStops = response.body();
                Log.d(TAG, "Number of bus stops:" + mCityTransitStops.size());
                mCityTransitStopMarkers = new HashMap<>();

                // Save the transit stop markers
                for (Stop stop : mCityTransitStops) {
                    mCityTransitStopMarkers.put(
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(stop.getLat(), stop.getLon()))
                                    .icon(BitmapDescriptorFactory.fromBitmap(
                                        drawableToBitmap(getDrawable(R.drawable.ic_bus_stop))))
                                    .flat(true)
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

            @Override
            public void onFailure(Call<ArrayList<Stop>> call, Throwable throwable) {
                Log.e(TAG, "Failed to get transit stops");
            }
        });

        // Set the on marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                // Must be home state to show transit stop info
                if (!isHomeState(getState()))
                    return false;

                // Must be a transit stop marker to show transit stop info
                if (!mCityTransitStopMarkers.keySet().contains(marker))
                    return false;

                // Make HOME_STOP_SELECTED the only state over HOME in the state stack
                if (getState() != ActivityState.HOME)
                    onBackPressed();
                setState(ActivityState.HOME_STOP_SELECTED);

                // Hide sliding layout
                mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                // Set map padding
                setMapPadding(ActivityState.HOME_STOP_SELECTED);

                // Set marker
                if (mPlaceSelectedMarker != null)
                    mPlaceSelectedMarker.remove();
                mPlaceSelectedMarker = mMap.addMarker(new MarkerOptions()
                        .position(marker.getPosition()));

                // Move camera
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                // Initialize a fragment transaction to show the info window
                mTransitStopInfoWindowFragment = new TransitStopInfoWindowFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.transit_stop_info_window_frame, mTransitStopInfoWindowFragment)
                        .commit();
                mTransitStopInfoWindowFragment.showStopInfo(marker.getTitle(),
                        mCityTransitStopMarkers.get(marker));

                return true;
            }
        });
    }


    /**
     * Helper method for obtaining location access permission
     * Checks if the manifest permission ACCESS_FINE_LOCATION has been granted
     * and, if not, requests the permission
     *
     * @return If permission was initially granted
     */
    public boolean checkLocationPermission() {
        // If permission has not already been granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            // Invokes OnRequestPermissionsResult callback
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            return false;

        } else { // Permission has already been granted
            return true;
        }
    }

    /**
     * Callback method invoked by response to permissions request
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    // Permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        // Build the GoogleApiClient
                        if (mGoogleAPIClient == null) buildGoogleApiClient();
                    } else {
                        // Permission was denied
                        Toast.makeText(this, "Location access permission denied", Toast.LENGTH_LONG).show();
                        if (mGoogleAPIClient == null) buildGoogleApiClientWithoutLocationServices();
                    }
            }
            // Add other case lines to check for other permissions this app might request
        }
    }

    /**
     * Helper method that builds & connects the GoogleApiClient
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleAPIClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleAPIClient.connect();
    }

    /**
     * Helper method that builds & connects the GoogleApiClient without the
     * Location Services API
     */
    private synchronized void buildGoogleApiClientWithoutLocationServices() {
        mGoogleAPIClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleAPIClient.connect();
    }

    /**
     * Callback invoked when the GoogleApiClient is connected
     * Requests location update to initialize the map with the current location
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {

        Log.d(TAG, "API Client connected");

        startLocationUpdates(false);

        // Enable the My Location button
        try { mMap.setMyLocationEnabled(true); } catch (SecurityException ignored) {}

        // Move map camera to current location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(getCurrentCoordinates()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM_LEVEL));

    }

    /**
     * Callback invoked when location update is received
     * Initializes map by repositioning camera
     * Stops the location updates
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
    }

    /**
     * Hook method invoked when the GoogleApiClient's connection is suspended
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {}

    /**
     * Hook method invoked when the GoogleApiClient connection fails
     * @param cr
     */
    @Override
    public void onConnectionFailed(ConnectionResult cr) {
        Toast.makeText(this, "GoogleApiClient connection failed", Toast.LENGTH_LONG).show();
    }


    /**
     * Helper method to configure the directions button and show info about a location in
     * the sliding panel head, when a location is clicked on the map
     * @param place
     * @pre the activity is in HOME_PLACE_SELECTED state (transitionState has already been called)
     */
    private void selectPlaceOnMap(final TripPlanPlace place) {

        // Show marker
        if (mPlaceSelectedMarker != null)
            mPlaceSelectedMarker.remove();
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

                transitionState(getState(), ActivityState.TRIP_PLAN);
                planTrip(new TripPlanPlace(), place);
            }
        });
        hideArrowButtons();
        mNavButtonsLayout.setVisibility(View.VISIBLE);

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

    public ActivityState getState() { return mStateStack.peek();}

    public ActivityState removeState() {
        return mStateStack.pop();
    }

    public void setState(ActivityState state) {
        mStateStack.push(state);
    }

    /**
     * Helper method to facilitate a state transition in the activity
     * @param oldState
     * @param newState
     */
    public void transitionState(ActivityState oldState, ActivityState newState) {

        if (oldState == ActivityState.TRIP_PLAN && newState == ActivityState.TRIP_PLAN) {
            return;

        } else if (isHomeState(oldState) && newState == ActivityState.HOME_PLACE_SELECTED) {


            // INITIALIZE HOME_PLACE_SELECTED MODE

            // If the previous state was HOME_PLACE_SELECTED or HOME_STOP_SELECTED
            // or HOME_BUS_SELECTED, remove the previous state before updating the current state
            if (getState() == ActivityState.HOME_PLACE_SELECTED)
                removeState();
            if (getState() == ActivityState.HOME_STOP_SELECTED) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .remove(mTransitStopInfoWindowFragment)
                        .commit();
                removeState();
            }
            setState(ActivityState.HOME_PLACE_SELECTED);

            // Set map padding
            setMapPadding(ActivityState.HOME_PLACE_SELECTED);

            // Hide simple search bar
            if (mSimpleSearchBar.getVisibility() != View.GONE) {
                mSimpleSearchBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_up));
                mSimpleSearchBar.setVisibility(View.GONE);
                setMapPadding(ActivityState.HOME_PLACE_SELECTED);
            }

            // Clear sliding panel head
            mSlidingPanelHead.removeAllViews();
            mSlidingPanelLayout.setTouchEnabled(false);
            mSlidingPanelHead.setOrientation(LinearLayout.VERTICAL);


        } else if (isHomeState(oldState) && newState == ActivityState.TRIP_PLAN) {


            // INITIALIZE TRIP_PLAN MODE

            Log.d(TAG, "Transitioning to TRIP_PLAN screen");

            // Update state
            if (getState() == ActivityState.HOME_STOP_SELECTED) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .remove(mTransitStopInfoWindowFragment)
                        .commit();
            }

            while (getState() != ActivityState.HOME)
                removeState();
            setState(ActivityState.TRIP_PLAN);

            // Hide simple search bar
            mSimpleSearchBar.setVisibility(View.GONE);

            // Create a new detailed search bar
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
                    // after the new onClickListener is set

                    LatLng tripStartLocation = mOrigin.getLocation();
                    LatLng myLocation = getCurrentCoordinates();

                    if (tripStartLocation == null
                            || Math.abs(tripStartLocation.latitude - myLocation.latitude) > 0.0005
                            || Math.abs(tripStartLocation.longitude - myLocation.longitude) > 0.0005) {
                        Toast.makeText(MainActivity.this,
                                "Cannot launch navigation mode for trip " +
                                        "that does not begin at the current location",
                                Toast.LENGTH_LONG).show();
                    } else {
                        transitionState(ActivityState.TRIP_PLAN, ActivityState.NAVIGATION);
                    }
                }
            });
            mFab.setClickable(false);


            // Show navigation buttons
            mNavButtonsLayout.setVisibility(View.VISIBLE);


        } else if (oldState == ActivityState.TRIP_PLAN && newState == ActivityState.NAVIGATION) {


            // INITIALIZE NAVIGATION MODE

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
            mFab.setBackground(getDrawable(R.drawable.white_circle));
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
                if (StringToModeDictionary.isTransit(leg.getMode())) {
                    if (leg.getIntermediateStops() != null)
                        for (vanderbilt.thub.otp.model.OTPPlanModel.Place place
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
            startLocationUpdates(true); // invokes onLocationChanged
            registerSensorListeners(); // invokes onSensorChanged which updates camera


        } else {
            throw new RuntimeException("Invalid state transition request");
        }
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
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.getTextBounds(instruction,0,instruction.length(),textDimensions);

        int TEXT_PADDING = 20;
        int BALLOON_TAIL_SIZE = 20;
        float ROUNDED_RECT_CORNER_RADIUS = 20f;

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
     */
    public void setUpModeButtons() {

        // Get the current selected modes
        Set<TraverseMode> selectedModes = ModeSelectOptions.getSelectedModes();

        // Loop through the TraverseMode-ImageButtonId bimap
        for (Map.Entry<TraverseMode,ImageButton> entry: modeToImageButtonBiMap.entrySet()) {

            TraverseMode traverseMode = entry.getKey();
            ImageButton button = entry.getValue();

            // Initialize each button as selected or deselected
            if (selectedModes.contains(traverseMode))
                selectModeButton(button);
            else
                deselectModeButton(button);

            // Set on click listener for each button
            if (entry.getKey() == TraverseMode.BICYCLE) // TODO don't omit bike mode
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
            else
                button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton button = (ImageButton) v;

                    if (button.isSelected())
                        deselectModeButton(button);
                    else
                        selectModeButton(button);

                    // Refresh the search
                    planTrip(mOrigin, mDestination);
                }
            });

        }
    }

    /**
     * Helper function for selecting a mode button
     */
    private void selectModeButton(ImageButton button) {
        Log.d(TAG, "Mode button was selected");

        // Select button and add corresponding mode to list of selected modes
        button.setSelected(true);
        ModeSelectOptions.addSelectedMode(modeToImageButtonBiMap.inverse().get(button));

        // Set white background, colored image
        button.setBackgroundResource(R.drawable.rounded_rectangle_white);
        button.setColorFilter(getResources().getColor(R.color.colorPrimary, null));
    }

    /**
     * Helper function for deselecting a mode button
     */
    private void deselectModeButton(ImageButton button) {
        Log.d(TAG, "Mode button was deselected");

        // Deselect button and remove corresponding mode from list of selected modes
        button.setSelected(false);
        ModeSelectOptions.removeSelectedMode(modeToImageButtonBiMap.inverse().get(button));

        // Set colored background, white image
        button.setBackgroundResource(R.drawable.rounded_rectangle_primary);
        button.setColorFilter(Color.WHITE);
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
     * Gets the current location, makes a GET request to the OTP
     * server for a list of itineraries from the current location to mDestination,
     * and invokes displayItinerary on the first itinerary
     * @param origin
     * @param destination
     * @param time time by which the trip should depart or arrive by, pass null to use current time
     * @param departOrArriveBy false for depart, true for arrive
     * @return true if the request was successfully made, false otherwise
     * @pre the activity is in the TRIP_PLAN state (transitionState has already been called)
     */
    public boolean planTrip(@NonNull final TripPlanPlace origin,
                            @NonNull final TripPlanPlace destination,
                            @Nullable List<TripPlanPlace> intermediateStops,
                            @Nullable Date time, boolean departOrArriveBy) {

        // BEFORE PLANNING TRIP:

        // If no modes are selected, prompt user to choose a mode
        if (ModeSelectOptions.getNumSelectedModes() == 0) {
            Toast.makeText(this, "Please select at least one mode of transportation",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // If public transport is selected, ensure that walk or bicycle or car is also selected
        Set<TraverseMode> selectedModes = ModeSelectOptions.getSelectedModes();
        if ((selectedModes.contains(TraverseMode.BUS)
                || selectedModes.contains(TraverseMode.SUBWAY))
                &&
                !(selectedModes.contains(TraverseMode.WALK)
                || selectedModes.contains(TraverseMode.BICYCLE)
                || selectedModes.contains(TraverseMode.CAR))) {
            Toast.makeText(this, "Please select at least one of walk, bike, or car modes",
                    Toast.LENGTH_SHORT).show();
            return false;
        }


        // PLAN TRIP:

        Log.d(TAG, "Planning trip");
        Log.d(TAG, "Sliding panel state: " + mSlidingPanelLayout.getPanelState());

        // Set up origin and destination
        if (origin.isCurrentLocation())
            origin.setLocation(getCurrentCoordinates());
        if (destination.isCurrentLocation())
            destination.setLocation(getCurrentCoordinates());

        mOrigin = origin;
        mDestination = destination;

        // Get depart/arrive-by date & time
        if (time == null) // Get current time if a time was not provided
            time = new Date();
        String dateString = new SimpleDateFormat("MM-dd-yyyy", Locale.US).format(time);
        String timeString = new SimpleDateFormat("hh:mma", Locale.US).format(time);
        String displayTimeString = new SimpleDateFormat("hh:mm a", Locale.US).format(time);
        if (displayTimeString.charAt(0) == '0')
            displayTimeString = displayTimeString.substring(1);

        // Generate & display depart/arrive-by text
        String departOrArrive = departOrArriveBy ? "Arrive" : "Depart";
        mDetailedSearchBarFragment
                .setDepartArriveTimeText(departOrArrive + " by " + displayTimeString);

        // Reset MyLocation button functionality
        resetMyLocationButton();

        // Hide arrow buttons
        hideArrowButtons();

        // Remove origin and destination marker from the map
        if (mDestinationMarker != null)
            mDestinationMarker.remove();
//        if (mOriginMarker != null)
//            mOriginMarker.remove();

        // Remove previous itinerary from the map if it exists
        if (mPolylineList != null) {
            for (Polyline polyline : mPolylineList)
                polyline.remove();
            mPolylineList = null;
        }

        // Clear sliding panel head and display loading text
        showSlidingPanelHeadMessage("LOADING RESULTS...");

        // Clear sliding panel tail
        mSlidingPanelTail.removeAllViews();

        // Create and set up a new trip planner request
        final PlannerRequest request = new PlannerRequest();

        request.setFrom(new GenericLocation(origin.getLatitude(),
               origin.getLongitude()));
        request.setTo(new GenericLocation(destination.getLatitude(),
                destination.getLongitude()));
        request.setModes(ModeSelectOptions.getSelectedModesString());
        Log.d(TAG, "Selected modes: " + ModeSelectOptions.getSelectedModesString());

        if (intermediateStops != null) {
            ArrayList<GenericLocation> intermediatePlaces = new ArrayList<>();
            for (TripPlanPlace tpp : intermediateStops)
                intermediatePlaces.add(new GenericLocation(tpp.getLatitude(), tpp.getLongitude()));
            request.setIntermediatePlaces(intermediatePlaces);
        }

        String startLocation = Double.toString(request.getFrom().getLat()) +
                "," + Double.toString(request.getFrom().getLng());
        String endLocation = Double.toString(request.getTo().getLat()) +
                "," + Double.toString(request.getTo().getLng());

        String intermediateLocations = "";
        if (intermediateStops != null) {
            for (GenericLocation location : request.getIntermediatePlaces())
                intermediateLocations += ";" + Double.toString(location.getLat()) +
                        "," + Double.toString(location.getLng());
            intermediateLocations = intermediateLocations.substring(1); // separator fencepost
        }


        // Make the request to OTP server
        Call<Response> response;
        if (intermediateStops == null)
            response = OTPPlanService.getOtpService().getTripPlan(
                    OTPPlanService.ROUTER_ID,
                    startLocation,
                    endLocation,
                    request.getModes(),
                    "TRANSFERS",
                    dateString,
                    timeString,
                    departOrArriveBy
            );
        else
            response = OTPPlanService.getOtpService().getTripPlan(
                    OTPPlanService.ROUTER_ID,
                    startLocation,
                    endLocation,
                    intermediateLocations,
                    true,
                    request.getModes(),
                    "TRANSFERS",
                    dateString,
                    timeString,
                    departOrArriveBy
            );

        final long curTime = System.currentTimeMillis();
        response.enqueue(new Callback<Response>() {

            // Handle the request response
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                Log.d(TAG, "Received trip plan from server. Time: " +
                        (System.currentTimeMillis() - curTime));

                if (response.body().getPlan() == null
                        || response.body().getPlan().getItineraries() == null
                        || response.body().getPlan().getItineraries().isEmpty()) {
                    Log.d(TAG, "OTP request result was empty");
                    showSlidingPanelHeadMessage("No results");
                    return;
                }

                // Save & sort the list of itinerary results
                mItineraryList = response.body().getPlan().getItineraries();
                Collections.sort(mItineraryList, new Comparator<Itinerary>() {
                    @Override
                    public int compare(Itinerary o1, Itinerary o2) {
                        int numLegs1 = o1.getLegs().size();
                        int numLegs2 = o2.getLegs().size();

                        if (1.5 * o1.getDuration() <= o2.getDuration())
                            return -1;
                        else if (1.5 *  o2.getDuration() <= o1.getDuration())
                            return 1;
                        else if (numLegs1 < numLegs2) {
                            return -1;
                        } else if (numLegs1 > numLegs2)
                            return 1;
                        else
                            return 0;
                    }
                });

                // Initialize list of points along itinerary
                mItineraryPointList = new LinkedList<>();

                // Initialize list of markers along itinerary
                mNavigationMarkerList = new LinkedList<>();

                // Get the first itinerary in the results & display it
                displayItinerary(0,
                        origin.getLocation(), destination.getLocation(),
                        android.R.anim.slide_in_left);

                // Activate the "start navigation" button
                mFab.setClickable(true);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Log.d(TAG, "Request failed to get itineraries:\n" + throwable.toString());
                Toast.makeText(getApplicationContext(),
                        "Request to server failed", Toast.LENGTH_LONG).show();

                // Display "Request failed" on the sliding panel head
                showSlidingPanelHeadMessage("Request failed");

                // Move the camera to include just the origin and destination
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder()
                        .include(origin.getLocation())
                        .include(destination.getLocation())
                        .build()
                        , 150)
                );
            }
        });

        Log.d(TAG, "Made request to OTP server");
        Log.d(TAG, "Starting point coordinates: " + origin.getLocation().toString());
        Log.d(TAG, "Destination coordinates: " + destination.getLocation().toString());
        return true;
    }


    /**
     * Helper method that resets the functionality of the map's My Location button
     *
     * pre: mMap and location services are set up and working
     */
    private void resetMyLocationButton() {
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(getCurrentCoordinates()));
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

        mItineraryPointList.clear();

//        // Log itinerary for debugging purposes
//                for (Leg leg : itinerary.getLegs())
//                    Log.d(TAG, leg.toString());

        // Clear slidingPanelHead
        mSlidingPanelHead.removeAllViews();
        mSlidingPanelHead.setOrientation(LinearLayout.HORIZONTAL);
        // Clear sliding panel tail
        mSlidingPanelTail.removeAllViews();

        if (itinerary == null) {
            Log.d(TAG, "Itinerary is null; failed to display");
            return;
        }

        // Remove polyline of previous itinerary if it exists
        if (mPolylineList != null) {
            for (Polyline polyline : mPolylineList)
                polyline.remove();
            mPolylineList = null;
        }

        // Remove place selected marker and add origin and destination markers
        if (mPlaceSelectedMarker != null)
            mPlaceSelectedMarker.remove();

        mDestinationMarker = mMap.addMarker(new MarkerOptions()
                .position(mDestination.getLocation())
                .title(mDestination.getName())
        );
//        mOriginMarker = mMap.addMarker(new MarkerOptions()
//                .position(mOrigin.getLocation())
//                .title(mOrigin.getName())
//        );

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
                    .width(POLYLINE_WIDTH);

            // Create a new custom view representing this leg of the itinerary
            int paddingBetweenModeIconAndDurationText =
                    (leg.getMode().equals(TraverseMode.BICYCLE.toString())) ? 13 : 0;

            ItineraryLegIconView view = new ItineraryLegIconView(this,
                    paddingBetweenModeIconAndDurationText);

            // Configure the polyline and custom view based on the mode of the leg
            Drawable d = ModeToDrawableDictionary.getDrawable(leg.getMode());
            d.setAlpha(DARK_OPACITY);
            view.setIcon(d);

            switch (leg.getMode()) {
                case ("WALK"):
                    polylineOptions
                            .color(getResources().getColor(R.color.colorPrimary, null))
                            .pattern(Arrays.asList(new Dot(), new Gap(10)));
                    view.setLegDuration((int) Math.ceil(leg.getDuration()/60));
                    break;
                case ("BICYCLE"):
                    polylineOptions
                            .color(getResources().getColor(R.color.colorPrimary, null))
                            .pattern(Arrays.asList(new Dash(30), new Gap(10)));
                    view.setLegDuration((int) Math.ceil(leg.getDuration()/60));
                    break;
                case ("CAR"):
                    polylineOptions.color(getResources().getColor(R.color.colorPrimary, null));

                    break;
                case ("BUS"):
                    polylineOptions.color(Color.parseColor("#" + leg.getRouteColor()));
                    view.setRouteName(leg.getRoute());
                    view.setRouteColor(Color.parseColor("#" + leg.getRouteColor()));
                    view.setRouteNameColor(Color.WHITE);
                    view.setShowRoute(true);
                    break;
                case ("SUBWAY"):
                    polylineOptions.color(Color.parseColor("#" + leg.getRouteColor()));
                    view.setRouteName(leg.getRoute());
                    view.setRouteColor(Color.parseColor("#" + leg.getRouteColor()));
                    view.setRouteNameColor(Color.WHITE);
                    view.setShowRoute(true);
                    break;
                default: polylineOptions.color(Color.GRAY);
            }

            // Draw the polyline leg to the map and save it to the list
            polylineList.add(mMap.addPolyline(polylineOptions));

            // Add chevron icon to the sliding panel drawer handle
            // (except in front of the first mode icon)
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

            // Add the leg custom view to the created linear layout
            itinerarySummaryLegsLayout.addView(view, index,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            ++index;

        }

        // Add the itinerary summary layout to the sliding panel head
        mSlidingPanelHead.addView(itinerarySummaryLegsLayout, new LinearLayout
                .LayoutParams(mSlidingPanelHead.getWidth() - 230,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Add the itinerary duration view to the sliding panel head
        TextView duration = new TextView(this);
        duration.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        duration.setTextColor(Color.BLACK);
        duration.setHorizontallyScrolling(false);
        duration.setAlpha(DARK_OPACITY_PERCENTAGE);
        duration.setTextSize(13);
        duration.setText(getDurationString(itinerary.getDuration()));
        duration.setPadding(0,0,0,0);
        mSlidingPanelHead.addView(duration, new LinearLayout
                .LayoutParams(230, ViewGroup.LayoutParams.MATCH_PARENT));

        // Add the expanded itinerary view to the sliding panel tail
        ExpandedItineraryView itineraryView = new ExpandedItineraryView(this);
        itineraryView.setPadding(0,50,0,150);

        // Configure the start and end points of the itinerary
        if (legList.size() != 0)
            legList.get(0).setFrom(new vanderbilt.thub.otp.model.OTPPlanModel.Place(
                    origin.latitude, origin.longitude, mDetailedSearchBarFragment.getOriginText())
            );
        legList.get(legList.size() - 1).setTo(new vanderbilt.thub.otp.model.OTPPlanModel.Place(
                destination.latitude, destination.longitude,
                mDetailedSearchBarFragment.getDestinationText())
        );
        itineraryView.setItinerary(itinerary);
        mSlidingPanelTail.addView(itineraryView, new ScrollView
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Animate the sliding panel components into view
        mSlidingPanelHead.startAnimation(AnimationUtils.loadAnimation(this, animationId));
        mSlidingPanelTail.startAnimation(AnimationUtils.loadAnimation(this, animationId));

        // Save the list of polylines drawn on the map
        mPolylineList =  polylineList;

        // Reconfigure map camera & My Location button
        configureZoomByPolylineBounds();
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                configureZoomByPolylineBounds();
                return true;
            }
        });

        // Show the appropriate arrow buttons
        showArrowButtons();

        Log.d(TAG, "Done displaying itinerary. Time: " + (System.currentTimeMillis() - time));
        Log.d(TAG, "Sliding panel state: " + mSlidingPanelLayout.getPanelState());

    }

    private void configureZoomByPolylineBounds() {

        if (!mPolylineList.isEmpty()) {
            LatLngBounds bounds = calculateLatLngPolylineBounds();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    bounds, 100));
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
     * Helper function that returns a string representing time in terms of
     * days, hours, and minutes, or seconds if shorter than a single minute
     * @param seconds
     * @return
     */
    public static String getDurationString(double seconds) {

        long totalMins = (long) seconds/60;

        long totalHours = totalMins/60;
        long remainderMins = totalMins%60;

        long days = totalHours/24;
        long remainderHours = totalHours%24;

        String duration = "";

        if (days != 0)
            duration += (days + " days ");
        if (remainderHours != 0)
            duration += (remainderHours + " h ");
        if (remainderMins != 0)
            duration += (remainderMins + " m ");

        if (duration.equals(""))
            duration = seconds + " sec ";

        // Slice off the extra space at the end
        duration = duration.substring(0, duration.length() - 1);

        return duration;
    }

    /**
     * Helper method that returns the current location
     */
    private LatLng getCurrentCoordinates() {
        try {
            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleAPIClient);
            return new LatLng(location.getLatitude(), location.getLongitude());
        } catch (SecurityException se) {
            Toast.makeText(this, "Location access permission denied", Toast.LENGTH_LONG).show();
            throw se;
        }
    }

    /**
     *  Helper function to generate latitude and longitude bounds to bias the results of a Google
     *  Places autocomplete prediction to a 20-mile-wide square centered at the current location
     *  If the current location is unavailable, returns bounds encompassing the whole globe
     */
    public LatLngBounds getBoundsBias() {

        try {
            // Get current location
            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleAPIClient);

            if (location != null) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Return bounds for a 20-mile-wide square centered at the current location
                return new LatLngBounds(new LatLng(latitude - .145, longitude - .145),
                        new LatLng(latitude + .145, longitude - .145));
            } else {
                Log.d(TAG, "Current location for setting search bounds bias was null");
                // If location is null, return bounds for the whole globe
                return new LatLngBounds(new LatLng(-90,-180), new LatLng(90, 180));
            }

        } catch (SecurityException se) {

            // If we cannot access the current location, return bounds for the whole globe
            return new LatLngBounds(new LatLng(-90,-180), new LatLng(90, 180));
        }
    }

    public void onSwipeSlidingPanelLeft() {

        Log.d(TAG, "Handling swipe left");

        // Do nothing if we are displaying the last itinerary
        if (mItineraryList == null || mCurItineraryIndex == mItineraryList.size() - 1)
            return;
        if (mSlidingPanelHead == null || mSlidingPanelTail == null)
            return;

        ++mCurItineraryIndex;
        Animation slideOutLeft = AnimationUtils
                .loadAnimation(this, R.anim.slide_out_left);
        slideOutLeft.setAnimationListener(new SwipeLeftAnimationListener());
        mSlidingPanelHead.startAnimation(slideOutLeft);
        mSlidingPanelTail.startAnimation(slideOutLeft);

        if (mCurItineraryIndex == mItineraryList.size() - 1) {
            hideRightArrowButton();
        }

    }

    private class SwipeLeftAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
            displayItinerary(mCurItineraryIndex,
                    mOrigin.getLocation(), mDestination.getLocation(), R.anim.slide_in_right);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }

    public void onSwipeSlidingPanelRight() {

        Log.d(TAG, "Handling swipe right");

        // Do nothing if we are displaying the first itinerary
        if (mItineraryList == null || mCurItineraryIndex == 0)
            return;
        if (mSlidingPanelHead == null || mSlidingPanelTail == null)
            return;

        --mCurItineraryIndex;
        Animation slideOutRight = AnimationUtils
                .loadAnimation(this, R.anim.slide_out_right);
        slideOutRight.setAnimationListener(new SwipeRightAnimationListener());
        mSlidingPanelHead.startAnimation(slideOutRight);
        mSlidingPanelTail.startAnimation(slideOutRight);

        if (mCurItineraryIndex == 0) {
            hideLeftArrowButton();
        }

    }

    private class SwipeRightAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {
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

    public void setmOrigin(TripPlanPlace place) {
        mOrigin = place;
    }

    public void setmDestination(TripPlanPlace place) {
        mDestination = place;
    }

    public void addToModeButtonBiMap(TraverseMode mode, ImageButton button) {
        modeToImageButtonBiMap.forcePut(mode, button);
    }

    private void setLastEditedSearchBar(SearchBarId id) {lastEditedSearchBar = id;}

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
        switch (state) {
            case HOME:
                mMap.setPadding(12,175,12,12);
                break;
            case HOME_PLACE_SELECTED:
                mMap.setPadding(12,12,12,12);
                break;
            case HOME_STOP_SELECTED:
                mMap.setPadding(12,175,12,340);
                break;
            case HOME_BUS_SELECTED:
                mMap.setPadding(12,12,12,12);
                break;
            case TRIP_PLAN:
                mMap.setPadding(12,550,12,12);
                break;
            case NAVIGATION:
                mMap.setPadding(12,12,12,12);
        }
    }

    /**
     * Helper method to start sending location requests
     * OnLocationChanged callback will be invoked on each response
     */
    private void startLocationUpdates(boolean high_accuracy) {

        // Remove previous location update request
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleAPIClient, this);

        if (high_accuracy) {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(LOCATION_INTERVAL);
            locationRequest.setFastestInterval(LOCATION_INTERVAL);
            if (checkLocationPermission())
                // Invokes onLocationChanged callback
                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleAPIClient, locationRequest, this);

        } else {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (checkLocationPermission())
                // Invokes onLocationChanged callback
                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleAPIClient, locationRequest, this);
        }
    }

    /**
     * Helper method to register sensor listeners to detect device rotation
     */
    private void registerSensorListeners() {
        mSensorManager.registerListener((SensorEventListener) this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener((SensorEventListener) this,
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
     * Shows the arrow buttons according to the current itinerary index and itinerary list size
     */
    private void showArrowButtons() {
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
     * Helper method that returns true if the state is one of HOME,
     * HOME_PLACE_SELECTED, HOME_STOP_SELECTED, or HOME_BUS_SELECTED
     * @param state
     * @return
     */
    private boolean isHomeState(ActivityState state) {
        return (state == ActivityState.HOME ||
                state == ActivityState.HOME_PLACE_SELECTED ||
                state == ActivityState.HOME_STOP_SELECTED ||
                state == ActivityState.HOME_BUS_SELECTED);
    }

    /**
     * Helper method to calculate the bearing between two points
     * @param start
     * @param end
     * @return
     */
    private double getBearing(LatLng start, LatLng end) {
        double startLat = Math.toRadians(start.latitude);
        double startLong = Math.toRadians(start.longitude);
        double endLat = Math.toRadians(end.latitude);
        double endLong = Math.toRadians(end.longitude);

        double dLong = endLong - startLong;

        double dPhi = Math.log(Math.tan(endLat / 2.0 + Math.PI / 4.0)
                / Math.tan(startLat / 2.0 + Math.PI / 4.0));
        if (Math.abs(dLong) > Math.PI) {
            if (dLong > 0.0) dLong = -(2.0 * Math.PI - dLong);
            else dLong = (2.0 * Math.PI + dLong);
        }

        return (Math.toDegrees(Math.atan2(dLong, dPhi)) + 360.0) % 360.0;
    }


    /**
     * Helper method to update the camera
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
                .target(getCurrentCoordinates())
                .zoom(NAVIGATION_MODE_ZOOM_LEVEL)
                .tilt(NAVIGATION_MODE_TILT_LEVEL)
                .bearing((float)Math.toDegrees(orientationAngles[0]))
                .build())
        );

    }
}
