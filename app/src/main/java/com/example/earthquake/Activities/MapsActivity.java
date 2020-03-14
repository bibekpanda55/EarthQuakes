package com.example.earthquake.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquake.Model.EarthQuake;
import com.example.earthquake.R;
import com.example.earthquake.UI.CustominfoWindow;
import com.example.earthquake.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener,GoogleMap.OnMarkerClickListener  {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private RequestQueue requestQueue;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private BitmapDescriptor[] iconColors;
    private Button showListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        showListBtn=findViewById(R.id.showListBtn);
        showListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(MapsActivity.this,QuakesListActivity.class));
            }
        });
         iconColors=new BitmapDescriptor[]
                 {
                         BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                         BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                         BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                         BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                         BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                         BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                         BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                         BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                         BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                 };
        requestQueue= Volley.newRequestQueue(this);
        getEarthQuakes();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustominfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);


        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//                LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//      mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(latLng)
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
//                        .title("Hello"));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
          }
        }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
             if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
             {
                   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                 //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        getQuakeDetails(marker.getTag().toString());
        //Toast.makeText(this, marker.getTag().toString() , Toast.LENGTH_SHORT).show();

    }



    private void getEarthQuakes() {
        final EarthQuake earthQuake = new EarthQuake();
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, Constants.URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray features=response.getJSONArray("features");
                            for(int i=0;i<Constants.LIMIT;i++)
                            {
                                JSONObject properties=features.getJSONObject(i).getJSONObject("properties");
                                //Log.d("Place", "onResponse: "+properties.getString("place"));
                                JSONObject geometry=features.getJSONObject(i).getJSONObject("geometry");
                                JSONArray coordinates=geometry.getJSONArray("coordinates");

                                double lon=coordinates.getDouble(0);
                                double lat=coordinates.getDouble(1);
                                //Log.d("Quake" ," " +lon +" "+lat);

                                earthQuake.setPlace(properties.getString("place"));
                                earthQuake.setType(properties.getString("type"));
                                earthQuake.setDetailLink(properties.getString("detail"));
                                earthQuake.setLat(lat);
                                earthQuake.setLon(lon);
                                earthQuake.setMag(properties.getDouble("mag"));
                                earthQuake.setTime(properties.getLong("time"));

                                java.text.DateFormat dateFormat=java.text.DateFormat.getDateInstance();
                                String formattedDate=dateFormat.format(new Date(Long.valueOf(properties.getLong("time"))).getTime());


                                MarkerOptions markerOptions=new MarkerOptions();
                                markerOptions.icon(iconColors[Constants.randomInt(iconColors.length,0)]);
                                markerOptions.title(earthQuake.getPlace());
                                markerOptions.position(new LatLng(lat,lon));
                                markerOptions.snippet("Mag:"+earthQuake.getMag() +"\n" +"Date:"+formattedDate);

                                if(earthQuake.getMag()>=2.0)
                                {
                                    CircleOptions circleOptions =new CircleOptions();
                                    circleOptions.center(new LatLng(earthQuake.getLat(),earthQuake.getLon()));
                                    circleOptions.radius(30000);
                                    circleOptions.strokeWidth(3.6f);
                                    circleOptions.fillColor(Color.RED);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                    mMap.addCircle(circleOptions);
                                }

                                Marker marker=mMap.addMarker(markerOptions);
                                marker.setTag(earthQuake.getDetailLink());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),4));


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void getQuakeDetails(String url) {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String detailURL="";
                try {
                    JSONObject properties=response.getJSONObject("properties");
                    JSONObject products=properties.getJSONObject("products");
                    JSONArray geoserve=products.getJSONArray("geoserve");

                    for(int i=0;i<geoserve.length();i++)
                    {
                        JSONObject geoserveObj=geoserve.getJSONObject(i);
                        JSONObject contentObj=geoserveObj.getJSONObject("contents");
                        JSONObject geoJsonObj=contentObj.getJSONObject("geoserve.json");
                         detailURL=geoJsonObj.getString("url");
                    }
                    Log.d("URL", "onResponse: "+detailURL);
                    getMoreDetails(detailURL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);

    }
    private void getMoreDetails(String url)
    {
       JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               builder=new AlertDialog.Builder(MapsActivity.this);
                 View view =getLayoutInflater().inflate(R.layout.popup,null);

               Button dismissButton=view.findViewById(R.id.dismiss_pop);
               Button dismissButtonTop=view.findViewById(R.id.dismiss_popup);
               TextView popList=view.findViewById(R.id.pop_list);
               WebView html=view.findViewById(R.id.htmlWebView);

                StringBuilder stringBuilder=new StringBuilder();
               try {
                   if(response.has("tectonicSummary")&& response.getString("tectonicSummary")!=null)
                   {
                       JSONObject tectonic=response.getJSONObject("tectonicSummary");
                       if(tectonic.has("text") && tectonic.getString("text")!=null)
                       {
                           String text=tectonic.getString("text");

                           html.loadDataWithBaseURL(null,text,"text/html","UTF-8",null);
                       }
                   }
                   JSONArray cities =response.getJSONArray("cities");
                   for(int i=0;i<cities.length();i++)
                   {
                       JSONObject citiesObject=cities.getJSONObject(i);
                       stringBuilder.append("City: " +citiesObject.getString("name")+
                               "\n" +"Distsnce: " +citiesObject.getString("distance")
                               +"\n" +"Population: " +citiesObject.getString("population"));
                        stringBuilder.append("\n\n");

                   }
                   popList.setText(stringBuilder);

                   dismissButton.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           alertDialog.dismiss();
                       }
                   });
                   dismissButtonTop.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           alertDialog.dismiss();
                       }
                   });
                   builder.setView(view);
                   alertDialog=builder.create();
                   alertDialog.show();
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {

           }
       });
       requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
