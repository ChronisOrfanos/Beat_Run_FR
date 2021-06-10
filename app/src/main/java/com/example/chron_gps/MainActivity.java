package com.example.chron_gps;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

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

    //Gia INTENT---------------------------------------------
    String User_Name;
    String userName;
    //String User_Name_tel;

    //Telos INTENT-------------------------------------------

    //Gia to FireBase-----------------------------
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    List<String> myList;
    List<String> TableList;
    //Telos FireBase------------------------------

    //FireBase Download-----------------------------------------------------------------------------
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    ArrayList <StorageReference> reflist = new ArrayList<>();
    StorageReference ref1,ref2,ref3,ref4,ref5,ref6,ref7,ref8,ref9,ref10;
    //Telos FireBase Download-----------------------------------------------------------------------


    //

    // references to the UI elements

    TextView  tv_sensor, tv_updates,  tv_wayPointsCounts;
    Button btn_newWaypoint, btn_showWayPointList, btn_save, btn_music, down;

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
        if (User_Name.equals("Chronis")||User_Name.equals("Chronis ")){reference = rootNode.getReference("Chronis_Runs");
        } else if (User_Name.equals("Xirorafas")||User_Name.equals("Xirorafas ")){reference = rootNode.getReference("Xiro_Runs");
        } else if (User_Name.equals("Sousanis")||User_Name.equals("Sousanis ")){reference = rootNode.getReference("Sousanis_Runs");
        } else if (User_Name.equals("Guru")||User_Name.equals("Guru ")){reference = rootNode.getReference("Guru_Runs");
        } else if (User_Name.equals("Moustakas")||User_Name.equals("Moustakas ")){reference = rootNode.getReference("Moustakas_Runs");
        } else if (User_Name.equals("Levis")||User_Name.equals("Levis ")){reference = rootNode.getReference("Levis_Runs");
        } else {reference = rootNode.getReference("New_User_Runs");}
        //reference = rootNode.getReference("sooo");
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


        //Gia FireBase Download---------------------------------------------------------------------
        down = findViewById(R.id.down);

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(userName);
            }
        });
        //Telos FireBase Download-------------------------------------------------------------------


        //Gia to INTENT-----------------------------------------------------------------------------
        Intent intent = getIntent();
        String Name_Activity_1 = intent.getStringExtra(Start_Activity.Share_User);
        User_Name = Name_Activity_1;

        userName = Name_Activity_1;

        //Intent intent1 = getIntent();
        //String Name_Activity_2 =intent1.getStringExtra(Start_Activity.Share_User_2);
        //User_Name_tel = Name_Activity_2;
        //Telos INTENT------------------------------------------------------------------------------

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


    //Gia to FireBase Download----------------------------------------------------------------------
    public void download(String userName)
    {

        //if(userName == "Chronis"){}

        storageReference = firebaseStorage.getInstance().getReference();

        if (userName.equals("Chronis")||userName.equals("Chronis "))
        {ref1 = storageReference.child("John").child("1.mp3");
            ref2 = storageReference.child("").child("2.mp3");
            ref3 = storageReference.child("Chronis").child("3.mp3");
            ref4 = storageReference.child("Chronis").child("4.mp3");
            ref5 = storageReference.child("Chronis").child("5.mp3");
            ref6 = storageReference.child("Chronis").child("6.mp3");
            ref7 = storageReference.child("Chronis").child("7.mp3");
            ref8 = storageReference.child("Chronis").child("8.mp3");
            ref9 = storageReference.child("Chronis").child("9.mp3");
            ref10 = storageReference.child("Chronis").child("10.mp3");
        }else if(userName.equals("Xirorafas")||userName.equals("Xirorafas ")){
            ref1 = storageReference.child("Xirorafas").child("1.mp3");
            ref2 = storageReference.child("Xirorafas").child("2.mp3");
            ref3 = storageReference.child("Xirorafas").child("3.mp3");
            ref4 = storageReference.child("Xirorafas").child("4.mp3");
            ref5 = storageReference.child("Xirorafas").child("5.mp3");
            ref6 = storageReference.child("Xirorafas").child("6.mp3");
            ref7 = storageReference.child("Xirorafas").child("7.mp3");
            ref8 = storageReference.child("Xirorafas").child("8.mp3");
            ref9 = storageReference.child("Xirorafas").child("9.mp3");
            ref10 = storageReference.child("Xirorafas").child("10.mp3");
        }else if(userName.equals("Sousanis")||userName.equals("Sousanis ")){
            ref1 = storageReference.child("Sousanis").child("1.mp3");
            ref2 = storageReference.child("Sousanis").child("2.mp3");
            ref3 = storageReference.child("Sousanis").child("3.mp3");
            ref4 = storageReference.child("Sousanis").child("4.mp3");
            ref5 = storageReference.child("Sousanis").child("5.mp3");
            ref6 = storageReference.child("Sousanis").child("6.mp3");
            ref7 = storageReference.child("Sousanis").child("7.mp3");
            ref8 = storageReference.child("Sousanis").child("8.mp3");
            ref9 = storageReference.child("Sousanis").child("9.mp3");
            ref10 = storageReference.child("Sousanis").child("10.mp3");
        }else if(userName.equals("Guru")||userName.equals("Guru ")){
            ref1 = storageReference.child("Guru").child("1.mp3");
            ref2 = storageReference.child("Guru").child("2.mp3");
            ref3 = storageReference.child("Guru").child("3.mp3");
            ref4 = storageReference.child("Guru").child("4.mp3");
            ref5 = storageReference.child("Guru").child("5.mp3");
            ref6 = storageReference.child("Guru").child("6.mp3");
            ref7 = storageReference.child("Guru").child("7.mp3");
            ref8 = storageReference.child("Guru").child("8.mp3");
            ref9 = storageReference.child("Guru").child("9.mp3");
            ref10 = storageReference.child("Guru").child("10.mp3");
        }else if(userName.equals("Moustakas")||userName.equals("Moustakas ")){
            ref1 = storageReference.child("Moustakas").child("1.mp3");
            ref2 = storageReference.child("Moustakas").child("2.mp3");
            ref3 = storageReference.child("Moustakas").child("3.mp3");
            ref4 = storageReference.child("Moustakas").child("4.mp3");
            ref5 = storageReference.child("Moustakas").child("5.mp3");
            ref6 = storageReference.child("Moustakas").child("6.mp3");
            ref7 = storageReference.child("Moustakas").child("7.mp3");
            ref8 = storageReference.child("Moustakas").child("8.mp3");
            ref9 = storageReference.child("Moustakas").child("9.mp3");
            ref10 = storageReference.child("Moustakas").child("10.mp3");
        }else if(userName.equals("Levis")||userName.equals("Levis ")){
            ref1 = storageReference.child("Levis").child("1.mp3");
            ref2 = storageReference.child("Levis").child("2.mp3");
            ref3 = storageReference.child("Levis").child("3.mp3");
            ref4 = storageReference.child("Levis").child("4.mp3");
            ref5 = storageReference.child("Levis").child("5.mp3");
            ref6 = storageReference.child("Levis").child("6.mp3");
            ref7 = storageReference.child("Levis").child("7.mp3");
            ref8 = storageReference.child("Levis").child("8.mp3");
            ref9 = storageReference.child("Levis").child("9.mp3");
            ref10 = storageReference.child("Levis").child("10.mp3");
        }else{ref1 = storageReference.child("New_User").child("1.mp3");
            ref2 = storageReference.child("New_User").child("2.mp3");
            ref3 = storageReference.child("New_User").child("3.mp3");
            ref4 = storageReference.child("New_User").child("4.mp3");
            ref5 = storageReference.child("New_User").child("5.mp3");
            ref6 = storageReference.child("New_User").child("6.mp3");
            ref7 = storageReference.child("New_User").child("7.mp3");
            ref8 = storageReference.child("New_User").child("8.mp3");
            ref9 = storageReference.child("New_User").child("9.mp3");
            ref10 = storageReference.child("New_User").child("10.mp3");}


        //logika ama thelw ola ta tragoudia tote xwris child
//        ref1 = storageReference.child("John").child("1.mp3");
//        ref2 = storageReference.child("John").child("2.mp3");
//        ref3 = storageReference.child("John").child("3.mp3");
//        ref4 = storageReference.child("John").child("4.mp3");
//        ref5 = storageReference.child("John").child("5.mp3");
//        ref6 = storageReference.child("John").child("6.mp3");
//        ref7 = storageReference.child("John").child("7.mp3");
//        ref8 = storageReference.child("John").child("8.mp3");
//        ref9 = storageReference.child("John").child("9.mp3");
//        ref10 = storageReference.child("John").child("10.mp3");


        //ref= storageReference
                //.child("Chron")
                //.child("08 - Big Bottles feat. Jelly Roll.mp3");

        ref1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, "1", ".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, "2", ".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        ref3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, "3", ".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        ref4.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, "4", ".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        ref5.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, "5", ".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        ref6.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, "6", ".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        ref7.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, "7", ".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        ref8.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, "8", ".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        ref9.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, "9", ".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        ref10.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, "10", ".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });


    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){

        DownloadManager downloadManager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);

    }



    //Telos FireBase Download-----------------------------------------------------------------------



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