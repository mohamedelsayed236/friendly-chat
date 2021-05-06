package com.example.findingnearplaces;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.findingnearplaces.getplaces.GetNearbyPlaces;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @BindView(R.id.hospital_img_btn)
    ImageButton hospitalImgBtn;
    @BindView(R.id.school_img_btn)
    ImageButton schoolImgBtn;
    @BindView(R.id.restaurant_img_btn)
    ImageButton restaurantImgBtn;
    @BindView(R.id.second_rl)
    RelativeLayout secondRl;
    @BindView(R.id.search_img_btn)
    ImageButton searchImgBtn;
    @BindView(R.id.address_field)
    EditText addressField;
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    double latiide, longitude;
    private  int proximityRadius = 10000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkUserLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApi();
            mMap.setMyLocationEnabled(true);
        }

    }

    public boolean checkUserLocationPermission() {
        // app ask to get permission from user to get location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);

            }
            return false;
        } else {
            return true;
        }
    }

    //method to get the request permission result(handle permission request response)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApi();
                        }
                        mMap.setMyLocationEnabled(true);
                    } else {
                        Toast.makeText(this, "permisson denied... ", Toast.LENGTH_LONG).show();
                    }
                    return;
                }

        }
    }

    protected synchronized void buildGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latiide = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        //to put a marker for the places of the user found and delete the stored markers
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();

        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //display the marker of the current position of the user
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentUserLocationMarker = mMap.addMarker(markerOptions);
        //to move the camera to that location (zoom in the map)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(14));
        if (googleApiClient != null) {

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //to update user place
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @OnClick({R.id.hospital_img_btn, R.id.school_img_btn, R.id.restaurant_img_btn, R.id.search_img_btn})
    public void onViewClicked(View view) {
        String hospital = "hospital", school = "school",  restaurant = "restaurant";
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        switch (view.getId()) {
            case R.id.search_img_btn:
                //if a user want to search location this method will return 6 locations
                String address = addressField.getText().toString();
                List<Address> addressList=null;
                MarkerOptions markerOptions = new MarkerOptions();
                if (!TextUtils.isEmpty(address)){
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList= geocoder.getFromLocationName(address,6);
                        if(addressList!= null){
                            // get addresses one by one
                            for (int i =0 ; i<addressList.size();i++){
                                Address useraddress = addressList.get(i);
                                LatLng latLng = new LatLng(useraddress.getLatitude(),useraddress.getLongitude());
                                markerOptions.position(latLng);
                                markerOptions.title(address);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                //to move the camera to that location (zoom in the map)
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                            }
                        }
                        else {
                            Toast.makeText(this," Sorry Location Not Found....",Toast.LENGTH_LONG).show();


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(this,"Please write any Location name....",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.hospital_img_btn:
                mMap.clear();
                String url = getURL(latiide , longitude,hospital);
                transferData[0] =mMap;
                transferData[1]= url;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(this,"searching for near hospitals",Toast.LENGTH_LONG).show();
                Toast.makeText(this,"showing for near places",Toast.LENGTH_LONG).show();


                break;
            case R.id.school_img_btn:
                mMap.clear();
                 url = getURL(latiide , longitude,school);
                transferData[0] =mMap;
                transferData[1]= url;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(this,"searching for schools",Toast.LENGTH_LONG).show();
                Toast.makeText(this,"showing for near schools",Toast.LENGTH_LONG).show();

                break;
            case R.id.restaurant_img_btn:
                mMap.clear();
                 url = getURL(latiide , longitude,restaurant);
                transferData[0] =mMap;
                transferData[1]= url;
                getNearbyPlaces.execute(transferData);
                Toast.makeText(this,"searching for restaurants",Toast.LENGTH_LONG).show();
                Toast.makeText(this,"showing for near restaurants",Toast.LENGTH_LONG).show();
                break;

        }
    }

    private String getURL(double latiide, double longitude, String nearplace) {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latiide +","+longitude);
        googleURL.append("&radius="+ proximityRadius);
        googleURL.append("&type="+ nearplace);
        googleURL.append("&sensor= true");
        googleURL.append("&key="+ "AIzaSyD-pequArYySBBnm9iY9YR4cnLCQDkcpIk");
        Log.d("GoogleMapsActivity","url="+ googleURL.toString());

        return googleURL.toString();




    }
}
