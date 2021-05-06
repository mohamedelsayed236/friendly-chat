package com.example.findingnearplaces.getplaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private HashMap<String, String> getSinglePlace (JSONObject  googlePlaceJSON){
       //usinf hashmap to put the data
        HashMap<String, String> goolePlcaeMap = new HashMap<>();
        String NameOfPlace = "-NA-";
        String Nearplace = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        try {
            // to fetch the data
            if (!googlePlaceJSON.isNull("name")){
                NameOfPlace = googlePlaceJSON.getString("name");

            }
            if (!googlePlaceJSON.isNull("vicinity")){
                Nearplace = googlePlaceJSON.getString("vicinity");

            }
            latitude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("latitude");
            longitude=googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("longitude");
            reference = googlePlaceJSON.getString("reference");

            //then put the data to hashmap
            goolePlcaeMap.put("place_name" ,NameOfPlace);
            goolePlcaeMap.put("near places" ,Nearplace);
            goolePlcaeMap.put("lat" ,latitude);
            goolePlcaeMap.put("lng" ,longitude);
            goolePlcaeMap.put("reference" ,reference);









        } catch (JSONException e) {
            e.printStackTrace();
        }
       return  goolePlcaeMap;
          // this method will store one place
    }
    private List<HashMap<String,String>> gatAllNearPlaces (JSONArray jsonArray){
        int Counter = jsonArray.length();
        List<HashMap<String,String>> NearPlacesList = new ArrayList<>();
        // this hash map to store every place in the list
        HashMap<String,String> NearPlaceMap  = null;
        for (int i =0 ; i<Counter ; i++){
            try {
                NearPlaceMap = getSinglePlace((JSONObject)jsonArray.get(i));
                NearPlacesList.add(NearPlaceMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
return  NearPlacesList;
    }
    public List<HashMap<String,String>> parse (String jsondata){
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            jsonArray = jsonObject .getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

return  gatAllNearPlaces(jsonArray);
    }

    }
