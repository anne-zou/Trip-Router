package com.example.anne.otp_android_client_v3;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.common.collect.BiMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import vanderbilt.thub.otp.model.GenericLocation;
import vanderbilt.thub.otp.model.Itinerary;
import vanderbilt.thub.otp.model.Leg;
import vanderbilt.thub.otp.model.PlannerRequest;
import vanderbilt.thub.otp.model.Response;
import vanderbilt.thub.otp.model.TraverseMode;
import vanderbilt.thub.otp.service.OTPService;
import vanderbilt.thub.otp.service.OTPSvcApi;

import static com.example.anne.otp_android_client_v3.MainActivity.ActivityState.ONE;
import static com.example.anne.otp_android_client_v3.MainActivity.ActivityState.TWO;
import static com.example.anne.otp_android_client_v3.ModeOptions.getSelectedModes;
import static vanderbilt.thub.otp.model.TraverseMode.BICYCLE;
import static vanderbilt.thub.otp.model.TraverseMode.BUS;
import static vanderbilt.thub.otp.model.TraverseMode.CAR;
import static vanderbilt.thub.otp.model.TraverseMode.SUBWAY;
import static vanderbilt.thub.otp.model.TraverseMode.WALK;


public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "MainActivity.java";

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    // Activity state

    public enum ActivityState {ONE, ONE_A, ONE_B_i, ONE_B_ii, TWO, TWO_A, THREE, FOUR, FOUR_A}

    private Stack<ActivityState> mStateStack;

    // API Client

    private GoogleApiClient mGoogleAPIClient;

    // UI fragments and layouts

    private GoogleMap mMap;

    private SlidingUpPanelLayout mSlidingPanelLayout;

    private PlaceAutocompleteFragment mAutocompleteFragment;

    private DetailedSearchBarFragment mDetailedSearchBarFragment;

    private FloatingActionButton mFAB;

    // Current selected source & destination

    private Place mSource = null;

    private Place mDestination = null;

    // List of itineraries from the current trip plan

    private List<Itinerary> mItineraryList;

    // List of polylines for the itinerary currently displayed on the map

    private List<Polyline> mPolylineList;

    // Destination marker

    private Marker mDestinationMarker;

    // BiMap mapping each ImageButton representing a mode to a TraverseMode

    private BiMap<TraverseMode, ImageButton> modeToImageButtonBiMap;


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
        setUpFloatingActionButton();

        // Initialize state
        mStateStack = new Stack<>();
        setState(ONE);
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
            // Custom operations
            switch (mStateStack.pop().toString()) {
                case("TWO"): {
                    Log.d(TAG, "Transitioning back from state TWO to state ONE");

                    // Revert map, center/zoom to current location, restore center button
                    mMap.setPadding(50,175,50,0);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(getCoordinates(null)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);

                    // Clear search box
                    mAutocompleteFragment.setText("");

                    // Hide FAB
                    mFAB.hide();

                    // Hide sliding panel
                    mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
            }

        }
    }

    /**
     * Helper method for setting up the navigation drawer
     */
    private void setUpDrawer() {

        // Get drawer layout and navigation view
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Highlight 'Planner' menu item in the navigation view
        navigationView.getMenu().getItem(0).setChecked(true);

        // Set item selected listener
        navigationView.setNavigationItemSelectedListener(new
                MyOnNavigationItemSelectedListener(drawer, navigationView));
    }

    /**
     * Helper method for setting up the PlaceAutocompleteFragment
     */
    private void setUpPlaceAutocompleteSearch() {
        mAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        mAutocompleteFragment.setHint("Where to?");


        mAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                Log.d(TAG, "Destination selected: " + place.getName());
                mDestination = place;
                transitionToStateTWO();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

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
        ModeOptions.setDefaultModes(Arrays.asList(BICYCLE, WALK));
        ModeOptions.selectDefaultModes();
    }

    /**
     * Helper method for setting up the sliding panel layout
     */
    private void setUpSlidingPanel() {
        mSlidingPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
    }

    /**
     * Helper method for setting up the floating action button
     */
    private void setUpFloatingActionButton() {
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
    }

    /**
     * Callback triggered when the map is ready to be used
     * Sets up the Google API Client & enables the compass and location features for the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setPadding(50,175,50,0);
        mMap.getUiSettings().setCompassEnabled(true);

        // Build the GoogleApiClient, enable my location
        if (checkLocationPermission()) {
            // Permission was already granted
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // If permission was not already granted, checkLocationPermission() requests
        // permission and executes the above enclosed statements after permission is granted
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
                    }
                    else {
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

        // Use the last known location to set the bounds bias for the Place autocomplete search bar
        mAutocompleteFragment.setBoundsBias(getBoundsBias());

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

        Log.d(TAG, "Try state change ONE to TWO");

        // Resize map & disable map center button
        mMap.setPadding(50,450,50,80);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Initialize a fragment transaction
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add showing the detailed search bar fragment to the transaction
        mDetailedSearchBarFragment = new DetailedSearchBarFragment();
        fragmentTransaction.add(R.id.detailed_search_bar_frame, mDetailedSearchBarFragment);

        // Add to stack and execute the fragment transaction
        fragmentTransaction.addToBackStack("Screen Two");
        fragmentTransaction.commit();

        // Plan the trip and display it on the map
        planAndDisplayTrip(null, mDestination);

        // Show sliding panel layout
        mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        // Show navigation button
        mFAB.show();

        setState(TWO);
        return true;
    }

    /**
     * Helper method to initialize the mode buttons in the detailed search bar
     */
    public void setUpModeButtons() {

        // Get the current selected modes
        Set<TraverseMode> selectedModes = getSelectedModes();

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
     */
    public void planAndDisplayTrip(com.google.android.gms.location.places.Place origin,
                                   com.google.android.gms.location.places.Place destination) {
        Log.d(TAG, "Planning trip");

        // Prompt user to choose a mode & exit if no modes are selected
        if (ModeOptions.getNumSelectedModes() == 0) {
            Toast.makeText(this, "Please select at least one mode of transportation",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // If public transport is selected, ensure that walk or bicycle or car is also selected
        Set<TraverseMode> selectedModes = ModeOptions.getSelectedModes();
        if ((selectedModes.contains(BUS) || selectedModes.contains(SUBWAY))
                && !(selectedModes.contains(WALK) || selectedModes.contains(BICYCLE))) {
            Toast.makeText(this, "Please select the WALK, BICYCLE, or CAR mode",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Set origin latlng
        final LatLng originLatLng = getCoordinates(origin);

        // Set destination latlng
        final LatLng destinationLatLng = getCoordinates(destination);

        // Exit if origin or destination was null and location access was denied
        if (originLatLng == null || destinationLatLng == null)
            return;

        // Display destination on map:
        if (mDestinationMarker != null) {
            mDestinationMarker.remove();
            mDestinationMarker = null;
        }
        // Draw and save a marker at the destination
        mDestinationMarker =  mMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .title("Destination"));
        // Move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                new LatLngBounds.Builder()
                        .include(originLatLng)
                        .include(destinationLatLng)
                        .build(), 100)
        );

        // Create and set up a new trip planner request
        final PlannerRequest request = new PlannerRequest();

        request.setFrom(new GenericLocation(originLatLng.latitude,
                originLatLng.longitude));
        request.setTo(new GenericLocation(destinationLatLng.latitude,
                destinationLatLng.longitude));
        request.setModes(ModeOptions.getSelectedModesString());
        Log.d(TAG, "Selected modes: " + ModeOptions.getSelectedModesString());

        // Set up the REST API service
        OTPService.buildRetrofit(OTPSvcApi.OTP_API_URL);
        String startLocation = Double.toString(request.getFrom().getLat()) +
                "," + Double.toString(request.getFrom().getLng());
        String endLocation = Double.toString(request.getTo().getLat()) +
                "," + Double.toString(request.getTo().getLng());

        // Make the request to OTP server
        Call<Response> response = OTPService.getOtpService().geTripPlan(OTPService.ROUTER_ID,
                startLocation,
                endLocation,
                request.getModes());
        response.enqueue(new Callback<Response>() {

            // Handle the request response
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                Log.d(TAG, "Received trip plan from server");

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
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Log.d(TAG, "Request failed to get itineraries:\n" + throwable.toString());
                Toast.makeText(getApplicationContext(),
                        "Request to server failed", Toast.LENGTH_LONG).show();
            }
        });

        Log.d(TAG, "Made request to OTP server");
        Log.d(TAG, "Starting point coordinates: " + originLatLng.toString());
        Log.d(TAG, "Destination coordinates: " + destinationLatLng.toString());
    }

    /**
     * Helper method to display an itinerary in polyline on the map
     */
    public void displayItinerary(Itinerary it, LatLng origin, LatLng destination) {

        Log.d(TAG, "Displaying itinerary");

        if (it == null || mMap == null) {
            Log.d(TAG, "Itinerary is null; failed to display");
            return;
        }

        // Remove previous itinerary if it exists
        if (mPolylineList != null) {
            for (Polyline polyline : mPolylineList)
                polyline.remove();
            mPolylineList = null;
        }

        // Get the legs of the itinerary and create a list for the corresponding polylines
        List<Leg> legList= it.getLegs();
        List<Polyline> polylineList = new LinkedList<>();

        // Display each leg on the map
        for (Leg leg : legList) {

            // Get a list of the points that make up the leg
            List<LatLng> points = PolyUtil.decode(leg.getLegGeometry().getPoints());

            // Draw a polyline on the map using the list of points
            PolylineOptions polylineOptions = new PolylineOptions().addAll(points).width(15);

            switch (leg.getMode()) {
                case ("WALK"): {
                    polylineOptions
                            .color(R.color.blue)
                            .pattern(Arrays.<PatternItem>asList(new Dot(), new Gap(15)));
                    break;
                }
                case ("BICYCLE"): {
                    polylineOptions
                            .color(R.color.blue)
                            .pattern(Arrays.<PatternItem>asList(new Dash(30), new Gap(20)));
                    break;
                }
                case ("CAR"): {
                    polylineOptions.color(R.color.green);
                    break;
                }
                case ("BUS"): {
                    polylineOptions.color(R.color.green);
                    break;
                }
                default: polylineOptions.color(R.color.green);
            }

            polylineList.add(mMap.addPolyline(polylineOptions));
        }

        // Save the list of polylines drawn on the map
        mPolylineList =  polylineList;

    }


    /**
     * Helper method that returns the coordinates of a Place, or, if the Place is null,
     * returns the current location
     * Returns null if a security exception was thrown
     */
    private LatLng getCoordinates(Place place) {
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
    private LatLngBounds getBoundsBias() {

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


}
