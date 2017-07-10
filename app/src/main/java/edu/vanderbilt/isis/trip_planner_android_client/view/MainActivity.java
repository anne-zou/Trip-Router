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


public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        SensorEventListener {

    /** Constants */

    private static final String TAG = MainActivity.class.getName(); // tag for Log messages

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

    private static final double LOCATION_RANGE = 0.0005; // degrees latitude/longitude

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


    /**
     * The set of UI element arrangements or "states" that the activity could be in.
     * These are pushed onto a manually maintained back stack (mStack) as the user navigates the
     * activity to determine which UI operations will need to be performed in order to transition
     * the activity to the next or previous state (see goToNextState() and goToPreviousState(),
     * defined below).
     *
     * The app runs in a single activity, and the UI elements are manipulated via fragment
     * transactions, animations, and view visibility settings.
     *
     * The initial/default state is HOME.
     *
     */
    enum ActivityState {HOME, HOME_PLACE_SELECTED, HOME_STOP_SELECTED, HOME_BUS_SELECTED, TRIP_PLAN, NAVIGATION}

    /**
     * The set of identifiers for the search fields in the activity that the user may click to
     * select a trip plan place (when a search field is clicked, a Google Places Autocomplete search
     * widget will be launched via an intent, and the user will be able to search for and select
     * a place using the widget.)
     *
     * This enum is used to identify which search bar the autocomplete search widget was launched
     * from (and thus, which search bar to update the text of) in the callback method invoked after
     * a place is selected via the widget.
     */
    enum SearchFieldId {SIMPLE, DETAILED_FROM, DETAILED_TO}

    /**
     * Manually-maintained back stack used to keep track of the activity's state
     */
    private ConcurrentLinkedDeque<ActivityState> mStack;

    /**
     * Google Map fragment
     */
    private GoogleMap mMap;

    /**
     * Layout for the sliding-up-panel through which to set the panel state.
     * (possible states are EXPANDED, COLLAPSED, or HIDDEN)
     */
    private SlidingUpPanelLayout mSlidingPanelLayout;

    /**
     * The handle of the sliding-up-panel (the part visible when the layout is in COLLAPSED mode)
     */
    private LinearLayout mSlidingPanelHead;

    /**
     * The component of the sliding up panel attached to the handle from below (not visible in
     * COLLAPSED mode)
     */
    private ScrollView mSlidingPanelTail;

    /**
     * Layout for the row of tab segments representing the different itineraries in the trip plan.
     *
     * The tab row layout will convey which itinerary is currently selected by keeping the
     * corresponding tab segment of the currently selected itinerary highlighted, while keeping
     * the other tab segments not highlighted.
     *
     * To be inserted as the first & topmost child of mSlidingPanelHead in the TRIP_PLAN screen
     */
    private LinearLayout mSelectedItineraryTabRowLayout;

    /**
     * Fragment containing the FROM and TO search fields, the swap-origin-&-destination
     * button, the trip plan options button, and the mode selection tab widget (see
     * detailed-search_bar_layout.xml).
     * By interacting with the fragment's children, the user can modify the parameters of the trip
     * plan request and re-request the trip plan.
     *
     * Shown in the TRIP_PLAN state at the top of the screen.
     */
    private DetailedSearchBarFragment mDetailedSearchBarFragment;

    /**
     * Fragment to display info about the selected transit stop.
     *
     * Shown in the HOME_STOP_SELECTED state at the bottom of the screen.
     *
     * Views depicting the transit stop name and the transit routes that serve the stop
     * are to be inserted into this fragment as children in order to be displayed.
     */
    private TransitStopInfoWindowFragment mTransitStopInfoWindowFragment;

    /**
     * The (not-really)-floating action button used in several different screens of the activity:
     *
     * HOME_PLACE_SELECTED: Serves as the "Go" or "Directions" button. When clicked, a trip plan
     *                      request will be made (using the device's current location as the origin
     *                      and the selected place on the GoogleMap as the destination) and
     *                      the activity will transition to the TRIP_PLAN screen
     * TRIP_PLAN: Serves as the "Go" or "Navigation" button. When clicked, the activity will
     *            transition to the NAVIGATION screen -- if the origin of the current trip plan
     *            matches the current location of the device. Else, the activity will display
     *            a Toast saying: "Cannot launch navigation mode for trip that does not begin
     *            at the current location"
     * NAVIGATION: Serves as the "Exit Navigation" button. When clicked, the activity will exit
     *             the NAVIGATION screen and transition back to the TRIP_PLAN screen displaying
     *             the currently selected trip plan and itinerary
     *
     */
    private ImageButton mFab;

    /**
     * The arrow button on the right of the floating action button in the TRIP_PLAN screen.
     * Selects & displays the next itinerary in mItinerary list.
     */
    private ImageButton mRightArrowButton;

    /**
     * The arrow button on the left of the floating action button in the TRIP_PLAN screen.
     * Selects & displays the previous itinerary in mItinerary list.
     */
    private ImageButton mLeftArrowButton;

    /**
     * The white, rounded rectangular backdrop for the simple search field, shown in the
     * HOME and HOME_STOP_SELECTED screens.
     */
    private CardView mSimpleSearchBar;

    /**
     * The text view for the simple search field, shown in the HOME and HOME_STOP_SELECTED screens.
     * Displays "Where to?" by default.
     */
    private TextView mSimpleSearchBarText;

    /**
     * Field indicating which search field was last clicked by the user. Identifies which search
     * bar the Google autocomplete search widget was launched from (and thus, which search bar to
     * update the text of) in the callback method invoked after a place is selected via the widget.
     */
    private SearchFieldId lastEditedSearchField;

    /**
     * The origin of the current trip plan. Meaningful in the TRIP_PLAN and NAVIGATION states.
     */
    private TripPlanPlace mOrigin = null;

    /**
     * The destination of the current trip plan. Meaningful in the TRIP_PLAN and NAVIGATION states.
     */
    private TripPlanPlace mDestination = null;

//    /**
//     * Marker on the map indicating the origin location of the current trip plan.
//     * Shown in the TRIP_PLAN and NAVIGATION screens.
//     */
//    private Marker mOriginMarker;

    /**
     * Marker on the map indicating the destination location of the current trip plan.
     * Shown in the TRIP_PLAN and NAVIGATION screens.
     */
    private static Marker mDestinationMarker;

    /**
     * Marker on the map indicating the current selected location on the map (may be an arbitrary
     * LatLng or a GoogleMap PointOfInterest).
     * Shown in the HOME_PLACE_SELECTED screen.
     */
    private static Marker mPlaceSelectedMarker;

    /**
     * The list of itineraries in the current trip plan. Meaningful in the TRIP_PLAN and
     * NAVIGATION states.
     */
    private static List<Itinerary> mItineraryList;

    /**
     * The index of the currently selected itinerary within mItineraryList. Meaningful in the
     * TRIP_PLAN and NAVIGATION states
     */
    private static int mCurItineraryIndex;

    /**
     * The polyline objects highlighting the currently selected itinerary on the GoogleMap.
     * Meaningful in the TRIP_PLAN and NAVIGATION states.
     */
    private static List<Polyline> mPolylineList;

    /**
     * The list of points along the currently selected itinerary. To be used to determine
     * whether or not the user is adhering to the itinerary in NAVIGATION mode.
     */
    private static List<LatLng> mItineraryPointList; // todo probably change to list of lists of leg points

    /**
     * The list of markers showing the next navigation instruction at certain points/junctions
     * along the currently selected itinerary. Shown in the NAVIGATION screen.
     */
    private static List<Marker> mNavigationMarkerList;

    /**
     * A list of all the transit stops in the city
     */
    private static List<Stop> mCityTransitStops;

    /**
     * The list of markers representing all teh transit stops in the city.
     * Shown in the HOME, HOME_PLACE_SELECTED, HOME_STOP_SELECTED, and TRIP_PLAN states
     */
    private static Map<Marker, String> mCityTransitStopMarkers;

    /**
     * BiMap mapping TraverseModes (WALK, BUS, BICYCLE, CAR, etc.) to their corresponding mode
     * buttons in the detailed search bar in the TRIP_PLAN screen.
     */
    private static BiMap<TraverseMode, ImageButton> modeToImageButtonBiMap;

    /**
     * Sensor manager for registering listeners to listen for changes in the device's cardinal
     * orientation.
     */
    private static SensorManager mSensorManager;

    /**
     * Most recent accelerometer reading for the device as updated by the SensorEventListener
     * Used to help calculate the device's cardinal orientation.
     */
    private static final float[] mAccelerometerReading = new float[3];

    /**
     * Most recent magnetometer reading for the device as updated by the SensorEventListener
     * Used to help calculate the device's cardinal orientation.
     */
    private static final float[] mMagnetometerReading = new float[3];

    /**
     * The zoom level of the GoogleMap as last updated by its OnCameraMoveListener
     */
    private static volatile double mLastZoomLevel;


    /**
     * Invoked when the activity is created
     * Performs some setup operations for the application
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Activity created");

        // Set up google play services for the activity
        Controller.setUpGooglePlayServices(this);

        // Set up UI elements for the activity
        setUpMap();
        setUpDrawer();
        setUpSimpleSearchBar();
        setUpModes();
        setUpSlidingPanel();
        setUpFloatingButtons();

        // Set up the sensor manager for the activity
        setUpSensorManager();
        // Set up the manually maintained back stack for te activity
        setUpStateStack();
        // Set up mode utility class
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
        // Will trigger the OnMapReady() callback when the map is ready to be used (implemented below)
        mapFragment.getMapAsync(this);
    }


    /**
     * Helper method for setting up the navigation drawer
     * See res/menu/activity_main_drawer.xml for the list of menu items
     */
    private void setUpDrawer() {

        // Get drawer layout and navigation view
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Highlight the 'Planner' menu item in the navigation view (this is the first item)
        navigationView.getMenu().getItem(0).setChecked(true);

        // Set the listener for when an item in the nav drawer is selected
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    /**
                     * Invoked when a menu item in the nav drawer is selected
                     * @param item the menu item that was selected
                     * @return true to display the item as the selected item
                     */
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
     * Helper method for setting up the simple search bar that appears on the HOME and
     * HOME_STOP_SELECTED screens of the activity
     */
    private void setUpSimpleSearchBar() {

        // Get the card view and the textview that the search bar is composed of
        mSimpleSearchBar = (CardView) findViewById(R.id.simple_search_bar_card_view);
        mSimpleSearchBarText = (TextView) findViewById(R.id.simple_search_bar_text_view);

        // Set the listener for when the search bar is clicked
        mSimpleSearchBarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the intent for the Google autocomplete widget
                launchGooglePlacesSearchWidget(SearchFieldId.SIMPLE);
            }
        });

        // Get the hamburger image at
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
     * Helper method for initializing the selected traverse modes for a trip plan
     */
    private void setUpModes() {

        // Create the mode-to-button bimap.
        // The entries will be added to the bimap upon inflation of the DetailedSearchBarFragment,
        // since the mode buttons are its children and must be inflated first.
        modeToImageButtonBiMap = HashBiMap.create();

        // TODO: Grab the actual default modes set by the user
        // Initialize default modes & select the default modes
        Controller.setDefaultModes(new HashSet<TraverseMode>());
    }

    /**
     * Helper method for getting the sliding panel layout and its components
     */
    private void setUpSlidingPanel() {
        mSlidingPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingPanelHead = (LinearLayout) findViewById(R.id.sliding_panel_head);
        mSlidingPanelTail = (ScrollView) findViewById(R.id.sliding_panel_tail);
    }

    /**
     * Helper method for getting the floating action button arrow buttons, and setting
     * the on click listeners for the arrow buttons.
     *
     * All 3 buttons have visibility & clickability initially set to GONE or
     * INVISIBLE & unclickable (see trip_plan_floating_buttons_layout.xml)
     */
    private void setUpFloatingButtons() {
        mFab = (ImageButton) findViewById(R.id.fab);
        mLeftArrowButton = (ImageButton) findViewById(R.id.left_button);
        mRightArrowButton = (ImageButton) findViewById(R.id.right_button);

        mLeftArrowButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Invoked automatically when the left arrow button view is clicked, if it is clickable
             * @param v the view that was clicked
             */
            @Override
            public void onClick(View v) {
                onSwipeSlidingPanelRight();
            }
        });

        mRightArrowButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Invoked automatically when the right arrow button view is clicked, if it is clickable
             * @param v the view that was clicked
             */
            @Override
            public void onClick(View v) {
                onSwipeSlidingPanelLeft();
            }
        });
    }

    /**
     * Helper method for getting the sensor manager
     */
    private void setUpSensorManager() {
        // SensorEventListeners are to be later set for this sensor manager when we want to start
        // receiving sensor event updates
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    /**
     * Invoked automatically when the sensor's accuracy changes
     * Overrides method from the SensorEventListener class
     * @param sensor the sensor whose accuracy changed
     * @param accuracy the new accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Invoked automatically when the sensor senses a change
     * Overrides method from the SensorEventListener class
     * @param event the sensor event encapsulating the values of the new sensor reading
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
     * Helper method for creating & initializing the back stack
     */
    private void setUpStateStack() {
        // Create the concurrent linked deque to be used as a stack
        mStack = new ConcurrentLinkedDeque<>();
        // Initialize the state to HOME
        setState(ActivityState.HOME);
    }

    /**
     * Callback triggered automatically when the map is ready to be used.
     * Overrides method from the OnMapReadyCallback class.
     * Configures what is shown in the GoogleMap view & sets listeners for camera moves,
     * marker clicks, map clicks, and point of interest clicks.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        UiSettings settings = mMap.getUiSettings();
        settings.setCompassEnabled(true);
        settings.setZoomControlsEnabled(true);
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


        // Set the on camera move listener for the map
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            /**
             * Callback triggered automatically when the user zooms or pans the map, independent
             * of the ActivityState. Shows or hides the appropriate markers based on the change
             * in zoom level.
             */
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


        // Set the on point of interest click listener for the map
        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {

            /**
             * Callback triggered automatically when the user clicks on a point of interest,
             * independent of the ActivityState.
             * @param pointOfInterest the point of interest that was clicked
             */
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {

                if (!isAHomeState(getState()))
                    return;

                Controller.interruptOngoingRoutesRequests();

                // Center camera on the selected POI
                mMap.animateCamera(CameraUpdateFactory.newLatLng(pointOfInterest.latLng));

                // Get the Place object representing the poi and update the ActivityState & upon
                // response from the server via the callback method
                // updateUIonGetPlaceByIdRequestResponse(), defined below.

                Controller.requestPlaceById(MainActivity.this, pointOfInterest.placeId);

                // DO NOT add the place selected marker to the map or transition to the
                // HOME_PLACE_SELECTED state yet in case the request fails (in which case the
                // callback method updateUIonGetPlaceByIdRequestFailure(), defined below, will be
                // invoked)

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

                // Transition to the HOME_PLACE_SELECTED screen
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

        // Successfully retrieved the Place object for the point of interest selected!
        Log.i(TAG, "Point of interest Place found: "
                + myPlace.getName());

        // Transition to HOME_PLACE_SELECTED screen
        goToNextScreen(ActivityState.HOME_PLACE_SELECTED);

        // Draw the selected place marker, TODO finish this comment
        selectPlaceOnMap(new TripPlanPlace(myPlace.getName(),
                myPlace.getLatLng(), myPlace.getAddress()));
    }

    /**
     * Callback invoked from the controller layer upon failure of getting a place by id
     */
    public void updateUIonGetPlaceByIdRequestFailure() {

        // Do not transition to HOME_PLACE_SELECTED screen
        Log.e(TAG, "Could not find Point of interest");
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

        // Show the markers if we are not in navigation mode and are at or above the min zoom level
        if (getState() != ActivityState.NAVIGATION &&
                mMap.getCameraPosition().zoom >= MIN_SHOW_MARKER_ZOOM_LEVEL)
            if (mCityTransitStopMarkers != null)
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

            // Configure sliding panel head
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
            if (mCityTransitStopMarkers != null)
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
            mNavigationMarkerList = new LinkedList<>();
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
                if (mNavigationMarkerList != null)
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

                // Remove navigation step & bus stop markers
                if (mNavigationMarkerList != null) {
                    for (Marker marker : mNavigationMarkerList)
                        marker.remove();
                    mNavigationMarkerList.clear();
                }

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

                // Show the city transit stop markers if we are above minimum zoom level
                if (mMap.getCameraPosition().zoom >= MIN_SHOW_MARKER_ZOOM_LEVEL)
                    if (mCityTransitStopMarkers != null)
                        for (Marker marker : mCityTransitStopMarkers.keySet())
                            marker.setVisible(true);

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
        Controller.addToSearchHistory(this, mOrigin.getName(), mDestination.getName(),
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
        mSelectedItineraryTabRowLayout = new LinearLayout(MainActivity.this);
        mSelectedItineraryTabRowLayout.setOrientation(LinearLayout.HORIZONTAL);
        mSelectedItineraryTabRowLayout.setLayoutParams(new LinearLayout
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
            mSelectedItineraryTabRowLayout.addView(view, i);
        }

        // Show highlighted tab bar on sliding panel handle
        mSlidingPanelHead.addView(mSelectedItineraryTabRowLayout, 0);

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
        setLastEditedSearchField(id);

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
                if (lastEditedSearchField == SearchFieldId.SIMPLE) {

                    if (getState() == ActivityState.HOME_STOP_SELECTED) {
                        ArrayList<TripPlanPlace> intermediateStops = new ArrayList<>();
                        intermediateStops.add(0, new TripPlanPlace(mPlaceSelectedMarker.getTitle(),
                                mPlaceSelectedMarker.getPosition()));
                        planTrip(new TripPlanPlace(), tripPlanPlace, intermediateStops);
                    } else {
                        planTrip(new TripPlanPlace(), tripPlanPlace);
                    }

                } else if (lastEditedSearchField == SearchFieldId.DETAILED_FROM) {

                    // Set the text in the detailed from search bar
                    mDetailedSearchBarFragment.setOriginText(place.getName());
                    planTrip(tripPlanPlace, mDestination);

                } else if (lastEditedSearchField == SearchFieldId.DETAILED_TO) {

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
            mSelectedItineraryTabRowLayout.getChildAt(mCurItineraryIndex)
                    .setBackground(getDrawable(R.drawable.rectangle_selected));
            mSelectedItineraryTabRowLayout.getChildAt(mCurItineraryIndex - 1)
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
            mSelectedItineraryTabRowLayout.getChildAt(mCurItineraryIndex)
                    .setBackground(getDrawable(R.drawable.rectangle_selected));
            mSelectedItineraryTabRowLayout.getChildAt(mCurItineraryIndex + 1)
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

    private void setLastEditedSearchField(SearchFieldId id) {
        lastEditedSearchField = id;}

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