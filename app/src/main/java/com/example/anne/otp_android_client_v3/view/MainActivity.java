package com.example.anne.otp_android_client_v3.view;

import android.Manifest;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.example.anne.otp_android_client_v3.util.ModeSelectOptions;
import com.example.anne.otp_android_client_v3.R;
import com.example.anne.otp_android_client_v3.model.Contract;
import com.example.anne.otp_android_client_v3.model.SearchHistoryDbHelper;
import com.example.anne.otp_android_client_v3.util.ModeUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
import java.util.Date;
import java.util.HashMap;
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
import vanderbilt.thub.otp.service.OTPService;
import vanderbilt.thub.otp.service.OTPServiceAPI;


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

    private final double MY_CITY_RADIUS = 48300; // meters

    private final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private final int POLYLINE_WIDTH = 23;

    private final int LOCATION_INTERVAL = 5000; // milliseconds

    private static final float DEFAULT_ZOOM_LEVEL = 15;

    private static final float NAVIGATION_MODE_ZOOM_LEVEL = 18;

    private static final float NAVIGATION_MODE_TILT_LEVEL = 60;

    private static final float MIN_SHOW_MARKER_ZOOM_LEVEL = 17;

    private static final double LOCATION_COORD_THRESHOLD = 0.0005;

    private ConcurrentLinkedDeque<ActivityState> mStack;

    private SearchHistoryDbHelper mSearchHistoryDatabaseHelper;

    private GoogleMap mMap;

//    private volatile LatLngBounds mMapBounds;

    private GoogleApiClient mGoogleAPIClient;

    private SlidingUpPanelLayout mSlidingPanelLayout;

    private LinearLayout mSlidingPanelHead;

    private ScrollView mSlidingPanelTail;

    private LinearLayout mTabRowLayout;

    private DetailedSearchBarFragment mDetailedSearchBarFragment;

    private TransitStopInfoWindowFragment mTransitStopInfoWindowFragment;

    private ImageButton mFab;

    private ImageButton mRightArrowButton;

    private ImageButton mLeftArrowButton;

    private SearchBarId lastEditedSearchBar;

    private CardView mSimpleSearchBar;

    private TextView mSimpleSearchBarText;

    private TripPlanPlace mOrigin = null;

    private TripPlanPlace mDestination = null;

    //    private Marker mOriginMarker;

    private Marker mDestinationMarker;

    private Marker mPlaceSelectedMarker;

    private int mCurItineraryIndex;

    private List<Itinerary> mItineraryList;

    private List<Polyline> mPolylineList;

    private List<LatLng> mItineraryPointList; // todo probably change to list of lists

    private List<Marker> mNavigationMarkerList;

    private BiMap<TraverseMode, ImageButton> modeToImageButtonBiMap;

    private SensorManager mSensorManager;

    private final float[] mAccelerometerReading = new float[3];

    private final float[] mMagnetometerReading = new float[3];

    private List<Stop> mCityTransitStops;

    private Map<Marker, String> mCityTransitStopMarkers;

    private volatile double mLastZoomLevel;

    public volatile long timeOfLastTripPlanInterrupt = 0;

    public volatile long timeOfLastTransitRoutesInterrupt = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Activity created");

        // Setup
        setUpMap();
        setUpDrawer();
        setUpSimpleSearchBar();
        setUpModes();
        setUpSlidingPanel();
        setUpNavigationButtons();
        setUpSensorManager();
        setUpOTPServices();
        setUpStateStack();
        setUpAccessToDatabase();
        ModeUtils.setup(this);
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

        // INITIALIZE HOME_PLACE_SELECTED MODE
        if (isHomeState(oldState) && newState == ActivityState.HOME_PLACE_SELECTED) {

            // If the previous state was HOME_STOP_SELECTED
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


        // INITIALIZE TRIP_PLAN MODE
        if (isHomeState(oldState) && newState == ActivityState.TRIP_PLAN) {

            Log.d(TAG, "Transitioning to TRIP_PLAN screen");

            // If the previous screen was HOME_STOP_SELECTED
            if (oldState == ActivityState.HOME_STOP_SELECTED)
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

                    // Launch navigation if the start location of the current trip matches
                    // the current location of the device
                    LatLng tripStartLocation = mOrigin.getLocation();
                    LatLng myLocation = getCurrentCoordinates();
                    if (tripStartLocation == null ||
                            Math.abs(tripStartLocation.latitude - myLocation.latitude)
                            > LOCATION_COORD_THRESHOLD ||
                            Math.abs(tripStartLocation.longitude - myLocation.longitude)
                            > LOCATION_COORD_THRESHOLD) {
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


        // INITIALIZE NAVIGATION MODE
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
                if (ModeUtils.isTransit(leg.getMode())) {
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

            updateNavigationModeCamera();

            return;
        }

        throw new RuntimeException("Invalid state transition request");
    }


    /**
     * Helper method to facilitate transition to the previous screen in the activity
     */
    public void goToPreviousScreen() {
        // Interrupt any ongoing trip plan request
        interruptTripPlan();

        // Perform the corresponding necessary actions based on the current state
        switch (removeState()) {
            case TRIP_PLAN:

                // TRANSITIONING BACK FROM TRIP_PLAN SCREEN

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
                mMap.moveCamera(CameraUpdateFactory.newLatLng(getCurrentCoordinates()));
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

                // TRANSITIONING BACK FROM HOME_PLACE_SELECTED SCREEN

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
                mMap.animateCamera(CameraUpdateFactory.newLatLng(getCurrentCoordinates()));

                break;

            case HOME_STOP_SELECTED:

                // TRANSITIONING BACK FROM HOME_STOP_SELECTED SCREEN

                // Remove place selected marker
                removeMarker(mPlaceSelectedMarker);

                // Set map padding
                setMapPadding(ActivityState.HOME);

                // Focus camera back to current location
                mMap.animateCamera(CameraUpdateFactory.newLatLng(getCurrentCoordinates()));

                // Remove the transit stop info window fragment
                removeTransitStopInfoWindow();

                break;

            case NAVIGATION:

                // TRANSITIONING BACK FROM NAVIGATION SCREEN

                // Re-set-up the "start navigation" button (fab)
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mFab.setClickable(false);

                        // FAB becomes clickable again as the "exit navigation mode" button
                        // after the new onClickListener is set in call to goToNextScreen()

                        LatLng tripStartLocation = mOrigin.getLocation();
                        LatLng myLocation = getCurrentCoordinates();

                        if (tripStartLocation == null
                                || Math.abs(tripStartLocation.latitude - myLocation.latitude) > 0.0005
                                || Math.abs(tripStartLocation.longitude - myLocation.longitude) > 0.0005) {
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
                configureZoomByPolylineBounds();

                // Re-enable my location button
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                // Resume low-battery consumption location updates
                startLocationUpdates(false);

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
     * Gets the current location, makes a GET request to the OTP
     * server for a list of itineraries from the current location to mDestination,
     * and invokes displayItinerary() on the first itinerary
     * @param origin point of origin for the trip; use null for current location
     * @param destination destination for the trip; use null for current location
     * @param intermediateStops use null for none
     * @param time time by which the trip should depart or arrive by; use null for current time
     * @param departOrArriveBy false for depart time, true for arrive time
     * @return true if the request was successfully made, false otherwise
     */
    public boolean planTrip(@NonNull final TripPlanPlace origin,
                            @NonNull final TripPlanPlace destination,
                            @Nullable List<TripPlanPlace> intermediateStops,
                            @Nullable Date time, boolean departOrArriveBy) {

        final long timeBeginPlanTrip = System.currentTimeMillis();

        // Set up UI for the trip plan screen
        if (getState() != ActivityState.TRIP_PLAN)
            goToNextScreen(ActivityState.TRIP_PLAN);

        // Set up origin and destination
        if (origin.shouldUseCurrentLocation())
            origin.setLocation(getCurrentCoordinates());
        if (destination.shouldUseCurrentLocation())
            destination.setLocation(getCurrentCoordinates());
        mOrigin = origin;
        mDestination = destination;

        // Add the trip plan attempt to search history database
        addToSearchHistory(mOrigin.getName(), mDestination.getName(),
                mOrigin.getLocation(), mDestination.getLocation(),
                ModeSelectOptions.getSelectedModesString(), (new Date()).getTime());

        // Hide the arrow buttons and the start navigation button
        hideArrowButtons();
        mFab.setVisibility(View.GONE);

        // Remove previous markers from the map
        removeMarker(mDestinationMarker);
//        removeMarker(mOriginMarker);

        // Remove previous itinerary from the map
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

        // If no modes are selected, prompt user to choose a mode
        if (ModeSelectOptions.getNumSelectedModes() == 0) {
            Toast.makeText(this, "Please select at least one mode of transportation",
                    Toast.LENGTH_SHORT).show();
            removeMarker(mPlaceSelectedMarker);
            return false;
        }

        // If public transport is selected, ensure that walk or bicycle or car is also selected
        Set<TraverseMode> selectedModes = ModeSelectOptions.getSelectedModes();
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

        Log.d(TAG, "Planning trip");
        Log.d(TAG, "Sliding panel state: " + mSlidingPanelLayout.getPanelState());

        // Get depart/arrive-by date & time
        if (time == null) // Get current time if a time was not provided
            time = new Date();
        String dateString = new SimpleDateFormat("MM-dd-yyyy", Locale.US).format(time);
        String timeString = new SimpleDateFormat("hh:mma", Locale.US).format(time);
        String displayTimeString = new SimpleDateFormat("hh:mm a", Locale.US).format(time);
        if (displayTimeString.charAt(0) == '0')
            displayTimeString = displayTimeString.substring(1);

        // Generate & display depart/arrive-by text
        String departOrArrive = departOrArriveBy ? "Arrive by " : "Depart after ";
        mDetailedSearchBarFragment
                .setDepartArriveTimeText(departOrArrive + displayTimeString);

        // Reset MyLocation button functionality
        resetMyLocationButton();

        // Display loading text on sliding panel head
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
            response = OTPService.getOtpService().getTripPlan(
                    OTPService.ROUTER_ID,
                    startLocation,
                    endLocation,
                    request.getModes(),
                    true,
                    "TRANSFERS",
                    dateString,
                    timeString,
                    departOrArriveBy
            );
        else
            response = OTPService.getOtpService().getTripPlan(
                    OTPService.ROUTER_ID,
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

        final long timeOfRequest = System.currentTimeMillis();

        response.enqueue(new Callback<Response>() {

            // Handle the request response
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                Log.d(TAG, "Received trip plan from server. Time: " +
                        (System.currentTimeMillis() - timeOfRequest));

                // Abort if request was cancelled
                if (timeOfLastTripPlanInterrupt > timeBeginPlanTrip)
                    return;

                // Handle case where no results are received
                if (response.body().getPlan() == null
                        || response.body().getPlan().getItineraries() == null
                        || response.body().getPlan().getItineraries().isEmpty()) {
                    Log.d(TAG, "OTP request result was empty");
                    showSlidingPanelHeadMessage("No results");
                    hideArrowButtons();
                    mFab.setVisibility(View.GONE);
                    return;
                }

                // Save the list of itinerary results
                mItineraryList = response.body().getPlan().getItineraries();

                // Initialize the highlighted tab row to indicate which itinerary is selected
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
                        origin.getLocation(), destination.getLocation(),
                        android.R.anim.slide_in_left);

            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Log.d(TAG, "Request failed to get itineraries:\n" + throwable.toString());
                Toast.makeText(getApplicationContext(),
                        "Request to server failed", Toast.LENGTH_SHORT).show();

                // Display "Request failed" on the sliding panel head
                showSlidingPanelHeadMessage("Request failed");

                // Move the camera to include just the origin and destination
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder()
                                .include(origin.getLocation())
                                .include(destination.getLocation())
                                .build(), 150)
                );

                // Hide selected place marker
                removeMarker(mPlaceSelectedMarker);
            }
        });


        Log.d(TAG, "Made request to OTP server");
        Log.d(TAG, "Starting point coordinates: " + origin.getLocation().toString());
        Log.d(TAG, "Destination coordinates: " + destination.getLocation().toString());
        return true;
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
                    .width(POLYLINE_WIDTH);

            // Create a new custom view representing this leg of the itinerary
            int paddingBetweenModeIconAndDurationText =
                    (leg.getMode().equals(TraverseMode.BICYCLE.toString())) ? 13 : 0;

            ItineraryLegIconView view = new ItineraryLegIconView(this,
                    paddingBetweenModeIconAndDurationText);

            // Configure the polyline and custom view based on the mode of the leg
            Drawable d = ModeUtils.getDrawableFromString(leg.getMode());
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

        // Layout for the leg icons and the duration
        LinearLayout itinerarySummaryLayout = new LinearLayout(this);

        // Add the itinerary summary leg icons to the layout
        itinerarySummaryLayout.addView(itinerarySummaryLegsLayout, new LinearLayout
                .LayoutParams(mSlidingPanelHead.getWidth() - 230,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Add the itinerary duration view to the layout
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
        mSlidingPanelHead.getChildAt(1)
                .startAnimation(AnimationUtils.loadAnimation(this, animationId));
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
        // Activate the "start navigation" button
        mFab.setVisibility(View.VISIBLE);
        mFab.setClickable(true);

        // Set up on swipe listeners for the sliding panel
        mSlidingPanelHead.setOnTouchListener(new SlidingPanelHeadOnSwipeTouchListener(this, this));
        mSlidingPanelTail.setOnTouchListener(new SlidingPanelTailOnSwipeTouchListener(this, this));

        Log.d(TAG, "Done displaying itinerary. Time: " + (System.currentTimeMillis() - time));
        Log.d(TAG, "Sliding panel state: " + mSlidingPanelLayout.getPanelState());

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
    private void setUpSimpleSearchBar() {
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
    }

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

    private void setUpOTPServices() {
        // Set up the retrofit services
        OTPService.buildRetrofit(OTPServiceAPI.OTP_API_URL);
    }

    private void setUpStateStack() {
        // Initialize state
        mStack = new ConcurrentLinkedDeque<>();
        setState(ActivityState.HOME);
    }

    private void setUpAccessToDatabase() {
        mSearchHistoryDatabaseHelper = new SearchHistoryDbHelper(this);
    }

    private long addToSearchHistory(String fromName, String toName,
                                   LatLng fromCoords, LatLng toCoords,
                                   String modes, long timeStamp) {

        // Gets the data repository in write mode
        SQLiteDatabase db = mSearchHistoryDatabaseHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Contract.SearchHistoryTable.COLUMN_NAME_FROM_NAME, fromName);
        values.put(Contract.SearchHistoryTable.COLUMN_NAME_TO_NAME, toName);
        values.put(Contract.SearchHistoryTable.COLUMN_NAME_FROM_COORDINATES,
                Double.toString(fromCoords.latitude) + "," + Double.toString(fromCoords.longitude));
        values.put(Contract.SearchHistoryTable.COLUMN_NAME_TO_COORDINATES,
                Double.toString(toCoords.latitude) + "," + Double.toString(toCoords.longitude));
        values.put(Contract.SearchHistoryTable.COLUMN_NAME_MODES, modes);
        values.put(Contract.SearchHistoryTable.COLUMN_NAME_TIMESTAMP, timeStamp);

        // Insert the new row, returning the primary key value of the new row
        return db.insert(Contract.SearchHistoryTable.TABLE_NAME, null, values);

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

        // Hide built-in transit icons on map
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.hide_transit_stops_style_json));
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

//        // Set the on camera idle listener
//        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                // Update current bounds
//                mMapBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
//            }
//        });

        // Set the on camera move listener
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                // Get new zoom level
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

                interruptTransitRoutesRequest();

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

                                    goToNextScreen(ActivityState.HOME_PLACE_SELECTED);

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

                interruptTransitRoutesRequest();

                // Center camera on the selected POI
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


        // Initialize the markers representing the transit stops in the city
        Call<ArrayList<Stop>> call = OTPService.getOtpService().getStopsByRadius(
                OTPService.ROUTER_ID,
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

                interruptTransitRoutesRequest();

                // Must be in a home state to show transit stop info
                if (!isHomeState(getState()))
                    return false;

                // Must be a transit stop marker to show transit stop info
                if (!mCityTransitStopMarkers.keySet().contains(marker))
                    return false;


                // TRANSITION TO HOME_STOP_SELECTED STATE

                // If not already in HOME_STOP_SELECTED mode
                if (getState() != ActivityState.HOME_STOP_SELECTED) {

                    // Make HOME_STOP_SELECTED the only state over HOME in the state stack
                    if (getState() != ActivityState.HOME)
                        onBackPressed();
                    setState(ActivityState.HOME_STOP_SELECTED);
                    // Hide sliding layout
                    mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    // Set map padding
                    setMapPadding(ActivityState.HOME_STOP_SELECTED);

                }

                // Set marker
                removeMarker(mPlaceSelectedMarker);
                mPlaceSelectedMarker = mMap.addMarker(new MarkerOptions()
                        .position(marker.getPosition()));

                // Move camera
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                // Initialize a fragment transaction to show the info window
                FragmentManager fragmentManager = getFragmentManager();
                if (mTransitStopInfoWindowFragment == null) {
                    mTransitStopInfoWindowFragment = new TransitStopInfoWindowFragment();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_up)
                            .add(R.id.transit_stop_info_window_frame, mTransitStopInfoWindowFragment)
                            .commit();
                }
                // Or if the info window is already there, replace the information
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
                        Toast.makeText(this, "Location access permission denied", Toast.LENGTH_SHORT).show();
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
        if (getState() == ActivityState.NAVIGATION)
            updateNavigationModeCamera();
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
        Toast.makeText(this, "GoogleApiClient connection failed", Toast.LENGTH_SHORT).show();
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
    public void launchGooglePlacesSearchWidget(SearchBarId id) {

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
                interruptTripPlan();

                // Get the place selected
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.d(TAG, "Place selected: " + place.getName());

                TripPlanPlace tripPlanPlace = new TripPlanPlace(place.getName(), place.getLatLng(),
                        place.getAddress());

                // Make updates according to which search bar was edited
                if (lastEditedSearchBar == SearchBarId.SIMPLE) {

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
     * Called from the onCreateView method of the DetailedSearchBar fragment
     * pre: mode to image button bimap has already been set up
     */
    public void initializeModeButtons() {

        // Get the current selected modes
        Set<TraverseMode> selectedModes = ModeSelectOptions.getSelectedModes();

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
            else {

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        interruptTripPlan();

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
                        interruptTripPlan();

                        // Vibrate
                        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vib.vibrate(30);

                        TraverseMode buttonMode = modeToImageButtonBiMap.inverse().get(v);

                        // Select as normal if already selected as first, else select as first
                        if (buttonMode == ModeSelectOptions.getFirstMode()) {
                            ModeSelectOptions.setFirstMode(null);
                            selectModeButton((ImageButton) v);
                        } else {
                            selectModeButtonAsFirst((ImageButton) v);
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
        ModeSelectOptions.addSelectedMode(mode);

        // Set shaded background, white image
        button.setBackgroundResource(R.drawable.rounded_rectangle_accent);
        button.setColorFilter(getResources().getColor(R.color.white, null));
    }

    /**
     * Helper function for deselecting a mode button
     */
    private void deselectModeButton(ImageButton button) {
        Log.d(TAG, "Mode button was deselected");

        TraverseMode buttonMode = modeToImageButtonBiMap.inverse().get(button);

        // Deselect as first if needed
        if (ModeSelectOptions.getFirstMode() == buttonMode)
            ModeSelectOptions.setFirstMode(null);

        // Deselect button and remove corresponding mode from list of selected modes
        button.setSelected(false);
        ModeSelectOptions.removeSelectedMode(buttonMode);

        // Set colored background, white image
        button.setBackgroundResource(R.drawable.rounded_rectangle_primary);
        button.setColorFilter(Color.WHITE);
    }

    /**
     * Helper function for selecting a mode button as the first mode in the trip plan
     * @param button
     */
    private void selectModeButtonAsFirst(ImageButton button) {

        TraverseMode oldFirstMode = ModeSelectOptions.getFirstMode();
        TraverseMode newFirstMode = modeToImageButtonBiMap.inverse().get(button);

        // Select the old first mode button the regular way
        if (oldFirstMode != null)
            selectModeButton(modeToImageButtonBiMap.get(oldFirstMode));

        // Select the given button as first
        ModeSelectOptions.addSelectedMode(newFirstMode);
        ModeSelectOptions.setFirstMode(newFirstMode);
        button.setBackgroundResource(R.drawable.rounded_rectangle_white);
        button.setColorFilter(getResources().getColor(R.color.colorPrimary, null));
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
     * Helper method that resets the functionality of the map's My Location button
     * to centering the camera on the current location
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
     * Method that signals to the activity to ignore the response of any currently pending
     * trip plan requests
     */
    public void interruptTripPlan() {
        timeOfLastTripPlanInterrupt = System.currentTimeMillis();
    }

    public void interruptTransitRoutesRequest() {
        timeOfLastTransitRoutesInterrupt = System.currentTimeMillis();
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
            Toast.makeText(this, "Location access permission denied", Toast.LENGTH_SHORT).show();
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
                mMap.setPadding(12,12,12,200);
                break;
            case HOME_STOP_SELECTED:
                mMap.setPadding(12,175,12,340);
                break;
            case HOME_BUS_SELECTED:
                mMap.setPadding(12,12,12,12);
                break;
            case TRIP_PLAN:
                mMap.setPadding(12,550,12,200);
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
     * Helper method to remove a marker from the map
     */
    private void removeMarker(Marker marker) {
        if (marker != null) {
            marker.remove();
            marker = null;
        }
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
