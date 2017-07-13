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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

import edu.vanderbilt.isis.trip_planner_android_client.controller.ParameterRunnable;
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

    public static final String LOADING_MESSAGE = "LOADING RESULTS...";

    public static final String TRIP_PLAN_FAILURE_MESSAGE = "Trip plan failed";

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

    private static final int PERMISSIONS_REQUEST_CODE_LOCATION = 99;

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
    private static ConcurrentLinkedDeque<ActivityState> mStack;

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
     * Fragment to allow the user to search for a place and to provide search suggestions
     * based on the contents of the search bar.
     *
     * Can be launched from the HOME, HOME_STOP_SELECTED, or TRIP_PLAN screen.
     * Should be launched if and only if a search bar is clicked.
     */
    private SearchViewFragment mSearchViewFragment;

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
    private static SearchFieldId lastEditedSearchField;

    /**
     * The origin of the current trip plan. Meaningful in the TRIP_PLAN and NAVIGATION states.
     */
    private static TripPlanPlace mOrigin = null;

    /**
     * The destination of the current trip plan. Meaningful in the TRIP_PLAN and NAVIGATION states.
     */
    private static TripPlanPlace mDestination = null;

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
     * A map of the markers representing all the transit stops in the city to their stopIds
     * Markers are visible in the HOME, HOME_PLACE_SELECTED, HOME_STOP_SELECTED, and TRIP_PLAN states
     */
    private static Map<Marker, String> mCityTransitStopMarkersToStopIdsMap;

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
        setContentView(R.layout.navigation_menu_main);

        Log.d(TAG, "Activity created");

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
     * See res/menu/activity_main_drawer.xml for the layout & the list of menu items
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
                     * Callback invoked when a menu item in the nav drawer is selected
                     * @param item the menu item that was selected
                     * @return true to display the item as the selected item
                     */
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        // Select the menu item if it was not already selected
                        if (item.isChecked()) // close the drawer if the item was already selected
                            return false;
                        item.setChecked(true);

                        // Transition to the screen corresponding to the clicked menu item
                        int id = item.getItemId();
                        // TODO: Implement settings screen
                        if (id == R.id.nav_planner) {

                        } else if (id == R.id.nav_settings) {

                        }

                        // Close the navigation drawer
                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });
    }


    /**
     * Helper method for setting up the simple search bar, which appears on the HOME and
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
                launchSearchViewFragment(SearchFieldId.SIMPLE);
            }
        });

        // Get the hamburger imageview in the simple search bar
        ImageView burger = (ImageView) findViewById(R.id.simple_search_bar_burger);
        burger.setAlpha(LIGHT_OPACITY_PERCENTAGE); // set opacity of the image
        // Attach the hamburger imageview to the navigation drawer
        burger.setOnClickListener(new View.OnClickListener() {
            /**
             * Opens the navigation drawer when clicked
             * @param v the view that was clicked
             */
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

        // TODO: Grab the actual default modes set by the user from a database & select them via the controller
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
     * Each button should either have visibility set to GONE or visibility and clickability set
     * to INVISIBLE & false (see trip_plan_floating_buttons_layout.xml)
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
     * Helper method for creating & initializing the back stack
     */
    private void setUpStateStack() {
        // Create the concurrent linked deque to be used as a stack
        mStack = new ConcurrentLinkedDeque<>();
        // Initialize the state to HOME
        setState(ActivityState.HOME);
    }


    /**
     * Callback triggered automatically when the map is ready to be used.(Overridden method from
     * the OnMapReadyCallback class.)
     * Configures the UI settings, padding, and style for the GoogleMap & sets the
     * OnCameraMoveListener, the OnPoiClickListener, the OnMapClickListener, and the
     * OnMarkerClickListener for the GoogleMap.
     * @param googleMap the GoogleMap that is ready
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Acquire the GoogleMap
        mMap = googleMap;

        // Set up google play services for the activity
        // Map wil be updated by the first received location update
        Controller.setUpGooglePlayServices(this);

        // Configure map UI settings
        UiSettings settings = mMap.getUiSettings();
        settings.setCompassEnabled(true); // show compass
        settings.setTiltGesturesEnabled(false); // disable tilt gestures
        settings.setMapToolbarEnabled(false); // disable toolbar, which gives quick access
        // to the Google Maps mobile app

        // Set the map padding for the home screen
        setMapPadding(ActivityState.HOME);

        // Hide built-in transit stop icons on map using raw json to configure the map's style
        try {
            // Parse the json defining the style after getting it from a resource file
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.hide_transit_stops_style_json));
            if (!success)
                Log.e(TAG, "Style parsing failed.");
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Request transit stops for the city from our trip planner server
        LatLng cityCenter = new LatLng(MY_CITY_LATITUDE, MY_CITY_LONGITUDE);
        Controller.requestTransitStopsWithinRadius(this, cityCenter, MY_CITY_RADIUS);
        // Will invoke either updateUIonTransitStopsRequestSuccessful() or
        // updateUIonTransitStopsRequestUnsuccessful() upon resoonse

        // Set the on camera move listener for the map
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            /**
             * Callback triggered automatically by the GoogleMap associated with this listener when
             * the user zooms or pans the map.
             * Overridden to show or hide the appropriate markers based on the change in zoom level.
             */
            @Override
            public void onCameraMove() {

                // Get new zoom level
                double zoom = mMap.getCameraPosition().zoom;

                // If we zoomed out below min level, hide relevant markers
                if (zoom < MIN_SHOW_MARKER_ZOOM_LEVEL
                        && mLastZoomLevel > zoom && mLastZoomLevel >= MIN_SHOW_MARKER_ZOOM_LEVEL) {

                    Log.d(TAG, "Zoomed in below mininum level for showing marker");

                    // If in NAVIGATION state, hide the navigation direction & transit stop markers
                    if (getState() == ActivityState.NAVIGATION) {
                        if (mNavigationMarkerList != null)
                            for (Marker marker : mNavigationMarkerList)
                                marker.setVisible(false);
                    } else { // If in some other state, hide the city transit stop markers
                        Log.d(TAG, "Hiding city transit stops");
                        if (mCityTransitStopMarkersToStopIdsMap != null)
                            for (Marker marker : mCityTransitStopMarkersToStopIdsMap.keySet())
                                marker.setVisible(false);
                    }

                // If we zoomed above min level, show relevant markers
                } else if (zoom >= MIN_SHOW_MARKER_ZOOM_LEVEL
                        && mLastZoomLevel < zoom && mLastZoomLevel < MIN_SHOW_MARKER_ZOOM_LEVEL) {

                    Log.d(TAG, "Zoomed in above mininum level for showing marker");

                    // If in NAVIGATION state, show the navigation direction & transit stop markers
                    if (getState() == ActivityState.NAVIGATION) {
                        if (mNavigationMarkerList != null)
                            for (Marker marker : mNavigationMarkerList)
                                marker.setVisible(true);
                    } else { // If in some other state, show the city transit stop markers
                        Log.d(TAG, "Showing city transit stops");
                        if (mCityTransitStopMarkersToStopIdsMap != null)
                            for (Marker marker : mCityTransitStopMarkersToStopIdsMap.keySet())
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
             * Callback triggered automatically by the GoogleMap associated with this listener
             * when the user clicks on a point of interest.
             * @param pointOfInterest the point of interest that was clicked
             */
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {

                // If the activity is in a home state
                if (isAHomeState(getState())) {

                    // Interrupt any ongoing request for routes that service a particular transit
                    // stop.(Any responses to any ongoing routes requests have now been made obsolete
                    // since a new place on the map has been clicked.)
                    Controller.interruptOngoingRoutesRequests();

                    // Center camera on the selected POI
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(pointOfInterest.latLng));

                    // Remove previous place selected marker
                    removeMarker(mPlaceSelectedMarker);

                    // Request the Place object representing the poi and update the activity
                    // upon response via the callback runnables
                    Controller.requestPlaceById(pointOfInterest.placeId,
                            new ParameterRunnable<Place>() {
                                /**
                                 * Respond to when we receive the Place
                                 */
                                @Override
                                public void run() {
                                    // Get the Place we requested
                                    Place place = getParameterObject();

                                    // Update the UI based on the Place object received
                                    updateUIonPoiPlaceReceived(place);
                                }
                            },
                            new Runnable() {
                                /**
                                 * Respond to when the request failed
                                 */
                                @Override
                                public void run() {
                                    // Update the UI for POI request failed
                                    updateUIonPoiPlaceRequestFailure();
                                }
                            });

                    // DO NOT add the place selected marker to the map or transition to the
                    // HOME_PLACE_SELECTED state yet in case the request fails
                }

            }
        });

        // Set the on map click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            /**
             * Callback triggered automatically by the GoogleMap associated with this listener
             * when the user clicks the map.
             * @param latLng the location on the map that was clicked
             */
            @Override
            public void onMapClick(LatLng latLng) {

                Log.i(TAG, "Location on map clicked: " + latLng.toString());

                // If the activity is in a home state
                if (isAHomeState(getState())) {

                    // Interrupt any ongoing request for routes that service a particular transit
                    // stop. (Any responses to any ongoing routes requests have now been made
                    // obsolete since a new place on the map has been clicked.)
                    Controller.interruptOngoingRoutesRequests();

                    // Center camera on the selected location
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Transition to the HOME_PLACE_SELECTED screen
                    goToNextScreen(ActivityState.HOME_PLACE_SELECTED);

                    // Get a string representing the LatLng of the location with truncated decimals
                    NumberFormat formatter = new DecimalFormat("##0.0000");
                    String latLngString = "("
                            + formatter.format(Math.round(latLng.latitude * 10000) / 10000.0)
                            + ", " + formatter.format(Math.round(latLng.longitude * 10000) / 10000.0)
                            + ")";

                    // Perform the necessary steps to select the place on the map
                    selectPlaceOnMap(new TripPlanPlace(latLngString, latLng, "Unnamed location"));
                }
            }
        });


        // Set the on marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                // Interrupt any ongoing request for routes that service a particular transit stop.
                // (Any responses to any ongoing routes requests have now been made obsolete
                // since a new marker on the map has been clicked.)
                Controller.interruptOngoingRoutesRequests();

                // If the activity is in a home state and the marker clicked was a transit stop marker:
                if (isAHomeState(getState()) && mCityTransitStopMarkersToStopIdsMap.keySet().contains(marker)) {

                    // Transition to HOME_STOP_SELECTED (creates/shows the info window fragment)
                    goToNextScreen(ActivityState.HOME_STOP_SELECTED);

                    // Add place selected marker to the map
                    removeMarker(mPlaceSelectedMarker);
                    mPlaceSelectedMarker = mMap.addMarker(new MarkerOptions()
                            .position(marker.getPosition()));

                    // Move the camera to the selected place
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                    // Get the stopId of the selected transit stop
                    String stopId = mCityTransitStopMarkersToStopIdsMap.get(marker);

                    // Clear the info window fragment
                    mTransitStopInfoWindowFragment.clear();
                    // Show the name of the transit stop in the info window fragment
                    mTransitStopInfoWindowFragment.setTransitStopNameText(marker.getTitle());
                    // Request more information about the stop
                    mTransitStopInfoWindowFragment.requestStopRoutes(stopId);
                    // Invokes one of the following callbacks defined below:
                    // updateUIonRoutesRequestResponse(), updateUIonRoutesRequestFailure()
                }

                return true;
            }
        });
    }

    /**
     * Callback invoked from the controller layer upon successful receipt of response
     * for getting a place by id
     * @param myPlace the Place selected
     */
    public void updateUIonPoiPlaceReceived(Place myPlace) {

        // Successfully retrieved the Place object for the point of interest selected!
        Log.i(TAG, "Point of interest Place found: "
                + myPlace.getName());

        // Transition to HOME_PLACE_SELECTED screen
        goToNextScreen(ActivityState.HOME_PLACE_SELECTED);

        // Perform the necessary steps to select the place on the map
        selectPlaceOnMap(new TripPlanPlace(myPlace.getName(),
                myPlace.getLatLng(), myPlace.getAddress()));
    }

    /**
     * Callback invoked from the controller layer upon failure of getting a place by id
     */
    public void updateUIonPoiPlaceRequestFailure() {

        // Do not transition to HOME_PLACE_SELECTED screen
        Log.e(TAG, "Could not find Point of interest");
    }

    /**
     * Callback invoked from the controller layer upon successful receipt of response
     * for a transit stops request
     * @param stops
     */
    public void updateUIonTransitStopsRequestResponse(List<Stop> stops) {

        // Save the list of the city's transit stops
        mCityTransitStops = stops;

        Log.d(TAG, "Number of bus stops:" + mCityTransitStops.size());

        // Initialize a HashMap to map transit stop markers to stopIds
        mCityTransitStopMarkersToStopIdsMap = new HashMap<>();

        // Save the transit stop markers
        for (Stop stop : mCityTransitStops) {
            mCityTransitStopMarkersToStopIdsMap.put(
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
            if (mCityTransitStopMarkersToStopIdsMap != null)
                for (Marker marker : mCityTransitStopMarkersToStopIdsMap.keySet())
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
     * @param locationServicesAreEnabled true if location services were successfully enabled
     */
    public void updateUIOnGoogleAPIClientConnected(boolean locationServicesAreEnabled) {

        if (locationServicesAreEnabled) {
            if (getState() != ActivityState.NAVIGATION) { // if not in navigation mode
                // Start low frequency location updates
                Controller.startLowAccuracyLocationUpdates(this);
            } else { // if in navigation mode
                // Start high frequency location updates
                Controller.startHighAccuracyLocationUpdates(this);
            }
        }
    }

    /**
     * Handles back button press
     */
    @Override
    public void onBackPressed() {

        Log.d(TAG, "Back pressed");

        // Close drawer if open
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        // Else collapse sliding panel if expanded
        if (mSlidingPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }

        // Else go to previous state
        goToPreviousScreen();
    }


    /**
     * Helper method to facilitate transition to the next screen in the activity
     * @param newState
     */
    public void goToNextScreen(ActivityState newState) {

        ActivityState oldState = getState();

        // Do nothing if we are already in the new state
        if (oldState == newState)
            return;

        // Home --> HOME_STOP_SELECTED
        if (isAHomeState(oldState) && newState == ActivityState.HOME_STOP_SELECTED) {

            // Do nothing & exit already in HOME_STOP_SELECTED mode
            if (oldState == ActivityState.HOME_STOP_SELECTED)
                return;

            // Make HOME_STOP_SELECTED the only state above HOME in the state stack
            while (getState() != ActivityState.HOME)
                onBackPressed();
            setState(ActivityState.HOME_STOP_SELECTED);

            // Hide sliding panel layout
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

            // Set map padding
            setMapPadding(ActivityState.HOME_STOP_SELECTED);

            // Create the info window fragment if it does not already exist
            if (mTransitStopInfoWindowFragment == null)
                mTransitStopInfoWindowFragment = new TransitStopInfoWindowFragment();

            // Initialize a fragment transaction to show the info window
            FragmentManager fragmentManager = getFragmentManager();
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
                    if (tripStartLocation == null || myLocation == null ||
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
            if (mCityTransitStopMarkersToStopIdsMap != null)
                for (Marker marker : mCityTransitStopMarkersToStopIdsMap.keySet())
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



            // Create the navigation walkstep and transit stop markers:

            mNavigationMarkerList = new LinkedList<>();

            // Loop through the legs of the currently selected itinerary
            for (Leg leg : mItineraryList.get(mCurItineraryIndex).getLegs()) {

                // Walk step markers:

                // Loop through the walksteps of each leg & create a marker for each one
                for (WalkStep walkStep : leg.getSteps()) {

                    // Use the relative direction for the walkstep instruction if available, or
                    // the absolute direction if not
                    String walkStepDirection = walkStep.getRelativeDirection() == null ?
                            walkStep.getAbsoluteDirection().toString()
                            : walkStep.getRelativeDirection().toString();

                    // Get a speech balloon bitmap that shows the walkstep instruction
                    // in the format: "<relative/absolution direction> on <street name>
                    Bitmap instructionBitmap = createWalkStepBitmap(walkStepDirection
                            + " on " + walkStep.getStreetName(), true);

                    // Add a marker to the map at the walkstep location using the bitmap as the
                    // icon, and save the marker to mNavigationMarkerList
                    mNavigationMarkerList.add(mMap.addMarker(new MarkerOptions()
                            .title(walkStep.getRelativeDirection() + " on "
                                    + walkStep.getStreetName())
                            .anchor(1f, 1f)
                            .position(new LatLng(walkStep.getLat(), walkStep.getLon()))
                            .icon(BitmapDescriptorFactory.fromBitmap(instructionBitmap))
                            .visible(false))
                    );

                }

                // Transit stop markers:

                if (ModeUtil.hasFixedStops(leg.getMode())) {
                    // If the traverse mode of the leg has fixed stops (for example, BUS)
                    // Loop through the intermediate stops of the leg
                    if (leg.getIntermediateStops() != null)
                        for (edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Place place
                                : leg.getIntermediateStops()) {

                            // Create the bitmap to use for the transit stop marker
                            Drawable d = getDrawable(R.drawable.ic_bus_stop);
                            Bitmap b = drawableToBitmap(d);

                            // Add a marker to the map at the transit stop location using the
                            // bitmap as the icon, and save the marker to mNavigationMarkerList
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

            // Show the walkstep & transit stop markers if the map is at or above min zoom level
            if (mMap.getCameraPosition().zoom >= MIN_SHOW_MARKER_ZOOM_LEVEL)
                if (mNavigationMarkerList != null)
                    for (Marker marker : mNavigationMarkerList)
                        marker.setVisible(true);

            // Start high accuracy location requests & register sensor listeners
            Controller.startHighAccuracyLocationUpdates(this);
            registerSensorListeners(); // invokes onSensorChanged which updates camera

            // Update the camera based on sensor and location readings
            updateNavigationModeCamera();
            return;
        }

        throw new IllegalArgumentException("Invalid state transition: " + getState() + " to " +
                newState);
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

                // Revert map shape & center/zoom to current location if accessible
                setMapPadding(ActivityState.HOME);
                LatLng currentLocation = Controller.getCurrentLocation(this);
                if (currentLocation != null) {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(currentLocation)
                                    .zoom(DEFAULT_ZOOM_LEVEL)
                                    .build()
                            )
                    );
                }

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
                mSimpleSearchBarText.setText(getResources().getText(R.string.where_to));

                // Focus camera back to current location
                LatLng myCurLocation = Controller.getCurrentLocation(this);
                if (myCurLocation != null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(myCurLocation));

                break;

            case HOME_STOP_SELECTED:

                // HOME_STOP_SELECTED --> HOME

                // Remove place selected marker
                removeMarker(mPlaceSelectedMarker);

                // Set map padding
                setMapPadding(ActivityState.HOME);

                // Focus camera back to current location
                LatLng currLocation = Controller.getCurrentLocation(this);
                if (currLocation != null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(currLocation));
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

                        if (tripStartLocation == null || myLocation == null
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
                    if (mCityTransitStopMarkersToStopIdsMap != null)
                        for (Marker marker : mCityTransitStopMarkersToStopIdsMap.keySet())
                            marker.setVisible(true);

                // Show detailed search bar
                super.onBackPressed();

                break;

            default:
                super.onBackPressed();
        }
    }

    /**
     * Plans trip with no intermediate sotps departing now
     * @pre activity is in HOME, HOME_STOP_SELECTED, HOME_PLACE_SELECTED, or TRIP_PLAN state
     * @param origin point of origin for the trip
     * @param destination destination for the trip
     * @return true if the trip plan request was made, false otherwise
     */
    public boolean planTrip(@NonNull TripPlanPlace origin,
                            @NonNull TripPlanPlace destination) {
        return planTrip(origin, destination, null, null, false);
    }

    /**
     * Plans trip with intermediate stops departing now
     * @pre activity is in HOME, HOME_STOP_SELECTED, HOME_PLACE_SELECTED, or TRIP_PLAN state
     * @param origin point of origin for the trip
     * @param destination destination for the trip
     * @param intermediateStops intermediate stops for the trip; use null if none
     * @return true if the trip plan request was made, false otherwise
     */
    public boolean planTrip(@NonNull TripPlanPlace origin,
                            @NonNull TripPlanPlace destination,
                            @Nullable List<TripPlanPlace> intermediateStops) {
        return planTrip(origin, destination, intermediateStops, null, false);
    }

    /**
     * Plans trip with no intermediate stops departing or arriving at a specified time
     * @param origin point of origin for the trip
     * @param destination destination for the trip
     * @param time time by which the trip should depart or arrive by; use null for current time
     * @param departOrArriveBy false for depart time, true for arrive time
     * @return true if the trip plan request was made, false otherwise
     */
    public boolean planTrip(@NonNull TripPlanPlace origin,
                            @NonNull TripPlanPlace destination,
                            @Nullable Date time, boolean departOrArriveBy) {
        return planTrip(origin, destination, null, time, departOrArriveBy);
    }

    /**
     * Makes a request to the trip planner server for a list of itineraries (will invoke
     * updateUIonTripPlanResponse() or updateUIonTripPlanFailure() upon response from the server).
     *
     * Transitions the activity to the TRIP_PLAN state if needed.
     * Gets & sets the location of the origin and destination for the trip plan.
     * Clears/resets the elements in the TRIP_PLAN screen.
     * Checks if the selected modes are appropriate to request a trip plan (exits if not).
     *
     * @pre activity is in HOME, HOME_STOP_SELECTED, HOME_PLACE_SELECTED, or TRIP_PLAN state
     * @param origin point of origin for the trip
     * @param destination destination for the trip
     * @param intermediateStops intermediate stops for the trip; use null if none
     * @param time time by which the trip should depart or arrive by; use null for current time
     * @param departOrArriveBy false for depart time, true for arrive time
     * @return true if the trip plan request was made, false otherwise
     */
    public boolean planTrip(@NonNull final TripPlanPlace origin,
                            @NonNull final TripPlanPlace destination,
                            @Nullable List<TripPlanPlace> intermediateStops,
                            @Nullable Date time, boolean departOrArriveBy) {

        // Set up the trip plan screen
        if (getState() != ActivityState.TRIP_PLAN)
            goToNextScreen(ActivityState.TRIP_PLAN);

        // If the origin or destination TripPlanPlace is supposed to use the current location,
        // get and assign the current location to the TripPlanPlace
        if (origin.shouldUseCurrentLocation())
            origin.setLocation(Controller.getCurrentLocation(this));
        if (destination.shouldUseCurrentLocation())
            destination.setLocation(Controller.getCurrentLocation(this));

        // Save origin and destination
        mOrigin = origin;
        mDestination = destination;

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

        // If the origin or destination location is null, show an error message on the sliding
        // panel head and do not plan the trip
        if (origin.getLocation() == null || destination.getLocation() == null) {
            showOnSlidingPanelHead("Could not get current device location");
            return false;
        }

        // Add the trip plan to search history
        Controller.addToSearchHistory(this, mOrigin.getName(), mDestination.getName(),
                mOrigin.getLocation(), mDestination.getLocation(),
                mOrigin.getAddress(), mDestination.getAddress(),
                Controller.getSelectedModesString(), (new Date()).getTime());

        // CHECK IF MODES ARE APPROPRIATE FOR A TRIP PLAN:

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
        showOnSlidingPanelHead(LOADING_MESSAGE);

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
     * Callback invoked upon receipt of response for a trip plan request.
     * Updates mItineraryList, calls displayItinerary() on the first itinerary in the trip plan,
     * shows the origin and destination markers for the trip plan on the map, and adds a
     * highlighted tab row to the sliding panel head to indicate that the 1st itinerary of the
     * trip plan is currently selected.
     *
     * @param tripPlan the trip plan received
     */
    public void updateUIonTripPlanResponse(TripPlan tripPlan) {

        // Handle case where results are null or empty
        if (tripPlan == null
                || tripPlan.getItineraries() == null
                || tripPlan.getItineraries().isEmpty()) {
            Log.d(TAG, "OTP request result was empty");
            showOnSlidingPanelHead("No results");
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

        // If the trip plan has n itineraries, create and add n tabs to the layout
        for (int i = 0; i < mItineraryList.size(); ++i){
            View view = new View(MainActivity.this); // Create new view
            if (i == 0) // Highlight the 1st view
                view.setBackground(getDrawable(R.drawable.rectangle_selected));
            else // Don't highlight the other views
                view.setBackground(getDrawable(R.drawable.rectangle_unselected));
            // Add view to the tab row layout
            view.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            mSelectedItineraryTabRowLayout.addView(view, i);
        }

        // Show highlighted tab row on sliding panel head
        mSlidingPanelHead.addView(mSelectedItineraryTabRowLayout, 0);

        // Display origin and destination markers on the map
        removeMarker(mPlaceSelectedMarker);
        mDestinationMarker = mMap.addMarker(new MarkerOptions()
                .title(mDestination.getName())
                .position(mDestination.getLocation()));
//                mOriginMarker = mMap.addMarker(new MarkerOptions()
//                        .title(mOrigin.getName())
//                        .position(mOrigin.getLocation()));

        // Get the first itinerary in the results & display it
        displayItinerary(0, android.R.anim.slide_in_left);
    }

    /**
     * Callback invoked upon failure of a trip plan request
     */
    public void updateUIonTripPlanFailure() {
        // Display "Request failed" on the sliding panel head
        showOnSlidingPanelHead(TRIP_PLAN_FAILURE_MESSAGE);

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
     * Displays an itinerary both in polyline on the map and in icon depiction on the sliding
     * panel head and tail.
     *
     * Clears the previously displayed itinerary if it exists, sets mCurItineraryIndex, generates &
     * draws the polylines representing the itinerary on the map, generates the itinerary leg icons
     * layout and expanded itinerary view to be displayed in the sliding panel head and tail,
     * animates the entrance of the contents of the sliding panel head & tail, and repositions the
     * map camera to fit & be centered on the itinerary polylines on the map.
     *
     * @param itineraryIndex the index of the itinerary in mItineraryList to display
     * @param animationId the resource id of the entrance animation for the contents of the sliding
     *                    panel head and tail
     * @pre: Activity is in the TRIP_PLAN state, mItineraryList, mOrigin, and mDestination are
     *      non-null and valid
     */
    public void displayItinerary(int itineraryIndex, int animationId) {

        Log.d(TAG, "Displaying itinerary");
        Log.d(TAG, "Sliding panel state: " + mSlidingPanelLayout.getPanelState());

        // Set mCurItineraryIndex
        mCurItineraryIndex = itineraryIndex;

        // Remove previous itinerary summary layout
        mSlidingPanelHead.removeView(mSlidingPanelHead.getChildAt(1)); // child at 0 is the tab row

        // Clear sliding panel tail
        mSlidingPanelTail.removeAllViews();

        // Remove polyline of previous itinerary if it exists
        if (mPolylineList != null) {
            for (Polyline polyline : mPolylineList)
                polyline.remove();
            mPolylineList = null;
        }

        // Sanity check
        if (itineraryIndex >= mItineraryList.size()) {
            Log.d(TAG, "Itinerary index is invalid; failed to display");
            return;
        }

        // Get the selected itinerary
        Itinerary itinerary = mItineraryList.get(itineraryIndex);

        // Sanity check
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

        // Create & initialize layout to hold the itinerary summary icons representing the legs
        // of the itinerary, to be inserted into another layout along with a TextView displaying
        // the duration of the itinerary, to be inserted into the sliding panel head
        LinearLayout itinerarySummaryLegIconsLayout = new LinearLayout(this);
        itinerarySummaryLegIconsLayout.setPadding(30,0,30,0);
        itinerarySummaryLegIconsLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

        // Keep track of the index of the next view we add to itinerarySummaryLegIconsLayout
        int index = 0;

        // Represent each leg in the itinerary with an icon in the itinerary summary and with a
        // polyline on the map
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
                    (leg.getMode().equals(TraverseMode.BICYCLE.toString())) ? 13 : 0; // padding
            ItineraryLegIconView legIconView = new ItineraryLegIconView(this,
                    paddingBetweenModeIconAndDurationText);

            // Get the drawable associated with the traverse mode of the leg and set it as the
            // drawable of the custom leg icon view
            Drawable d = ModeUtil.getDrawableFromString(leg.getMode());
            if (d != null) {
                d.setAlpha(DARK_OPACITY);
                legIconView.setIcon(d);
            }

            // Configure the contents of the custom view and appearance of the polyline
            // based on the mode of the leg
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

            // Insert a chevron icon into the itinerary summary leg icons layout (do this in front
            // of each itinerary leg icon except the first)
            if (index != 0) {
                ImageView arrow = new ImageView(this);
                arrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
                arrow.setScaleType(ImageView.ScaleType.FIT_CENTER);
                arrow.setAlpha(DARK_OPACITY_PERCENTAGE);
                itinerarySummaryLegIconsLayout.addView(arrow, index,
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
                ++index;
            }

            // Insert the leg icon into the itinerary summary leg icons layout
            itinerarySummaryLegIconsLayout.addView(legIconView, index,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            ++index;

        }

        // Crete the layout for itinerary summary (includes both the leg icons layout and the
        // duration TextView)
        LinearLayout itinerarySummaryLayout = new LinearLayout(this);

        // Add the itinerary summary leg icons layout to the itinerary summary layout
        itinerarySummaryLayout.addView(itinerarySummaryLegIconsLayout, new LinearLayout
                .LayoutParams(mSlidingPanelHead.getWidth() - 230,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Create the view for the itinerary duration & add it to the itinerary summary layout
        TextView duration = new TextView(this);
        duration.setGravity(Gravity.CENTER);
        duration.setTextColor(Color.BLACK);
        duration.setHorizontallyScrolling(false);
        duration.setAlpha(DARK_OPACITY_PERCENTAGE);
        duration.setPadding(0,0,PixelUtil.pxFromDp(this, 15),0);
        duration.setTextSize(13);
        duration.setText(getDurationString(itinerary.getDuration()));
        itinerarySummaryLayout.addView(duration, new LinearLayout
                .LayoutParams(230, ViewGroup.LayoutParams.MATCH_PARENT));

        // Add the itinerary summary layout to sliding panel head
        itinerarySummaryLayout.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (.9 * mSlidingPanelHead.getHeight())
        ));
        mSlidingPanelHead.addView(itinerarySummaryLayout, 1);

        // Create the expanded itinerary view
        ExpandedItineraryView expandedItineraryView = new ExpandedItineraryView(this);
        expandedItineraryView.setPadding(0,50,0,150);

        // Assign the location and name of the 1st and last points in the itinerary
        // so that the expanded itinerary view display the correct info
        if (legList.size() != 0) {
            legList.get(0).setFrom(new edu.vanderbilt.isis.trip_planner_android_client.model
                    .TripPlanner.TPPlanModel.Place(mOrigin.getLatitude(), mOrigin.getLongitude(),
                    mOrigin.getName()));
            legList.get(legList.size() - 1).setTo(new edu.vanderbilt.isis
                    .trip_planner_android_client.model.TripPlanner.TPPlanModel
                    .Place(mDestination.getLatitude(), mDestination.getLongitude(),
                    mDestination.getName()));
        }
        // Set the itinerary of the expanded itinerary view (will invalidate the view & display
        // the details of the itinerary)
        expandedItineraryView.setItinerary(itinerary);

        // Insert the expanded itinerary view into the sliding panel tail
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
        zoomMapToFitPolylines(); // Move the map camera to fit & be centered on the itinerary
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            /**
             * Move the map camera to fit & be centered on the itinerary when the My Location
             * button on the GoogleMap is clicked
             * @return true if the click has been processed
             */
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

        // Set up the on-swipe listeners for the sliding panel
        mSlidingPanelHead.setOnTouchListener(new SlidingPanelHeadOnSwipeTouchListener(this, this));
        mSlidingPanelTail.setOnTouchListener(new SlidingPanelTailOnSwipeTouchListener(this, this));

        Log.d(TAG, "Sliding panel state: " + mSlidingPanelLayout.getPanelState());

    }

    /**
     * Callback to invoke from the controller layer when the first location update of the activity
     * is received. Enables the "My Location" button on the google map, centers the map camera
     * on the current location, and zooms to the defined default zoom level.
     *
     * @pre the google map is ready, and location services & the google api client have been set up
     */
    public void initializeUIOnFirstLocationUpdate() {

        // Enable the My Location button
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException ignored) {}

        // Move map camera to current location if available
        LatLng currentLocation = Controller.getCurrentLocation(this);
        if (currentLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(currentLocation)
                            .zoom(DEFAULT_ZOOM_LEVEL)
                            .build()
                    )
            );
        }

    }

    /**
     * Callback to invoke from the controller layer when a location update is received
     * @pre location services & the google api client have been set up
     */
    public void updateUIOnLocationChanged() {

        if (getState() == ActivityState.NAVIGATION)
            updateNavigationModeCamera();
    }


    /**
     * Remove the transit stop info window from the HOME_STOP_SELECTED screen
     */
    private void removeTransitStopInfoWindow() {

        // Load the exit animation from resources
        Animation exit = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);

        // Set the animation listener
        exit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}

            /**
             * When the animation finishes, remove the transit stop info window
             * @param animation the animation
             */
            @Override
            public void onAnimationEnd(Animation animation) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .remove(mTransitStopInfoWindowFragment)
                        .commit();
                mTransitStopInfoWindowFragment = null;
            }
        });

        // Start the animation
        if (mTransitStopInfoWindowFragment.getView() != null)
            mTransitStopInfoWindowFragment.getView().startAnimation(exit);
    }

    /**
     * Remove the simple search bar
     */
    private void removeSimpleSearchBar() {
        // If the simple search bar is not already gone
        if (mSimpleSearchBar.getVisibility() != View.GONE) {

            // Load the exit animation from resources
            Animation hide = AnimationUtils.loadAnimation(this, R.anim.slide_out_up);

            // Set the animation listener
            hide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}

                /**
                 * When the animation finishes, set the simple search bar's visibility to gone
                 */
                @Override
                public void onAnimationEnd(Animation animation) {
                    mSimpleSearchBar.setVisibility(View.GONE);
                }
            });

            // Start the animation
            mSimpleSearchBar.startAnimation(hide);
        }
    }


    /**
     * Launch the google places autocomplete search widget
     * Will invoke onActivityResult when the user selects a place
     */
    public void launchSearchViewFragment(SearchFieldId id) {

        // Record which search bar was clicked
        setLastEditedSearchField(id);

        // Launch the fragment
        mSearchViewFragment = new SearchViewFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(mSearchViewFragment) // TODO

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

                    // If in state HOME_STOP_SELECTED
                    if (getState() == ActivityState.HOME_STOP_SELECTED) {
                        // Add the selected place as an intermediate stop
                        ArrayList<TripPlanPlace> intermediateStops = new ArrayList<>();
                        intermediateStops.add(0, new TripPlanPlace(mPlaceSelectedMarker.getTitle(),
                                mPlaceSelectedMarker.getPosition()));
                        planTrip(new TripPlanPlace(), tripPlanPlace, intermediateStops);
                    } else {
                        planTrip(new TripPlanPlace(), tripPlanPlace);
                    }

                } else if (lastEditedSearchField == SearchFieldId.DETAILED_FROM) {

                    // Set the text in the detailed from search bar
                    setmOrigin(tripPlanPlace);
                    // Plan the trip
                    planTrip(mOrigin, mDestination);

                } else if (lastEditedSearchField == SearchFieldId.DETAILED_TO) {

                    // Set the text in the detailed to search bar
                    setmDestination(tripPlanPlace);
                    // Plan the trip
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
     * @param place the place to select
     * @pre the activity is in HOME_PLACE_SELECTED state
     */
    private void selectPlaceOnMap(final TripPlanPlace place) {

        // Show place selected marker
        removeMarker(mPlaceSelectedMarker);
        mPlaceSelectedMarker = mMap.addMarker(new MarkerOptions()
                .position(place.getLocation())
                .title(place.getName())
        );

        // Set up "directions" button (fab)
        mFab.setImageDrawable(getDrawable(R.drawable.ic_directions_white_24dp)); // change image
        mFab.setOnClickListener(new View.OnClickListener() { // configure new functionality
            /**
             * Invoked when the associated view is clicked
             * @param v the view that was clicked
             */
            @Override
            public void onClick(View v) {

                // Make the button unclickable
                mFab.setClickable(false);

                // FAB becomes clickable again in the form of the "start navigation" button
                // after the itineraries of the trip plan are acquired

                // Launch a trip plan using the current location as the origin and the selected
                // place as the destination
                planTrip(new TripPlanPlace(), place);
            }
        });
        // Keep the arrow buttons hidden
        hideArrowButtons();
        // Show the directions button
        mFab.setVisibility(View.VISIBLE);

        // Initialize the name text view
        TextView placeNameText = new TextView(this);
        placeNameText.setId(R.id.place_name_text_view);
        placeNameText.setHorizontallyScrolling(true);
        placeNameText.setEllipsize(TextUtils.TruncateAt.END);
        placeNameText.setGravity(Gravity.BOTTOM);
        placeNameText.setTextSize(18);
        placeNameText.setPadding(40,0,40,0);
        placeNameText.setTextColor(Color.BLACK);
        placeNameText.setAlpha(DARK_OPACITY_PERCENTAGE);
        placeNameText.setText(place.getName());

        // Initialize the address text view
        TextView placeAddressText = new TextView(this);
        placeAddressText.setHorizontallyScrolling(true);
        placeAddressText.setEllipsize(TextUtils.TruncateAt.END);
        placeAddressText.setGravity(Gravity.TOP);
        placeAddressText.setPadding(40,0,40,0);
        placeAddressText.setTextSize(12);
        placeAddressText.setMaxLines(1);
        placeAddressText.setText(place.getAddress());

        // Add both text views to the sliding panel head
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        mSlidingPanelHead.addView(placeNameText, layoutParams);
        mSlidingPanelHead.addView(placeAddressText, layoutParams);

        // Show the sliding panel head
        mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

    }

    /**
     * @return the activity's current state
     */
    public ActivityState getState() { return mStack.peek();}

    /**
     * Get and pop the activity's current state off the back stack
     * @return the activity's current state
     */
    public ActivityState removeState() {
        return mStack.pop();
    }

    /**
     * Set the current state of the activity by pushing it on the back stack
     * @param state the new state for the activity
     */
    public void setState(ActivityState state) {
        mStack.push(state);
    }

    /**
     * Helper method to convert a drawable to a bitmap
     * @param drawable the drawable to be  converted
     * @return the bitmap converted from the drawable
     */
    private Bitmap drawableToBitmap (Drawable drawable) {

        // If the drawable is a bitmap drawable, return its bitmap
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable)drawable).getBitmap();

        // Create a new bitmap with the same dimensions as the drawable
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // Draw the drawable onto a canvas associated with the bitmap
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        // Return the bitmap
        return bitmap;
    }

    /**
     * Helper method to generate a speech balloon bitmap for a navigation instruction
     * @param instruction the instruction to show as the text in the balloon
     * @param left true if the speech bubble should lean left, false if right
     * @return the speech balloon bitmap
     */
    private Bitmap createWalkStepBitmap(String instruction, boolean left) {

        // Initialize constants
        final int TEXT_PADDING = PixelUtil.pxFromDp(this, 5);
        final int BALLOON_TAIL_SIZE = PixelUtil.pxFromDp(this, 10);
        final float ROUNDED_RECT_CORNER_RADIUS = PixelUtil.pxFromDp(this, 2);

        // Initialize Rect objects to hold the dimensions of the text and rounded rectangle
        Rect textDimensions = new Rect();
        Rect roundedRectDimensions = new Rect();

        // Initialize & configure the paint object for the rounded rectangle
        Paint paint = new Paint();
        paint.setColor(getColor(R.color.colorPrimary));

        // Initialize & configure the paint object for the text
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(PixelUtil.pxFromDp(this, 12));
        textPaint.setTextAlign(Paint.Align.LEFT);

        // Get the dimensions of the text
        textPaint.getTextBounds(instruction,0,instruction.length(),textDimensions);

        // Calculate the dimensions of the rounded rectangle
        roundedRectDimensions.set(0,0,textDimensions.width() + 2 * TEXT_PADDING,
                textDimensions.height() + 2 * TEXT_PADDING);

        // Calculate the dimensions of the entire speech balloon bitmap
        int w = roundedRectDimensions.width() + BALLOON_TAIL_SIZE;
        int h = roundedRectDimensions.height() + BALLOON_TAIL_SIZE;

        // Create a new bitmap and a new canvas to draw to the bitmap
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        if (left) { // Bubble on left, tail on right

            // Configure the path to draw the speech bubble tail
            Path tail = new Path();
            tail.setFillType(Path.FillType.EVEN_ODD);
            tail.moveTo(w, h);
            tail.lineTo(roundedRectDimensions.width(),
                    roundedRectDimensions.height() - ROUNDED_RECT_CORNER_RADIUS);
            tail.lineTo(roundedRectDimensions.width() - ROUNDED_RECT_CORNER_RADIUS,
                    roundedRectDimensions.height());
            tail.close();

            // Draw the rounded rectangle, the tail, and the text to the canvas
            canvas.drawRoundRect(0f, 0f, (float)roundedRectDimensions.width(),
                    (float)(roundedRectDimensions.height()),
                    ROUNDED_RECT_CORNER_RADIUS, ROUNDED_RECT_CORNER_RADIUS, paint);
            canvas.drawPath(tail, paint);
            canvas.drawText(instruction, TEXT_PADDING,
                    TEXT_PADDING + textDimensions.height(), textPaint);

        } else { // Bubble on right, tail on left

            // Configure the path to draw the speech bubble tail
            Path tail = new Path();
            tail.setFillType(Path.FillType.EVEN_ODD);
            tail.moveTo(0, h);
            tail.lineTo(BALLOON_TAIL_SIZE,
                    roundedRectDimensions.height() - ROUNDED_RECT_CORNER_RADIUS);
            tail.lineTo(BALLOON_TAIL_SIZE + ROUNDED_RECT_CORNER_RADIUS,
                    roundedRectDimensions.height());
            tail.close();

            // Draw the rounded rectangle, the tail, and the text to the canvas
            canvas.drawRoundRect(BALLOON_TAIL_SIZE, 0f,
                    (float)(BALLOON_TAIL_SIZE + roundedRectDimensions.width()),
                    (float)(roundedRectDimensions.height()),
                    ROUNDED_RECT_CORNER_RADIUS, ROUNDED_RECT_CORNER_RADIUS, paint);
            canvas.drawPath(tail, paint);
            canvas.drawText(instruction, BALLOON_TAIL_SIZE + TEXT_PADDING,
                    TEXT_PADDING + textDimensions.height(), textPaint);
        }

        // Return the finished bitmap
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

            // Initialize each button as selected or deselected based on their corresponding modes
            if (selectedModes.contains(traverseMode))
                selectModeButton(button);
            else
                deselectModeButton(button);

            // Set the on click listener for each button
            if (entry.getKey() == TraverseMode.BICYCLE) // If the bike button
                button.setOnClickListener( // TODO un-omit bike mode when available
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { // Show toast saying unavailable
                                Toast.makeText(MainActivity.this,
                                        "Bike mode not yet available", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            else { // If not the bike button
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Interrupt any ongoing trip plan requests, since we are about to
                        // request a new trip plan
                        Controller.interruptOngoingTripPlanRequests();

                        // Cast the view to an image button
                        ImageButton button = (ImageButton) v;

                        // Select or deselect the button based on its previous state
                        if (button.isSelected())
                            deselectModeButton(button);
                        else
                            selectModeButton(button);

                        // Refresh the trip plan
                        planTrip(mOrigin, mDestination);
                    }
                });

                // Set the on long click listener for each button
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        // Interrupt any ongoing trip plan requests, since we are about to
                        // request a new trip plan
                        Controller.interruptOngoingTripPlanRequests();

                        // Vibrate the device
                        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vib.vibrate(30);

                        // Get the traverse mode associated with the button
                        TraverseMode buttonMode = modeToImageButtonBiMap.inverse()
                                .get((ImageButton) v);

                        // Remove the first mode and select the mode normally if it was already the
                        // first mode, else select it as the first mode
                        if (buttonMode == Controller.getFirstMode()) {
                            Controller.removeFirstMode();
                            selectModeButton((ImageButton) v);
                        } else {
                            selectModeButtonAsFirstMode((ImageButton) v);
                        }

                        // Refresh the trip plan
                        planTrip(mOrigin, mDestination);

                        // Indicate that the click has been successfully processed
                        return true;
                    }
                });
            }
        }
    }


    /**
     * Helper function for selecting a mode button
     * @param button the button to select
     */
    private void selectModeButton(ImageButton button) {
        Log.d(TAG, "Mode button was selected");

        // Get the TraverseMode corresponding to the button
        TraverseMode mode = modeToImageButtonBiMap.inverse().get(button);

        // Select the button and select the mode
        button.setSelected(true);
        Controller.selectMode(mode);

        // Set the new colors for the button
        button.setBackgroundResource(R.drawable.rounded_rectangle_accent);
        button.setColorFilter(getResources().getColor(R.color.white, null));
    }

    /**
     * Helper function for deselecting a mode button
     * @param button the button to select
     */
    private void deselectModeButton(ImageButton button) {
        Log.d(TAG, "Mode button was deselected");

        // Get the TraverseMode corresponding to the button
        TraverseMode buttonMode = modeToImageButtonBiMap.inverse().get(button);

        // Deselect the button and deselect the mode
        button.setSelected(false);
        Controller.deselectMode(buttonMode);

        // Set the new colors for the button
        button.setBackgroundResource(R.drawable.rounded_rectangle_primary);
        button.setColorFilter(Color.WHITE);
    }

    /**
     * Helper function for selecting a mode button as the first mode in the trip plan
     * @param button the button to select
     */
    private void selectModeButtonAsFirstMode(ImageButton button) {

        // Get the old first traverse mode
        TraverseMode oldFirstMode = Controller.getFirstMode();
        // Get the new first traverse mode
        TraverseMode newFirstMode = modeToImageButtonBiMap.inverse().get(button);

        // If old first mode exists, select its corresponding button the regular way
        if (oldFirstMode != null) {
            selectModeButton(modeToImageButtonBiMap.get(oldFirstMode));
            Controller.removeFirstMode();
        }

        // Select the given button as the first mode button
        button.setSelected(true);
        Controller.setFirstMode(newFirstMode);

        // Set the new colors for the button
        button.setBackgroundResource(R.drawable.rounded_rectangle_white);
        button.setColorFilter(getResources().getColor(R.color.colorPrimary, null));
    }

    /**
     * Move/zoom the GoogleMap so that mOrigin, mDestination, and all the polylines in
     * mPolylineList are visible and centered on the map. No-op if mPolylineList is null or empty.
     *
     * @pre activity is in TRIP_PLAN mode and the currently selected trip plan is valid
     */
    private void zoomMapToFitPolylines() {

        if (mPolylineList != null && !mPolylineList.isEmpty()) {
            // Get the LatLngBounds
            LatLngBounds bounds = calculateLatLngPolylineBounds();
            // Move the camera accordingly
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    bounds, PixelUtil.pxFromDp(this, 30)));
        }
    }

    /**
     * Calculates the bounds for the GoogleMap in which mOrigin, mDestination, and all the
     * polylines in mPolylineList are visible and centered on the map.
     *
     * @pre activity is in TRIP_PLAN mode and the currently selected trip plan is valid
     * @return the LatLngBounds that show the polylines on the map
     */
    private LatLngBounds calculateLatLngPolylineBounds() {

        if (mPolylineList == null || mPolylineList.isEmpty()
                || mOrigin == null || mDestination == null)
            throw new RuntimeException("Cannot calculate polyline bounds");

        // Get the latlngs of the trip origin and destination
        LatLng origin = mOrigin.getLocation();
        LatLng destination = mDestination.getLocation();

        // Find the topmost, bottommost, leftmost, and rightmost of the origin and destination
        LatLng top = (origin.latitude >= destination.latitude) ? origin : destination;
        LatLng bottom = (origin.latitude <= destination.latitude) ? origin : destination;
        LatLng right = (origin.longitude >= destination.longitude) ? origin : destination;
        LatLng left = (origin.longitude <= destination.longitude) ? origin : destination;

        // Find the topmost, bottommost, leftmost, and rightmost of all the PolyLine points
        // and the origin and destination
        for (Polyline polyline : mPolylineList) {
            for (LatLng point : polyline.getPoints()) {
                top = (top.latitude >= point.latitude) ? top : point;
                bottom = (bottom.latitude <= point.latitude) ? bottom : point;
                right = (right.longitude >= point.longitude) ? right : point;
                left = (left.longitude <= point.longitude) ? left : point;
            }
        }

        // Move the top bound up by 1/10th of the latitude range to make room for any markers
        // (representing the trip's origin or destination) that might be added near the top bound
        double dy = top.latitude - bottom.latitude;
        top = new LatLng(top.latitude + .1 * dy, top.longitude);

        // Build & return the new latlngbounds to include the the topmost, bottommost, leftmost,
        // and rightmost points
        return new LatLngBounds.Builder()
                .include(top)
                .include(bottom)
                .include(left)
                .include(right)
                .build();
    }

    /**
     * Helper method that resets the functionality of the map's My Location button
     * to centering the camera on the current location
     *
     * @pre mMap, the Google API client, and location services are set up and working
     */
    private void resetMyLocationButton() {
        // Set the GoogleMap's OnMyLocationButtonClickListener
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                // Move the camera to the device's current location
                LatLng currentLocation = Controller.getCurrentLocation(MainActivity.this);
                if (currentLocation != null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                return false;
            }
        });
    }

    /**
     * Helper method that clears the sliding panel head and displays a simple message on it
     *
     * @pre mSlidingPanelHead has been initialized
     */
    public void showOnSlidingPanelHead(String message) {

        // Create and initialize TextView to show the message in
        TextView textView = new TextView(MainActivity.this);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(15);
        textView.setText(message);

        // Clear the sliding panel head
        mSlidingPanelHead.removeAllViews();

        // Add the textview to the sliding panel head
        mSlidingPanelHead.addView(textView,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
    }

    /**
     * Helper method that returns a string representation of a given span of time in terms of
     * days, hours, and minutes. Shows the time in just seconds if the span of time is less
     * than a minute total.
     * @param seconds the time to be represented, given in seconds
     * @return the string representation of the time
     */
    public static String getDurationString(double seconds) {

        // Get the total number of minutes spanned
        long totalMins = (long) Math.ceil(seconds/60.0);
        // Get the remainder number of minutes spanned when the total hours are accounted for
        long remainderMins = totalMins%60;

        // Get the total number of hours spanned
        long totalHours = totalMins/60;
        // Get the remainder number of hours spanned when the total days are accounted for
        long remainderHours = totalHours%24;

        // Get the total number of days spanned
        long days = totalHours/24;

        // Construct the string to reflect the following format:
        // "<x> days <y> hr <z> min"   -- time spans more than 2 days
        // "1 day <x> hr <y> min"      -- time spans more than a day but less than 2 days
        // "<x> hr <y> min"            -- time spans more than an hour but less than a day
        // "<x> min"                  -- time spans more than a minute but less than an hour
        // "<x> sec"                -- time spans less than an hour

        String duration = "";
        if (days == 1)
            duration += (days + " day ");
        else if (days != 0)
            duration += (days + " days ");

        if (remainderHours != 0)
            duration += (remainderHours + " hr ");
        if (remainderMins != 0)
            duration += (remainderMins + " min ");

        // Only display seconds if total time is less than 1 minute
        if (duration.equals(""))
            duration = seconds + " sec ";

        // Slice off the extra space character at the end
        duration = duration.substring(0, duration.length() - 1);

        // Return the string
        return duration;
    }



    /**
     * Method to be called to process when the user swipes the sliding panel left.
     *
     * @pre the activity is in TripPlanMode, one of the itineraries in the trip plan is already
     *      displayed, and mItineraryList & mCurItineraryIndex are both valid.
     */
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
            displayItinerary(mCurItineraryIndex, R.anim.slide_in_right);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }

    /**
     * Method to be called to process when the user swipes the sliding panel right.
     *
     * @pre the activity is in TripPlanMode, one of the itineraries in the trip plan is already
     *      displayed, and mItineraryList & mCurItineraryIndex are both valid.
     */
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
            displayItinerary(mCurItineraryIndex, R.anim.slide_in_left);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }

    /**
     * Get the TripPlanPlace currently selected as the origin of the trip plan
     * @return mOrigin
     */
    public TripPlanPlace getmOrigin() {
        return mOrigin;
    }

    /**
     * Get the TripPlanPlace currently selected as the destination of the trip plan
     * @return mDestination
     */
    public TripPlanPlace getmDestination() {
        return mDestination;
    }

    /**
     * Set the TripPlanPlace currently selected as the origin of the trip plan
     * Display the name of the place in the "from" search field in the detailed
     * search bar fragment if it is available
     * @param mOrigin new origin
     */
    public void setmOrigin(TripPlanPlace mOrigin) {
        mOrigin = mOrigin;
        if (getState() == ActivityState.HOME || getState() == ActivityState.HOME_STOP_SELECTED
                && mSimpleSearchBar != null && mSimpleSearchBar.getVisibility() == View.VISIBLE
                && mSimpleSearchBarText != null)
            mSimpleSearchBarText.setText(mDestination.getName());

        else if (getState() == ActivityState.TRIP_PLAN && mDetailedSearchBarFragment != null)
            mDetailedSearchBarFragment.setOriginText(mOrigin.getName());
    }

    /**
     * Set the TripPlanPlace currently selected as the destination of the trip plan
     * Display the name of the place in the "to" search field in the detailed
     * search bar fragment if it is available
     * @param mDestination new destination
     */
    public void setmDestination(TripPlanPlace mDestination) {
        mDestination = mDestination;

        if (getState() == ActivityState.TRIP_PLAN && mDetailedSearchBarFragment != null)
            mDetailedSearchBarFragment.setDestinationText(mDestination.getName());
    }

    /**
     * Getter for the marker representing the currently selected place
     * Valid only in HOME_PLACE_SELECTED and HOME_STOP_SELECTED states
     * @pre activity is in HOME_PLACE_SELECTED or HOME_STOP_SELECTED states
     * @return the marker representing the currently selected place
     */
    public Marker getmPlaceSelectedMarker() {
        return mPlaceSelectedMarker;
    }

    /**
     * Add a TraverseMode-ImageButton pair to the bi-map
     * @param mode the TraverseMode
     * @param button the ImageButton
     */
    public void addToModeButtonBiMap(TraverseMode mode, ImageButton button) {
        modeToImageButtonBiMap.forcePut(mode, button);
    }

    /**
     * Update the activity's record of the last edited search field (should be called every time a
     * search field is clicked). Will be used to determine which search field to update upon the
     * selection of a suggestion item in the search view.
     *
     * @param id the id of the last edited search field
     */
    public void setLastEditedSearchField(SearchFieldId id) {
        lastEditedSearchField = id;
    }

    /**
     * Gets the activity's record of the last edited search field
     * @return the id of the last edited search field
     */
    public SearchFieldId getLastEditedSearchField() {
        return lastEditedSearchField;
    }

    /**
     * Toggle the sliding panel between expanded and collapsed
     */
    public void toggleSlidingPanel() {

        if (mSlidingPanelLayout == null)
            throw new RuntimeException("Sliding panel layout reference is null");

        // Get the panel state
        SlidingUpPanelLayout.PanelState panelState = mSlidingPanelLayout.getPanelState();

        // Do nothing if the sliding panel is hidden or anchored
        if (panelState == SlidingUpPanelLayout.PanelState.HIDDEN
                || panelState == SlidingUpPanelLayout.PanelState.ANCHORED)
            return;

        if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED) // collapse if expanded
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) // expand if collapsed
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    /**
     * Helper method to set the padding for the Google Map for a given ActivityState
     * @param state the ActivityState for which the map padding will be set to accommodate
     */
    public void setMapPadding(ActivityState state) {

        // Convert amounts from dp to pixels
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
     * Register sensor listeners to detect device rotation
     * @pre mSensorManager has been initialized
     */
    private void registerSensorListeners() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

    }

    /**
     * Invoked automatically when the sensor's accuracy changes
     * Overrides method from the SensorEventListener class
     *
     * @param sensor the sensor whose accuracy changed
     * @param accuracy the new accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Invoked automatically when the sensor senses a change
     * Overrides method from the SensorEventListener class
     *
     * Updates local records of the last sensor reading
     * @param event the sensor event encapsulating the values of the new sensor reading
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        // Update mAccelerometerReading
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        // Update mMagnetometerReading
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }

    }

    /**
     * Hide and disable the left arrow button
     */
    private void hideLeftArrowButton() {
        if (mLeftArrowButton != null) {
            mLeftArrowButton.setClickable(false);
            mLeftArrowButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Show and enable the left arrow button
     */
    private void showLeftArrowButton() {
        if (mLeftArrowButton != null) {
            mLeftArrowButton.setVisibility(View.VISIBLE);
            mLeftArrowButton.setClickable(true);
        }
    }

    /**
     * Hide and disable the right arrow button
     */
    private void hideRightArrowButton() {
        if (mRightArrowButton != null) {
            mRightArrowButton.setClickable(false);
            mRightArrowButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Show and enable the right arrow button
     */
    private void showRightArrowButton() {
        if (mRightArrowButton != null) {
            mRightArrowButton.setVisibility(View.VISIBLE);
            mRightArrowButton.setClickable(true);
        }
    }

    /**
     * Hide both arrow buttons
     */
    private void hideArrowButtons() {
        hideLeftArrowButton();
        hideRightArrowButton();
    }

    /**
     * Show the arrow buttons according to the current itinerary on screen
     * @pre activity is in TRIP_PLAN state, mItineraryList and mCurItineraryIndex are valid
     */
    private void showArrowButtons() {

        // Only show in trip plan mode
        if (getState() != ActivityState.TRIP_PLAN)
            return;

        // Show the left arrow button if this is not the first itinerary
        if (mCurItineraryIndex != 0) {
            showLeftArrowButton();
        } else {
            hideLeftArrowButton();
        }
        // Show the right arrow button if this is not the last itinerary
        if (mCurItineraryIndex != mItineraryList.size() - 1) {
            showRightArrowButton();
        } else {
            hideRightArrowButton();
        }
    }

    /**
     * Checks if the specified ActivityState is one of HOME, HOME_PLACE_SELECTED,
     * HOME_STOP_SELECTED, or HOME_BUS_SELECTED
     *
     * @param state the ActivityState to examine
     * @return true if the state is a home state, false otherwise
     */
    private boolean isAHomeState(ActivityState state) {
        return (state == ActivityState.HOME ||
                state == ActivityState.HOME_PLACE_SELECTED ||
                state == ActivityState.HOME_STOP_SELECTED ||
                state == ActivityState.HOME_BUS_SELECTED);
    }

     /**
     * Remove a marker from the map
     * @param marker the marker to remove
     */
    private void removeMarker(Marker marker) {
        if (marker != null) {
            marker.remove();
        }
    }

    /**
     * Update the camera in navigation mode; move the camera to match the device's current
     * location & cardinal orientation
     */
    private void updateNavigationModeCamera() {

        // Get rotation matrix based on current readings from accelerometer and magnetometer
        final float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrix(rotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // Express the updated rotation matrix as three orientation angles
        final float[] orientationAngles = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        // Move the camera to follow the current location and cardinal orientation
        LatLng currentLocation = Controller.getCurrentLocation(this);
        if (currentLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder())
                    .target(currentLocation)
                    .zoom(NAVIGATION_MODE_ZOOM_LEVEL)
                    .tilt(NAVIGATION_MODE_TILT_LEVEL)
                    .bearing((float) Math.toDegrees(orientationAngles[0]))
                    .build())
            );
        }
    }


    /**
     * Callback method invoked when user responds to a permissions request
     * made from the controller layer
     * Must be overridden & implemented in the main activity
     *
     * @param requestCode the code identifying the request
     * @param permissions permissions
     * @param grantResults results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_LOCATION:
                // This method MUST be called for Google Play Services to be properly set up
                LocationPermissionService
                        .handleLocationRequestPermissionsResult(this, grantResults);
                break;
        }
    }

}
