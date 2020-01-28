package com.test.countrycapitalsmaps;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RequestQueue mRequestQueue;
    private ArrayList<MarkerData> markersArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mRequestQueue = Volley.newRequestQueue(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        jsonparse();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MapsActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                TextView cap = bottomSheetDialog.findViewById(R.id.capital);
                TextView reg = bottomSheetDialog.findViewById(R.id.region);
                TextView pop = bottomSheetDialog.findViewById(R.id.pop);
                TextView latlng = bottomSheetDialog.findViewById(R.id.lat_Lng);
                for (int i = 0; i < markersArray.size(); i++) {
                    if (marker.getTitle().equals(markersArray.get(i).getName())) {
                        cap.setText("Capital: " + markersArray.get(i).getCapital());
                        reg.setText("Region: " + markersArray.get(i).getRegion());
                        pop.setText("Population: " + markersArray.get(i).getPopulation());
                        latlng.setText(markersArray.get(i).getLatitude() + ", " + markersArray.get(i).getLongitude());
                    }
                }
                bottomSheetDialog.show();

                return false;
            }
        });

    }

    private void jsonparse() {
        String url = "https://restcountries.eu/rest/v2/all";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i <= 249; i++) {
                                if (i == 33)
                                    continue;
                                JSONObject jsonObject = response.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                String capital = jsonObject.getString("capital");
                                String region = jsonObject.getString("region");
                                long population = jsonObject.getInt("population");
                                double lat, lng;
                                JSONArray jsonArray = jsonObject.getJSONArray("latlng");
                                lat = jsonArray.getDouble(0);
                                lng = jsonArray.getDouble(1);
                                MarkerData markerData = new MarkerData();
                                markerData.setLatitude(lat);
                                markerData.setLongitude(lng);
                                markerData.setName(name);
                                markerData.setCapital(capital);
                                markerData.setRegion(region);
                                markerData.setPopulation(population);
                                markersArray.add(markerData);
                            }
                            showOnMap();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error Communicating with Server", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

    private void showOnMap() {
        for (int i = 0; i < markersArray.size(); i++) {
            Log.v("asdasdasd", "asd......\n\n\n\n\n\n" + ".................."
                    + markersArray.get(i).getLatitude() + "," + markersArray.get(i).getLongitude() +
                    ".........\n\n\n\n\n\n......." + "..........\n\n\n\n\n\n\n");
            createMarker(markersArray.get(i).getLatitude(), markersArray.get(i).getLongitude()
                    , markersArray.get(i).getName(), markersArray.get(i).getCapital(), markersArray.get(i).getRegion(),
                    markersArray.get(i).getPopulation());
        }
    }

    protected Marker createMarker(double latitude, double longitude, String name, String capital, String region, long population) {
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude)).title(name).
                        snippet("Capital: " + capital));
    }

}
