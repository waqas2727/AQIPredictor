package com.example.aqipredictor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Menu extends AppCompatActivity {

    private Dialog instruction;
    private Button camera;
    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    static String latitude,longitude,cityName,countryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        instruction = new Dialog(this);
        camera = (Button) findViewById(R.id.camera);
        final Intent intent = new Intent(this,Result.class);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {
                    Toast.makeText(Menu.this, "No Internet", Toast.LENGTH_LONG).show();
                } else {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    //Check gps is enable or not
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //Write Function To enable gps
                        OnGPS();
                        getLocation();
                    } else {
                        //GPS is already On then
                        getLocation();
                    }
                }
            }
        });

        showInstruction();

    }

    public void showInstruction() {
        Button close;
        instruction.setContentView(R.layout.instructionspopup);
        close = instruction.findViewById(R.id.button);
        instruction.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instruction.dismiss();
            }
        });

    }

    private void getLocation() {

        //Check Permissions again
        Intent intent = new Intent(this,Image.class);

        if (ActivityCompat.checkSelfPermission(Menu.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Menu.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else
        {
            Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            Double lat=0.0,longi=0.0;

            if (LocationGps !=null)
            {
                lat=LocationGps.getLatitude();
                longi=LocationGps.getLongitude();
                getCity(lat,longi);

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                intent = new Intent(this,Image.class);
                startActivity(intent);
            }
            else if (LocationNetwork !=null)
            {
                lat=LocationNetwork.getLatitude();
                longi=LocationNetwork.getLongitude();
                getCity(lat,longi);

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
                intent = new Intent(this,Image.class);
                startActivity(intent);
            }
            else if (LocationPassive !=null)
            {
                lat=LocationPassive.getLatitude();
                longi=LocationPassive.getLongitude();
                getCity(lat,longi);

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);

                intent = new Intent(this,Image.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }

        }

    }

    private  void getCity(double lat,double longi){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, longi, 1);
            cityName = addresses.get(0).getAddressLine(0);
            //String stateName = addresses.get(0).getAddressLine(1);
            countryName = addresses.get(0).getAddressLine(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

}
