package com.example.earthquake.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquake.Model.EarthQuake;
import com.example.earthquake.R;
import com.example.earthquake.Util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuakesListActivity extends AppCompatActivity {
    private ListView listView;
    private RequestQueue queue;
    private ArrayList<String> arrayList;
    private List<EarthQuake> quakeList;
   private ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quakes_list);
        quakeList=new ArrayList<>();
        arrayList=new ArrayList<>();
        listView=findViewById(R.id.listview);
          queue=Volley.newRequestQueue(this);
        getAllQuakes( Constants.URL);

    }
    void getAllQuakes(String url)
    {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EarthQuake earthQuake = new EarthQuake();
                try {
                    JSONArray features = response.getJSONArray("features");
                    for (int i = 0; i < Constants.LIMIT; i++) {
                        JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                        //Log.d("Place", "onResponse: "+properties.getString("place"));
                        JSONObject geometry = features.getJSONObject(i).getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        double lon = coordinates.getDouble(0);
                        double lat = coordinates.getDouble(1);
                        //Log.d("Quake" ," " +lon +" "+lat);

                        earthQuake.setPlace(properties.getString("place"));
                        earthQuake.setType(properties.getString("type"));
                        earthQuake.setDetailLink(properties.getString("detail"));
                        earthQuake.setLat(lat);
                        earthQuake.setLon(lon);
                        earthQuake.setMag(properties.getDouble("mag"));
                        earthQuake.setTime(properties.getLong("time"));

                        arrayList.add(earthQuake.getPlace());

                        arrayAdapter=new ArrayAdapter(QuakesListActivity.this,android.R.layout.simple_list_item_1,
                                android.R.id.text1,arrayList);
                        listView.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();


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
        queue.add(jsonObjectRequest);
    }

}
