package com.example.anne.otp_android_client_v3;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anne.otp_android_client_v3.itinerary_display_custom_views.SummarizedItineraryIcon;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.common.collect.BiMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.HashBiMap;
import com.google.maps.android.PolyUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import vanderbilt.thub.otp.model.OTPPlanModel.GenericLocation;
import vanderbilt.thub.otp.model.OTPPlanModel.Itinerary;
import vanderbilt.thub.otp.model.OTPPlanModel.Leg;
import vanderbilt.thub.otp.model.OTPPlanModel.PlannerRequest;
import vanderbilt.thub.otp.model.OTPPlanModel.Response;
import vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode;
import vanderbilt.thub.otp.model.OTPStopsModel.Stop;
import vanderbilt.thub.otp.service.OTPPlanService;
import vanderbilt.thub.otp.service.OTPPlanSvcApi;


public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final String TAG = "MainActivity.java";

    private final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private final float OPACITY_PECENTAGE = .70f;

    private final int OPACITY = (int) (OPACITY_PECENTAGE * 255);


    private GoogleMap mMap;

    private GoogleApiClient mGoogleAPIClient;

    private SlidingUpPanelLayout mSlidingPanelLayout;


    public enum ActivityState {HOME, HOME_PLACE_SELECTED, HOME_STOP_SELECTED, HOME_BUS_SELECTED,
        TRIP_PLAN, NAVIGATION}

    private Stack<ActivityState> mStateStack;

    public enum SearchBarId {SIMPLE, DETAILED_FROM, DETAILED_TO}

    private SearchBarId lastEditedSearchBar;


    private Place mSource = null;

    private Place mDestination = null;

    private EditText mSourceBox;

    private EditText mDestinationBox;

    private volatile LatLngBounds mMapBounds;

    private BiMap<TraverseMode, ImageButton> modeToImageButtonBiMap;



    private List<Itinerary> mItineraryList;

    private List<Polyline> mPolylineList;

    private Marker mDestinationMarker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Activity created");

        // Setup
        setUpDrawer();
        setUpPlaceAutocompleteSearch();
        setUpMap();
        setUpModes();
        setUpSlidingPanel();

        // Initialize state
        mStateStack = new Stack<>();
        setState(ActivityState.HOME);
    }

    /**
     * Callback that handles back button press
     * Closes the navigation drawer if it is open
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else {
            super.onBackPressed();

            // Perform the corresponding necessary actions based on the current state
            switch (mStateStack.pop().toString()) {
                case("TWO"):
                    Log.d(TAG, "Transitioning back from state TWO to state ONE");

                    // Remove destination marker from the map if it exists
                    if (mDestinationMarker != null) {
                        mDestinationMarker.remove();
                        mDestinationMarker = null;
                    }

                    // Remove previous itinerary from the map if it exists
                    if (mPolylineList != null) {
                        for (Polyline polyline : mPolylineList)
                            polyline.remove();
                        mPolylineList = null;
                    }

                    // Revert map shape & center/zoom to current location
                    mMap.setPadding(12,175,12,0);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(getCoordinates(null)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                    // Hide navigation buttons
                    LinearLayout navButtons =
                            (LinearLayout) findViewById(R.id.navigation_buttons_layout);
                    navButtons.setVisibility(View.GONE);

                    // Hide sliding panel
                    mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                    // Show simple search bar
                    CardView cardView = (CardView) findViewById(R.id.simple_search_bar_card_view);
                    cardView.setVisibility(View.VISIBLE);

                    TextView textView = (TextView) findViewById(R.id.simple_search_bar_text_view);
                    textView.setVisibility(View.VISIBLE);

                    break;

            }

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

                        if (id == R.id.nav_planner) {

                        } else if (id == R.id.nav_settings) {

                        }

                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });
    }

    /**
     * Helper method for setting up the PlaceAutocompleteFragment
     */
    private void setUpPlaceAutocompleteSearch() {
        TextView searchBar = (TextView) findViewById(R.id.simple_search_bar_text_view);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGooglePlacesSearchWidget(SearchBarId.SIMPLE);
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
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, "Error launching PlaceAutocomplete intent");
        }
        catch (GooglePlayServicesNotAvailableException e) {
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

                if (lastEditedSearchBar == SearchBarId.SIMPLE) {

                    // Hide simple search bar
                    TextView textView = (TextView) findViewById(R.id.simple_search_bar_text_view);
                    CardView cardView = (CardView) findViewById(R.id.simple_search_bar_card_view);
                    textView.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);

                    mDestination = place;
                    transitionToStateTWO();

                } else if (lastEditedSearchBar == SearchBarId.DETAILED_FROM) {

                    // Set the text in the detailed from search bar
                    mSourceBox.setText(place.getName());
                    mSource = place;
                    planAndDisplayTrip(mSource, mDestination);

                } else if (lastEditedSearchBar == SearchBarId.DETAILED_TO) {

                    // Set the text in the detailed to search bar
                    mDestinationBox.setText(place.getName());
                    mDestination = place;
                    planAndDisplayTrip(mSource, mDestination);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
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
        ModeOptions.setDefaultModes(Arrays.asList(TraverseMode.WALK, TraverseMode.BUS));
        ModeOptions.selectDefaultModes();
    }

    /**
     * Helper method for setting up the sliding panel layout
     */
    private void setUpSlidingPanel() {
        mSlidingPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
    }

    /**
     * Callback triggered when the map is ready to be used
     * Sets up the Google API Client & enables the compass and location features for the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setPadding(12,175,12,0);
        mMap.getUiSettings().setCompassEnabled(true);

        // Build the GoogleApiClient, enable my location
        if (checkLocationPermission()) {
            // Permission was already granted
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // If permission was not already granted, checkLocationPermission() requests
        // permission and executes the above enclosed statements after permission is granted

        // Set the on camera idle listener
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Update current bounds
                mMapBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
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
            // Invokes OnRequestPermissionsResult on result
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
                        // Build the GoogleApiClient, enable my location
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    } else {
                        // Permission was denied
                        Toast.makeText(this, "Location access permission denied", Toast.LENGTH_LONG).show();
                        buildGoogleApiClientWithoutLocationServices();
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

        // Create location request
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(5000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // If permission if granted, request location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleAPIClient, locationRequest, this);
        }
        Log.d(TAG, "Location updates request made");
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

        Log.d(TAG, "Location update received");

        // Get current location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        // Move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // Stop location updates, we already got the user's initial location
        if (mGoogleAPIClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleAPIClient, this);

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

    public ActivityState getState() { return mStateStack.peek();}

    public void setState(ActivityState state) {
        mStateStack.push(state);
    }

    public boolean transitionToStateTWO() {

        if (getState() != ActivityState.HOME && getState() != ActivityState.HOME_PLACE_SELECTED
                && getState() != ActivityState.HOME_STOP_SELECTED
                && getState() != ActivityState.HOME_BUS_SELECTED)
            return false;

        Log.d(TAG, "Transitioning from state ONE to TWO");

        // Resize map & disable map center button
        mMap.setPadding(12,450,12,80);

        // Create a new detailed search bar
         DetailedSearchBarFragment detailedSearchBarFragment = new DetailedSearchBarFragment();

        // Initialize a fragment transaction to show the detailed search bar
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.detailed_search_bar_frame, detailedSearchBarFragment);

        // Add to stack and execute the fragment transaction
        fragmentTransaction.addToBackStack("Screen Two");
        fragmentTransaction.commit();

        // Plan the trip and display it on the map
        planAndDisplayTrip(null, mDestination);

        // Show sliding panel layout
        mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        // Show navigation buttons
        LinearLayout navButtons =
                (LinearLayout) findViewById(R.id.navigation_buttons_layout);
        navButtons.setVisibility(View.VISIBLE);

        setState(ActivityState.TRIP_PLAN);
        return true;
    }

    /**
     * Helper method to initialize the mode buttons in the detailed search bar
     */
    public void setUpModeButtons() {

        // Get the current selected modes
        Set<TraverseMode> selectedModes = ModeOptions.getSelectedModes();

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
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton button = (ImageButton) v;

                    if (button.isSelected())
                        deselectModeButton(button);
                    else
                        selectModeButton(button);

                    // Refresh the search
                    planAndDisplayTrip(mSource, mDestination);
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
        ModeOptions.addSelectedMode(modeToImageButtonBiMap.inverse().get(button));

        // Set white background, colored image
        button.setBackgroundResource(R.drawable.rounded_rectangle_white);
        button.setColorFilter(Color.parseColor("#4983f2"));
    }

    /**
     * Helper function for deselecting a mode button
     */
    private void deselectModeButton(ImageButton button) {
        Log.d(TAG, "Mode button was deselected");

        // Deselect button and remove corresponding mode from list of selected modes
        button.setSelected(false);
        ModeOptions.removeSelectedMode(modeToImageButtonBiMap.inverse().get(button));

        // Set colored background, white image
        button.setBackgroundResource(R.drawable.rounded_rectangle_primary);
        button.setColorFilter(Color.WHITE);
    }

    /**
     * Helper method that gets the current location, makes a GET request to the OTP
     * server for a list of itineraries from the current location to mDestination,
     * and displays the first one on the map
     * @param origin
     * @param destination
     * @return true if the request was successfully made, false otherwise
     */
    public boolean planAndDisplayTrip(Place origin, Place destination) {

        Log.d(TAG, "Planning trip");

        // Prompt user to choose a mode & exit if no modes are selected
        if (ModeOptions.getNumSelectedModes() == 0) {
            Toast.makeText(this, "Please select at least one mode of transportation",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        // If public transport is selected, ensure that walk or bicycle or car is also selected
        Set<TraverseMode> selectedModes = ModeOptions.getSelectedModes();
        if ((selectedModes.contains(TraverseMode.BUS)
                || selectedModes.contains(TraverseMode.SUBWAY))
                &&
                !(selectedModes.contains(TraverseMode.WALK)
                || selectedModes.contains(TraverseMode.BICYCLE)
                || selectedModes.contains(TraverseMode.CAR))) {
            Toast.makeText(this, "Please select at least one of walk, bike, or car modes",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        // Set origin latlng
        final LatLng originLatLng = getCoordinates(origin);

        // Set destination latlng
        final LatLng destinationLatLng = getCoordinates(destination);

        // Exit if origin or destination was null and location access was denied
        if (originLatLng == null || destinationLatLng == null)
            return false;

        // Remove destination marker from the map if it exists
        if (mDestinationMarker != null) {
            mDestinationMarker.remove();
            mDestinationMarker = null;
        }

        // Remove previous itinerary from the map if it exists
        if (mPolylineList != null) {
            for (Polyline polyline : mPolylineList)
                polyline.remove();
            mPolylineList = null;
        }

        // Draw and save a marker at the destination
        mDestinationMarker =  mMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .title("Destination"));

        // Create and set up a new trip planner request
        final PlannerRequest request = new PlannerRequest();

        request.setFrom(new GenericLocation(originLatLng.latitude,
                originLatLng.longitude));
        request.setTo(new GenericLocation(destinationLatLng.latitude,
                destinationLatLng.longitude));
        request.setModes(ModeOptions.getSelectedModesString());
        Log.d(TAG, "Selected modes: " + ModeOptions.getSelectedModesString());

        // Set up the OPTPlanService
        OTPPlanService.buildRetrofit(OTPPlanSvcApi.OTP_API_URL);
        String startLocation = Double.toString(request.getFrom().getLat()) +
                "," + Double.toString(request.getFrom().getLng());
        String endLocation = Double.toString(request.getTo().getLat()) +
                "," + Double.toString(request.getTo().getLng());

        // Make the request to OTP server
        Call<Response> response = OTPPlanService.getOtpService().geTripPlan(
                OTPPlanService.ROUTER_ID,
                startLocation,
                endLocation,
                request.getModes(),
                true
        );
        final long time = System.currentTimeMillis();
        response.enqueue(new Callback<Response>() {

            // Handle the request response
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                Log.d(TAG, "Received trip plan from server. Time: " +
                        (System.currentTimeMillis() - time));

                if (response.body().getPlan() == null
                        || response.body().getPlan().getItineraries() == null
                        || response.body().getPlan().getItineraries().isEmpty()) {
                    Log.d(TAG, "OTP request result was empty");
                    return;
                }

                List<Itinerary> itineraries = response.body().getPlan().getItineraries();

                // Get the first itinerary in the results & display it
                displayItinerary(itineraries.get(0), originLatLng, destinationLatLng);
                // Save the list of itinerary results
                mItineraryList = itineraries;

                // TODO: just test
                for (Leg leg : itineraries.get(0).getLegs())
                    for (vanderbilt.thub.otp.model.OTPPlanModel.Place stop : leg.getIntermediateStops())
                        Log.d(TAG, stop.toString());
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Log.d(TAG, "Request failed to get itineraries:\n" + throwable.toString());
                Toast.makeText(getApplicationContext(),
                        "Request to server failed", Toast.LENGTH_LONG).show();

                // Move the camera to include just the origin and destination
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder()
                        .include(originLatLng)
                        .include(destinationLatLng)
                        .build()
                        , 100)
                );
            }
        });

        Log.d(TAG, "Made request to OTP server");
        Log.d(TAG, "Starting point coordinates: " + originLatLng.toString());
        Log.d(TAG, "Destination coordinates: " + destinationLatLng.toString());
        return true;
    }

    /**
     * Helper method to display an itinerary in polyline on the map
     * and in summary on the sliding panel
     * This method does not reset the destination marker, which is done in planAndDisplayTrip()
     */
    public void displayItinerary(Itinerary itinerary, LatLng origin, LatLng destination) {

        Log.d(TAG, "Displaying itinerary");
        long time = System.currentTimeMillis();

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

        // Get the legs of the itinerary and create a list for the corresponding polylines
        List<Leg> legList= itinerary.getLegs();
        List<Polyline> polylineList = new LinkedList<>();

        // Get & clear the sliding panel head
        ViewGroup slidingPanelHead = (ViewGroup) findViewById(R.id.sliding_panel_head);
        slidingPanelHead.removeAllViews();

        // Display each leg as a custom view in the itinerary summary and as a polyline on the map
        LinearLayout itinerarySummaryLegsLayout = new LinearLayout(this);
        itinerarySummaryLegsLayout.setGravity(Gravity.CENTER_VERTICAL);
        int index = 0;
        for (Leg leg : legList) {

            // Get a list of the points that make up the leg
            List<LatLng> points = PolyUtil.decode(leg.getLegGeometry().getPoints());

            // Create a polyline options object for the leg
            PolylineOptions polylineOptions = new PolylineOptions().addAll(points).width(15);

            // Create a new custom view representing this leg of the itinerary
            SummarizedItineraryIcon view = new SummarizedItineraryIcon(this);

            // Configure the polyline and custom view based on the mode of the leg
            Drawable d;
            switch (leg.getMode()) {
                case ("WALK"):
                    polylineOptions
                            .color(ResourcesCompat.getColor(getResources(),
                                    R.color.colorPrimary, null))
                            .pattern(Arrays.<PatternItem>asList(new Dot(), new Gap(10)));
                    d = ContextCompat
                            .getDrawable(this, R.drawable.ic_directions_walk_black_24dp);
                    d.setAlpha(OPACITY);
                    view.setIcon(d);
                    view.setLegDuration((int) Math.round(leg.getDuration()/60));
                    break;
                case ("BICYCLE"):
                    polylineOptions
                            .color(ResourcesCompat.getColor(getResources(),
                                    R.color.colorPrimary, null))
                            .pattern(Arrays.<PatternItem>asList(new Dash(30), new Gap(10)));
                    d = ContextCompat
                            .getDrawable(this, R.drawable.ic_directions_bike_black_24dp);
                    d.setAlpha(OPACITY);
                    view.setIcon(d);
                    view.setLegDuration((int) Math.round(leg.getDuration()/60));
                    break;
                case ("CAR"):
                    polylineOptions.color(ResourcesCompat.getColor(getResources(),
                            R.color.colorPrimary, null));
                    d = ContextCompat
                            .getDrawable(this, R.drawable.ic_directions_car_black_24dp);
                    d.setAlpha(OPACITY);
                    view.setIcon(d);
                    break;
                case ("BUS"):
                    polylineOptions.color(Color.parseColor("#" + leg.getRouteColor()));
                    d = ContextCompat
                            .getDrawable(this, R.drawable.ic_directions_bus_black_24dp);
                    d.setAlpha(OPACITY);
                    view.setIcon(d);
                    view.setRouteName(leg.getRoute());
                    view.setRouteColor(Color.parseColor("#" + leg.getRouteColor()));
                    view.setRouteNameColor(Color.WHITE);
                    view.setShowRoute(true);
                    break;
                case ("SUBWAY"):
                    polylineOptions.color(Color.parseColor("#" + leg.getRouteColor()));
                    d = ContextCompat
                            .getDrawable(this, R.drawable.ic_directions_subway_black_24dp);
                    d.setAlpha(OPACITY);
                    view.setIcon(d);
                    view.setRouteName(leg.getRoute());
                    view.setRouteColor(Color.parseColor("#" + leg.getRouteColor()));
                    view.setRouteNameColor(Color.WHITE);
                    view.setShowRoute(true);
                    break;
                default: polylineOptions.color(Color.GRAY);
            }

            // Draw the polyline leg to the map and save it to the list
            polylineList.add(mMap.addPolyline(polylineOptions));

            // Add arrow icon to the sliding panel drawer handle
            // (except in front of the first mode icon)
            if (index!= 0) {
                ImageView arrow = new ImageView(this);
                arrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
                arrow.setScaleType(ImageView.ScaleType.FIT_CENTER);
                arrow.setAlpha(OPACITY_PECENTAGE);
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

            // TODO: Add the details of the leg to the sliding panel tail
        }

        // Add the created linear layout to the sliding panel drawer handle
        slidingPanelHead.addView(itinerarySummaryLegsLayout, new LinearLayout
                .LayoutParams(slidingPanelHead.getWidth() - 230,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Add the duration of the itinerary to the sliding panel head
        TextView duration = new TextView(this);
        duration.setGravity(Gravity.CENTER);
        duration.setTextColor(Color.BLACK);
        duration.setHorizontallyScrolling(false);
        duration.setAlpha(OPACITY_PECENTAGE);
        duration.setTextSize(13);
        duration.setText(getDurationString(itinerary.getDuration()));
        duration.setPadding(0,0,0,0);
//        duration.setBackgroundResource(R.drawable.rectangle_border);

        slidingPanelHead.addView(duration, new LinearLayout
                .LayoutParams(230, ViewGroup.LayoutParams.MATCH_PARENT));

        // Save the list of polylines drawn on the map
        mPolylineList =  polylineList;

        // Reconfigure map camera
        if (!mPolylineList.isEmpty()) {

            // Find the topmost, bottommost, leftmost, and rightmost points in all the PolyLines
            LatLng top = (origin.latitude >= destination.latitude) ? origin : destination;
            LatLng bottom = (origin.latitude <= destination.latitude) ? origin : destination;
            LatLng right = (origin.longitude >= destination.longitude) ? origin : destination;
            LatLng left = (origin.longitude <= destination.longitude) ? origin : destination;

            for (Polyline polyline : mPolylineList) {
                for (LatLng point : polyline.getPoints()){
                    top = (top.latitude >= point.latitude) ? top : point;
                    bottom = (bottom.latitude <= point.latitude) ? bottom : point;
                    right = (right.longitude >= point.longitude) ? right : point;
                    left = (left.longitude <= point.longitude) ? left : point;
                }
            }

            // Move the camera to include all four points
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder()
                    .include(top)
                    .include(bottom)
                    .include(right)
                    .include(left)
                    .build()
                    , 100)
            );

        }
        Log.d(TAG, "Done displaying itinerary. Time: " + (System.currentTimeMillis() - time));

    }

    /**
     * Helper function that returns a string representing time in terms of
     * days, hours, and minutes, or seconds if shorter than a single minute
     * @param seconds
     * @return
     */
    private String getDurationString(long seconds) {

        long totalMins = seconds/60;

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

        if (duration == "")
            duration = seconds + " sec ";

        // Slice off the extra space at the end
        duration = duration.substring(0, duration.length() - 1);

        return duration;
    }

    /**
     * Helper method that returns the coordinates of a Place, or, if the Place is null,
     * returns the current location
     * Returns null if a security exception was thrown
     */
    @Nullable
    private LatLng getCoordinates(@Nullable Place place) {
        if (place == null) {
            // If destination was not provided, set the destination to be the current location
            try {
                Location location = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleAPIClient);
                return new LatLng(location.getLatitude(), location.getLongitude());


            } catch (SecurityException se) {
                Toast.makeText(this, "Location access permission denied", Toast.LENGTH_LONG).show();
                return null;
            }
        } else {
            // Otherwise, get the provided location's coordinates
            return place.getLatLng();
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

    public Place getCurrentSelectedSourcePlace() {
        return mSource;
    }

    public Place getCurrentSelectedDestinationPlace() {
        return mDestination;
    }

    public void setCurrentSelectedSourcePlace(Place place) {
        mSource = place;
    }

    public void setCurrentSelectedDestinationPlace(Place place) {
        mDestination = place;
    }

    public void addToModeButtonBiMap(TraverseMode mode, ImageButton button) {
        modeToImageButtonBiMap.forcePut(mode, button);
    }

    private void setLastEditedSearchBar(SearchBarId id) {lastEditedSearchBar = id;}

    public void setSourceBox(EditText et) {
        mSourceBox = et;
    }

    public void setDestinationBox(EditText et) {
        mDestinationBox = et;
    }

}
