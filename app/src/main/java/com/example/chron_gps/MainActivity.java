package com.example.chron_gps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static String FILE_ERROR ;
    private static String FILE_DATA ;



    int Update_pointer = 0;
    int Error_pointer = 0;

    ArrayList<String> Table_Data_1 = new ArrayList<String>();
    ArrayList<String> Table_Errors_1 = new ArrayList<String>();


    EditText mEditText;

    Calendar calendar = Calendar.getInstance();
    String currentTime;

    //Gia to FireBase
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    List<String> myList;
    List<String> TableList;



    //

    // references to the UI elements

    TextView  tv_sensor, tv_updates,  tv_wayPointsCounts;
    Button btn_newWaypoint, btn_showWayPointList, btn_save, btn_music;

    Switch sw_locationupdates, sw_gps;


    // variable to remember if we are tracking location or not.
    boolean updateOn = false;

    // current location
    Location currentLocation;

    //list of saved locations
    List<Location> savedLocations;


    //Location request is a config file for all setting related to FusedLocationClient.
    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    // Google's API for location services. The majority of the app functions using this class.
    FusedLocationProviderClient fusedLocationProviderClient;



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save(View v) {
        String text = mEditText.getText().toString();
        FileOutputStream fos = null;
        Error_pointer += 1;
        currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Table_Errors_1.clear();
        Table_Errors_1.add("Error no:"+Error_pointer+" Error: "+ text + " Time: "+ currentTime);


        //Firebase
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("do");
        //reference.setValue("llll");

        uploadList(v);



        //



        // plhrofories hmera
        try {
            FILE_ERROR = LocalDate.now().toString()+"Errors.txt";
            fos = openFileOutput(FILE_ERROR, MODE_APPEND);
            for (int i=0; i< Table_Errors_1.size(); i++)
                fos.write(((Table_Errors_1.get(i)+"\n").getBytes()));
            //fos.write(((Table_Errors)+"\n").getBytes());


            //fos.write(text.getBytes());




            mEditText.getText().clear();
            Toast.makeText(this, "Saved to" + getFilesDir() + "/" + FILE_ERROR, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Gia to Firebase
        myList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("data-location");
        TableList = new ArrayList<>();
        //


        //gia thn mousikh

        btn_music = findViewById(R.id.btn_music);
        btn_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMusic_List();

            }
        });

        //gia to grapsimo sthn othonh
        mEditText = findViewById(R.id.edit_text);

        // give each UI variable a value


        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        btn_newWaypoint = findViewById(R.id.btn_newWayPoint);
        btn_showWayPointList = findViewById(R.id.btn_showWayPointList);
        tv_wayPointsCounts = findViewById(R.id.tv_countOfCrumbs);

        locationRequest = new LocationRequest();

        //how often does the default location check occur?
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        //how often does the location check occur when set to the most frequent update?
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //event that is triggered whenever the update interval is met.
        locationCallBack = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // save the location
                updateUIValues(locationResult.getLastLocation());
            }
        };

        btn_newWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the gps location

                //add the new location to the global list
                MyApplication myApplication = (MyApplication)getApplicationContext();
                savedLocations = myApplication.getMyLocations();
                savedLocations.add(currentLocation);
            }
        });


        btn_showWayPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ShowSavedLocationLists.class);
                startActivity(i);
            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    //most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }

            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationupdates.isChecked()) {
                    // turn on location tracking
                    startLocationUpdates();
                } else {
                    //turn of location tracking
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();

    }// end onCreate method



    private void stopLocationUpdates() {
        tv_updates.setText("Location is NOT being tracked");

        tv_sensor.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {

        tv_updates.setText("Location is being tracked");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            }
            else {
                Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                finish();
            }
            break;
        }
    }

    private void updateGPS() {
        //get permissions from the user to track GPS
        //get the current location from the fused client
        //update the UI - i.e. set all properties in their associated text view items.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onSuccess(Location location) {
                    //we got permissions.Put the values of location. XXX into the UI components.
                    if (location != null)
                    {
                     updateUIValues(location);
                    }
                    currentLocation = location;



                    //updateUIValues(location);
                }
            });

        }
        else {
            // permissions not granted yet.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUIValues(Location location) {
        // update all of the text view objects with a ne Location.


        String Latitude = String.valueOf(location.getLatitude());
        String Longtitude = String.valueOf(location.getLongitude());
        String Altitude = String.valueOf(location.getAltitude());
        String Accuracy = String.valueOf(location.getAccuracy());
        String Speed = String.valueOf(location.getSpeed());
        FileOutputStream foss = null;
        Update_pointer += 1;
        currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        Table_Data_1.clear();
        Table_Data_1.add("Enhmerwsh no:"+Update_pointer + " " + "Lat:" + Latitude + " " + "Long:" + Longtitude + " " + "Alt:" + Altitude + " " + "Acc:" + Accuracy + " " + "Speed:" + Speed+ " Time: "+ currentTime);



        MyApplication myApplication = (MyApplication)getApplicationContext();
        savedLocations = myApplication.getMyLocations();

        //show the number of wayponits saved.
        tv_wayPointsCounts.setText(Integer.toString(savedLocations.size()));

        
       
        try {

            FILE_DATA = LocalDate.now().toString()+"Data.txt";
            foss = openFileOutput(FILE_DATA, MODE_APPEND);

                      for (int i=0; i< Table_Data_1.size(); i++)
                      foss.write(((Table_Data_1.get(i)+"\n").getBytes()));
            //foss.write((Table.get(Update_pointer).getBytes()));
            //}
            //else {
                  //foss.write((Table.get(0).getBytes()));


            //}


            Toast.makeText(this, "Saved to" + getFilesDir() + "/" + FILE_DATA, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (foss != null){
                try {
                    foss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    //Gia thn mousikh
    public void openMusic_List(){
        Intent intent = new Intent(this, Music_List.class);
        startActivity(intent);

    }


    public void uploadList(View v){
        myList.add("12 AA");
        myList.add("13 AA");
        myList.add("14 AA");
        myList.add(Table_Data_1.toString());

        //gia na steileis
        //ArrayList<String> Recent = new ArrayList<>();
        //DatabaseReference resent = FirebaseDatabase.getInstance().getReference().child("yo");
        //resent.addValueEventListener(new ValueEventListener() {
            //@Override
            //public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Recent.clear();
                //for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                  //  Recent.add(snapshot.getValue().toString());
                //}
            //}

            //@Override
            //public void onCancelled(@NonNull DatabaseError dataseterror) {

            //}
        //});


        reference.setValue(myList)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"List is uploaded successfully", Toast.LENGTH_LONG).show();
                    }
                    
                }
            
        });


    }

    public void TableUpload(View v)
    {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if (datasnapshot.exists()){
                    TableList.clear();
                    for (DataSnapshot dss:datasnapshot.getChildren())
                    {
                      String timeName = dss.getValue(String.class);
                      TableList.add(timeName);
                    }

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i=0;i<TableList.size(); i++)
                    {
                        stringBuilder.append(TableList.get(i) + ",");
                    }
                    Toast.makeText(getApplicationContext(), stringBuilder.toString(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError dataseterror) {

            }
        });

    }
}

//// Mouaxaxaxaxa