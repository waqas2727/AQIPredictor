package com.example.aqipredictor;

import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.maps.android.heatmaps.Gradient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Result extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Dialog weatherdetail;
    private Dialog chart;
    private FloatingActionButton floatingActionButton;
    private BottomAppBar bottomAppBar;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    private ScrollView mScrollView;
    private ArrayList<LatLng> helplist;
    private Intent intent;
    private ImageView userimage;
    Double lat,longi;
    private String feelsv,pressurev,sunrisev,sunsetv,temperaturev,humidv;
    private String result,cityName,countryName;
    private TextView userresult,weather,weather2;
    private Gradient redgradient,greengradient,bluegradient;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        result = intent.getStringExtra("result");

        weatherdetail = new Dialog(this);
        chart = new Dialog(this);

        floatingActionButton = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bottomAppBar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userimage = (ImageView) findViewById(R.id.userimage);
        userresult = (TextView) findViewById(R.id.resulttext);
        weather = (TextView) findViewById(R.id.weather);
        weather2 = (TextView)findViewById(R.id.weather2);
        userimage.setImageBitmap(Image.bitmap);
        if (result.equals("[0]"))
            userresult.setText("AVERAGE");
        else if (result.equals("[1]"))
            userresult.setText("BAD");
        else if (result.equals("[2]"))
            userresult.setText("GOOD");
//        else if (result.equals("NO")) {
//            Toast.makeText(this,"Please Read Instructions Carefully and Upload Pic again:(",Toast.LENGTH_LONG).show();
//            intent = new Intent(this,Menu.class);
//            startActivity(intent);
//        }
        else {
            Toast.makeText(this,"Some error occurs and Upload Pic again :(",Toast.LENGTH_LONG).show();
//            intent = new Intent(this,Image.class);
//            startActivity(intent);
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.valueOf(Menu.latitude),Double.valueOf(Menu.longitude) , 1);
            cityName = addresses.get(0).getLocality();
            //String stateName = addresses.get(0).getAddressLine(1);
            //countryName = addresses.get(0).getAddressLine(2);
            Log.i("country1",cityName);
            //Log.i("country2",countryName);
            //Log.i("country3",addresses.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("exception",e.toString());
        }
        Log.i("country4",cityName);
        //Log.i("country5",countryName);
        //Log.i("country6",addresses.toString());
        DownloadTask task = new DownloadTask();
        task.execute("https://openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=b6907d289e10d714a6e88b30761fae22");
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Image image = new Image();
                image.showPictureDialog();
            }
        });

    }

    public void showWeather(View view){
        TextView close,humidity,pressure,feel,sunrise,sunset ;

        weatherdetail.setContentView(R.layout.weatherdetail);
        close = weatherdetail.findViewById(R.id.textview1);
        humidity = weatherdetail.findViewById(R.id.textView12);
        pressure = weatherdetail.findViewById(R.id.textView6);
        feel = weatherdetail.findViewById(R.id.textView58);
        sunrise = weatherdetail.findViewById(R.id.textView9);
        sunset = weatherdetail.findViewById(R.id.textView11);

        humidity.setText(humidv+" %");
        pressure.setText(pressurev + "MB");
        feel.setText(feelsv + " C");
        sunrise.setText(sunrisev + " AM");
        sunset.setText(sunsetv + " PM");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherdetail.dismiss();
            }
        });
        weatherdetail.show();
    }

    public void showrange(View view){
        TextView close ;
        chart.setContentView(R.layout.aqichart);
        close = chart.findViewById(R.id.textview30);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chart.dismiss();
            }
        });
        chart.show();
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();

                while(data!=-1){
                    char current=(char) data;
                    result+=current;
                    data=reader.read();

                }

                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject parentObject = new JSONObject(s);
                Log.i("Weather content2", String.valueOf(parentObject));
                JSONObject userDetails = parentObject.getJSONObject("main");
                JSONObject sysDetails = parentObject.getJSONObject("sys");
                JSONObject weatherDetails = parentObject.getJSONObject("weather");

                temperaturev = userDetails.getString("temp");
                humidv = userDetails.getString("humidity") ;
                feelsv = userDetails.getString("feels_like");
                pressurev = userDetails.getString("pressure");
                sunrisev =  sysDetails.getString("sunrise");
                sunsetv = sysDetails.getString("sunset");

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("error",e.toString());
                Toast.makeText(Result.this, "Some Exception occured ", Toast.LENGTH_SHORT).show();
            }


        }
    }


}
