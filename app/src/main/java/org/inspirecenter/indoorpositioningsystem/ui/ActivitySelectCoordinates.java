package org.inspirecenter.indoorpositioningsystem.ui;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.inspirecenter.indoorpositioningsystem.R;

public class ActivitySelectCoordinates extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private GoogleMap googleMap;

    private CrossHairView crossHairView;

    public static final String INTENT_EXTRA_LOCATION_NAME_KEY = "location_name";
    public static final String INTENT_EXTRA_FLOOR_NAME_KEY = "floor_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_coordinates);

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        crossHairView = (CrossHairView) findViewById(R.id.activity_select_coordinates_crosshair);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public static final String INTENT_EXTRA_INITIAL_LAT_KEY = "initial_lat";
    public static final String INTENT_EXTRA_INITIAL_LNG_KEY = "initial_lng";

    @Override
    protected void onResume() {
        super.onResume();

        final Intent intent = getIntent();
        final String locationName = intent.hasExtra(INTENT_EXTRA_LOCATION_NAME_KEY) ? intent.getStringExtra(INTENT_EXTRA_LOCATION_NAME_KEY) : "unknown";
        final String floorName = intent.hasExtra(INTENT_EXTRA_FLOOR_NAME_KEY) ? intent.getStringExtra(INTENT_EXTRA_FLOOR_NAME_KEY) : "unknown";
        crossHairView.setNames(floorName, locationName);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location mLastLocation = null;
        try {
            mLastLocation = locationManager != null ? locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) : null;
        } catch (SecurityException se) {
            mLastLocation = null;
        }

        final double initialLat = getIntent().getDoubleExtra(INTENT_EXTRA_INITIAL_LAT_KEY, mLastLocation != null ? mLastLocation.getLatitude() : 35.008409d);
        final double initialLng = getIntent().getDoubleExtra(INTENT_EXTRA_INITIAL_LNG_KEY, mLastLocation != null ? mLastLocation.getLongitude() : 33.696683d);

        // move the camera and zoom
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initialLat, initialLng), 15.0f));

        // set to hybrid (i.e. with satellite)
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // enable zoom buttons
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);

        // register for camera updates
        this.googleMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        crossHairView.setSelectedCoordinates(cameraPosition.target.latitude, cameraPosition.target.longitude);
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public static final String SELECTED_LAT = "selected_lat";
    public static final String SELECTED_LNG = "selected_lng";

    public void save(View view) {
        final Intent data = new Intent();
        data.putExtra(SELECTED_LAT, googleMap.getCameraPosition().target.latitude);
        data.putExtra(SELECTED_LNG, googleMap.getCameraPosition().target.longitude);
        setResult(RESULT_OK, data);
        finish();
    }
}
