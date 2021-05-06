package com.example.findingnearplaces.getplaces;

import android.os.AsyncTask;

import com.example.findingnearplaces.bojo.DownLoadUrl;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String,String> {
    private String googleplaceData,URL;
    private GoogleMap mmap;
    @Override
    protected String doInBackground(Object... objects) {
        mmap = (GoogleMap)  objects[0];
        URL =(String) objects[1];
        DownLoadUrl downLoadUrl = new DownLoadUrl();
        try {
            googleplaceData = downLoadUrl.ReadData(URL);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return googleplaceData;
    }

    @Override
    protected void onPostExecute(String s)
    {
        List<HashMap<String,String>> nearPlaces=null;
        DataParser dataParser = new DataParser();
        nearPlaces= dataParser.parse(s);
        DisplaynearPlaces(nearPlaces);

    }


    private void DisplaynearPlaces(List<HashMap<String,String>> nearPlaces){
        for (int i =0 ; i<nearPlaces.size();i++){
            MarkerOptions markerOptions  = new MarkerOptions();
            HashMap<String,String> googlenearplace = nearPlaces.get(i);
            String NameOfPlace  = googlenearplace.get("place_name");
            String NearPlaces  = googlenearplace.get("near places");
            double lat= Double.parseDouble(googlenearplace.get("lat"));
            double lng= Double.parseDouble(googlenearplace.get("lng"));

            LatLng latLng = new LatLng(lat , lng);
            markerOptions.position(latLng);
            markerOptions.title(NameOfPlace + " : "+ NearPlaces );
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            //to move the camera to that location (zoom in the map)
            mmap.addMarker(markerOptions);
            mmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mmap.animateCamera(CameraUpdateFactory.zoomTo(14));





        }

    }
}


