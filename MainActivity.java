package com.example.hospitallocator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.squareup.retrofit2.Call;
import com.squareup.retrofit2.Callback;
import com.squareup.retrofit2.Response;
import com.squareup.retrofit2.Retrofit;
import com.squareup.retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView hospitalNameTextView;
    private TextView hospitalAddressTextView;
    private TextView hospitalDistanceTextView;
    private Button bookAmbulanceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        hospitalNameTextView = findViewById(R.id.hospitalName);
        hospitalAddressTextView = findViewById(R.id.hospitalAddress);
        hospitalDistanceTextView = findViewById(R.id.hospitalDistance);
        bookAmbulanceButton = findViewById(R.id.bookAmbulanceButton);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Handle the book ambulance button click
        bookAmbulanceButton.setOnClickListener(v -> bookAmbulance());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Get the user's current location
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        // Move the camera to the user's location
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                        // Fetch the nearest hospital
                        fetchNearestHospital(location);
                    }
                });
    }

    private void fetchNearestHospital(Location location) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GooglePlacesService service = retrofit.create(GooglePlacesService.class);

        Call<PlacesResponse> call = service.getNearbyHospitals(
                location.getLatitude() + "," + location.getLongitude(),
                5000, // 5km radius
                "hospital",
                getString(R.string.google_maps_key)
        );

        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacesResponse> call, @NonNull Response<PlacesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Place> hospitals = response.body().getResults();
                    if (!hospitals.isEmpty()) {
                        Place nearestHospital = hospitals.get(0);
                        showHospitalOnMap(nearestHospital, location);
                    } else {
                        Toast.makeText(MainActivity.this, "No hospitals found nearby", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlacesResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to fetch hospitals", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showHospitalOnMap(Place hospital, Location userLocation) {
        LatLng hospitalLocation = new LatLng(hospital.getGeometry().getLocation().getLat(),
                hospital.getGeometry().getLocation().getLng());

        // Add marker for the hospital
        mMap.addMarker(new MarkerOptions().position(hospitalLocation).title(hospital.getName()));

        // Update the hospital info section
        hospitalNameTextView.setText(hospital.getName());
        hospitalAddressTextView.setText(hospital.getVicinity());

        float[] results = new float[1];
        Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
                hospitalLocation.latitude, hospitalLocation.longitude, results);
        hospitalDistanceTextView.setText(String.format("Distance: %.2f km", results[0] / 1000));
    }

    private void bookAmbulance() {
        // Simulate booking an ambulance
        String hospitalName = hospitalNameTextView.getText().toString();
        if (!hospitalName.isEmpty()) {
            Toast.makeText(this, "Ambulance booked for " + hospitalName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No hospital selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
