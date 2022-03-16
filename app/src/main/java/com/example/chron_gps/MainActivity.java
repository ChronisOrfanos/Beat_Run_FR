package com.example.chron_gps;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity implements SensorEventListener , LocationListener {

    public static final String NAME = "NAME";

    public static final int DEFAULT_UPDATE_INTERVAL = 5;
    public static final int FAST_UPDATE_INTERVAL = 3;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static String FILE_ERROR ;
    private static String FILE_DATA ;

    //Gia INTENT---------------------------------------------
    private String name;
    //Telos INTENT-------------------------------------------

    //Gia ta text tou xronoy
    TextView txt_Welcome, txt_Encourage, txt_DayCount, txt_Calories, txt_MaxSpeed, txt_Average_Speed, txt_Runtime;
    int startProgress;
    int timeduration;
    DatabaseReference ref_xronou;
    //Telos text xronoy

    //Gia to progressBar circle
    ProgressBar progressBar;
    TextView text_progress;
    int p=0;
    //Telos gia progressBar circle

    //Gia to Refresh
    SwipeRefreshLayout refreshLayout;
    Handler h = new Handler();
    ImageView logo_refresh;
    //Telos gia to Refresh

    //Gia thn speedcounter
    float totalSpeed=0;
    int number_of_speed_mesurements=0;
    float meanSpeed=0;
    //Telos gia to Speedcounter

    //Gia to StepCounter
    private TextView textViewStepDetector;
    private SensorManager sensorManager;
    private Sensor mStepDetector;
    float speed;
    private boolean  isDetectorSensorPresent;
    int stepDetect = 0;
    int metavatiko_stepDetector = 0;
    // Telos StepCounter


    int start = 0;

    ArrayList<String> Run_Data = new ArrayList<String>();
    ArrayList<String> Errors_Data = new ArrayList<String>();


    EditText mEditText;

    String currentTime;


    //Gia Imerisio----------------------------------------------------------------------------------
    private ImageView imagePlayPause;
    private TextView textCurrentTime, textTotalDuration;
    private SeekBar playerSeekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    //Telos gia Imerisio----------------------------------------------------------------------------

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
    int User;
    //Telos FireBase Download-----------------------------------------------------------------------

    // references to the UI elements
    Button btn_save;
    Switch sw_locationupdates;
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
    public void save() {
        String text = mEditText.getText().toString();
        FileOutputStream fos = null;
        currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        //Table_Errors_1.clear();
        Errors_Data.add(" Error: "+ text + " Time: "+ currentTime +" Date "+LocalDate.now());

        //Firebase
        rootNode = FirebaseDatabase.getInstance();


//      if (User==1){reference =rootNode.getReference("Chronis_Runs");
//      }else {reference =rootNode.getReference("New_User_Runs");}
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference(name).child("Run");
      //uploadList();
        //

        // plhrofories hmera
//        try {
//            FILE_ERROR = LocalDate.now().toString()+"Errors.txt";
//            fos = openFileOutput(FILE_ERROR, MODE_APPEND);
//            for (int i=0; i< Errors_Data.size(); i++)
//                fos.write(((Errors_Data.get(i)+"\n").getBytes()));
//
//            mEditText.getText().clear();
//            Toast.makeText(this, "Saved to" + getFilesDir() + "/" + FILE_ERROR, Toast.LENGTH_LONG).show();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if (fos != null){
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Gia to INTENT-----------------------------------------------------------------------------
        Intent intent = getIntent();
        name = intent.getStringExtra(NAME);
        //Telos INTENT------------------------------------------------------------------------------//

        //Gia ta text tou xronoy
        txt_Welcome = findViewById(R.id.txt_Welcome);
        txt_Encourage = findViewById(R.id.txt_Encourage);
        txt_DayCount = findViewById(R.id.txt_DayCount);
        txt_Calories = findViewById(R.id.txt_Calories);
        txt_MaxSpeed = findViewById(R.id.txt_MaxSpeed);
        txt_Average_Speed = findViewById(R.id.txt_Average_Speed);
        txt_Runtime = findViewById(R.id.txt_Runtime);
        //Telos text xronoy


        //Gia to progressBar circle
        progressBar = findViewById(R.id.progress_bar);
        text_progress = findViewById(R.id.text_progress_bar);

        if(startProgress==1){
            final Handler hand = new Handler();
            hand.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (p<=100){
                        text_progress.setText(p+"%");
                        progressBar.setProgress(p);
                        p++;
                        hand.postDelayed(this,200);
                    }
                    else hand.removeCallbacks(this);

                }
            },200);
        }
        //Telos gia progressBar circle

        //Navigator Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigator);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference(name).child("Run");
                switch (menuItem.getItemId()){
                    case R.id.nav_music:{
                        openMusic_List();}
                }
                switch (menuItem.getItemId()){
                    case R.id.nav_close:
                        onDestroy();
                        finish();
                        Intent intent_arx = new Intent(MainActivity.this, Start_Activity.class );
                }
                switch (menuItem.getItemId()){
                    case R.id.nav_upload:{
//                        if (User==1){reference =rootNode.getReference("Chronis_Runs");
//                        }else {reference =rootNode.getReference("New_User_Errors");}
//                        rootNode = FirebaseDatabase.getInstance();
//                        reference = rootNode.getReference(name).child("Run");
                        uploadList();}
                }
                return false;
            }
        });
        //Telos Navigator

        //Gia to Refresh
        logo_refresh =  findViewById(R.id.logo_refresh);
        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                logo_refresh.setVisibility(View.VISIBLE);
                startAnimation(logo_refresh);
                prepareMediaPlayer();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        logo_refresh.setVisibility(View.INVISIBLE);
                    }
                },3000);
            }
        });
        //Telos gia to Refresh

        //Gia to ActionBar
        getSupportActionBar().hide();
        //Telos Action Bar

        //Gia to StepCounter--------------------------------------------------------------------------------------------------------------------------------------
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) !=null) {
            mStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }
        else{
            textViewStepDetector.setText("Detector Sensor is not Present");
            isDetectorSensorPresent = false;
        }
        metavatiko_stepDetector = stepDetect;
        // Telos tou StepCounter----------------------------------------------------------------------------------------------------------------------------------

        //Gia to xrwma tou Activity
        statusbarcolor();
        //Telos gia to xrwma tou Actiity

        //Gia Backround-----------------------------------------------------------------------------
        Intent intentBack = new Intent(this,MyService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForegroundService(intentBack);
        }
        else
            {
                startService(intentBack);
            }
        //Telos gia Backround-----------------------------------------------------------------------

        //Gia ta Errors-----------------------------------------------------------------------------
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String text = mEditText.getText().toString();
                FileOutputStream fos = null;
                currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                //Table_Errors_1.clear();
                Errors_Data.add(" Error: "+ text + " Time: "+ currentTime +" Date "+LocalDate.now());

                //Firebase
                rootNode = FirebaseDatabase.getInstance();

                Intent intent = new Intent(MainActivity.this, MainActivity.class );
                intent.putExtra(MainActivity.NAME,name);

                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference(name).child("Errors");
//                if (User==1){reference =rootNode.getReference("Chronis_Errors");
//                }else {reference =rootNode.getReference("New_User_Errors");}
                uploadErrorList(v);
                //

                // plhrofories hmera
//                try {
//                    FILE_ERROR = LocalDate.now().toString()+"Errors.txt";
//                    fos = openFileOutput(FILE_ERROR, MODE_APPEND);
//                    for (int i=0; i< Errors_Data.size(); i++)
//                        fos.write(((Errors_Data.get(i)+"\n").getBytes()));
//
//                    mEditText.getText().clear();
//                    Toast.makeText(MainActivity.this, "Saved to" +getFilesDir() + "/" + FILE_ERROR, Toast.LENGTH_SHORT).show();
////                    Toast.makeText(this, "Saved to" + getFilesDir() + "/" + FILE_ERROR, Toast.LENGTH_LONG).show();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }finally {
//                    if (fos != null){
//                        try {
//                            fos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
            }
        });
        //Telos gia ta Errors-----------------------------------------------------------------------

        //Gia Imerisio
        imagePlayPause = findViewById(R.id.imagePlayPause);
        textCurrentTime =  findViewById(R.id.textCurrentTime);
        textTotalDuration = findViewById(R.id.textTotalDuration);
        playerSeekBar = findViewById(R.id.playerSeekBar);
        mediaPlayer = new MediaPlayer();
        playerSeekBar.setMax(100);
        //Telos gia Imerisio

        //Gia thn koumpara---------------------------------------

        FloatingActionButton fab1 = findViewById(R.id.fab_action1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showToast("Τρέχα μωρή χοντρή");
            //userName = "Chronis";
                User=1;
                ref_xronou = FirebaseDatabase.getInstance().getReference().child("Chronis");
                details();
            }
        });
        FloatingActionButton fab8 = findViewById(R.id.fab_action8);
        fab8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { showToast("Καλως ήρθες νέος");
                User=8;
                ref_xronou = FirebaseDatabase.getInstance().getReference().child("New User");
                details();
            }
        });
        // Telos koumparas---------------------------------------


        //Gia to Firebase
        myList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("data-location");
        TableList = new ArrayList<>();
        //

        //gia to grapsimo sthn othonh
        mEditText = findViewById(R.id.edit_text);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
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

        //Gia Imerisio------------------------------------------------------------------------------
        imagePlayPause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imagePlayPause.setImageResource(R.drawable.ic_play_game);
                } else {
                    mediaPlayer.start();
                    imagePlayPause.setImageResource(R.drawable.ic_pause_game);
                    updateSeekBar();
                    totalSpeed = 0;
                    number_of_speed_mesurements = 0;
                    //save(v);
                }
                start=start+1;

                //Gia to ProgressCircle
                if(startProgress==1){
                    final Handler hand = new Handler();
                    hand.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (p<=timeduration){
                                text_progress.setText(p+"%");
                                progressBar.setProgress(p);
                                p++;
                                hand.postDelayed(this,timeduration*100);
                            }
                            else hand.removeCallbacks(this);
                        }
                    },timeduration*100);
                }
                //Telso gia ProgressCircle

            }
        });

        prepareMediaPlayer();

        // An to vgalw ayto tote o allos den mporei na proxwraei to kommati klp sto seekBar kai elitoyrgei kanonika o ypoloipo
        playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                SeekBar seekBar = (SeekBar) view;
                int playsPosition =  (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                mediaPlayer.seekTo(playsPosition);
                textCurrentTime.setText(milliSecondToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });
        //Telos gia Imerisio------------------------------------------------------------------------

        //Gia to Speedtometer to kainourio------------------------------------------------------------------------------------------------------------------

        // Get GPS permisions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            //Start the program if permission is grante
            doStuff();
        }

        this.updateSpeed(null);
        //Telos toy kainouriou Speedtometer-----------------------------------------------------------------------------------------------------------------

    }// end onCreate method++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //Gia thn koumpara---------------------------------------
    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    // Telos koumparas---------------------------------------


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
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {
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
                doStuff();
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
        String SpeedString = String.valueOf(location.getSpeed());
        FileOutputStream foss = null;
        currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        stepDetect = metavatiko_stepDetector;
        speed = (float) (location.getSpeed()*3.6);


        //Run_Data.clear();
        if (!(start % 2 == 0)){Run_Data.add("He push the Play Button------------------------------------------------"+" Time: "+ currentTime +" Date "+LocalDate.now());start=start+1;}

        Run_Data.add("User: "+User+ " " + "Num of steps: " + stepDetect +" " + "Lat:" + Latitude + " " + "Long:" + Longtitude + " " + "Alt:" + Altitude + " " + "Acc:" + Accuracy + " " + "Speed:" + speed+ " Time: "+ currentTime +" Date "+LocalDate.now());
//        try {
//            FILE_DATA = LocalDate.now().toString()+"Data.txt";
//            foss = openFileOutput(FILE_DATA, MODE_APPEND);
//
//                      for (int i=0; i< Run_Data.size(); i++)
//                      foss.write(((Run_Data.get(i)+"\n").getBytes()));
//            //Toast.makeText(this, "Saved to" + getFilesDir() + "/" + FILE_DATA, Toast.LENGTH_LONG).show();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if (foss != null){
//                try {
//                    foss.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    //Gia thn mousikh
    public void openMusic_List(){
        Intent intent = new Intent(this, Music_List.class);
        startActivity(intent);
    }

    public void uploadList(){
        //myList.add("New Button Data");
        myList.add(Run_Data.toString());

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference(name).child("Run");
//        if (User==1||User==2||User==3||User==4||User==5||User==6||User==7||User==8){
//            reference.setValue(myList)
//            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()){
//                        Toast.makeText(getApplicationContext(),"Data are uploaded successfully, Dont upload again for today", Toast.LENGTH_LONG).show();
//                    }
//                }
//        });
//        }else showToast("You have to choose User");
    }

    public void uploadErrorList(View v){
        myList.add(Errors_Data.toString());
        reference.setValue(myList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"List is uploaded successfully, Don't upload again for today", Toast.LENGTH_LONG).show();
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError dataseterror) {
            }
        });
    }

    private void  prepareMediaPlayer(){
        try {
            mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/music-97497.appspot.com/o/Skill.mp3?alt=media&token=f04ed603-a5a5-4d97-923c-3e66263a2367");
            mediaPlayer.prepare();
            textTotalDuration.setText(milliSecondToTimer(mediaPlayer.getDuration()));

        } catch (Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currenDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondToTimer(currenDuration));
        }
    };

    private void  updateSeekBar(){
        if (mediaPlayer.isPlaying()) {
            playerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    private String milliSecondToTimer(long milliSeconds){
        String timerString = "";
        String secondString;
        int hours = (int) (milliSeconds / (1000 *60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hours > 0) {
            timerString = hours + ":";
        }
        if (seconds < 10){
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;
        }
        timerString = timerString + minutes + ":" + secondString;
        return timerString;
    }
    //Telso gia Imerisio----------------------------------------------------------------------------

      //Gia thn allagh tou xrwmatos sto activity
    private void statusbarcolor()
    {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.second_activityre,this.getTheme()));
        }else if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.second_activityre));
        }
    }
    //Gia to StepCounter Sinarthseis----------------------------------------------------------------

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mStepDetector){
            stepDetect = (int) (stepDetect + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) !=null)
            sensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
            sensorManager.unregisterListener(this, mStepDetector);
    }
    //Telos Sinarthsewn gia to StepCounter----------------------------------------------------------

    //Gia to Speedtometer to kainourio-------------------------------------------------------------------------------------------------------------
    @Override
    public void onLocationChanged(@NonNull Location locatioN) {
        if (locatioN != null) {
            CLocation myLocation = new CLocation(locatioN);
            this.updateSpeed(myLocation);
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }
    private void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "Waiting for GPS connection!", Toast.LENGTH_SHORT).show();

    }

    private void updateSpeed(CLocation locatioN){
        float nCurrentSpeed = 0;
        if (locatioN!= null){
            nCurrentSpeed = locatioN.getSpeed();
        }
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US,"%5.1f",nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace("","0");
    }

    //Gia logo turning
    public void startAnimation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(logo_refresh, "rotation", 0f, 360f);
        animator.setDuration(3000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    //Telos gia logo turning

    //Sinartish gia na pairnei ta dedomena taxythtas, thermidwn klp apo ton kathena
    public void details()
    {
        ref_xronou.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String welcome = dataSnapshot.child("Welcome_User").getValue().toString();
                String encourage = dataSnapshot.child("Encourage_message").getValue().toString();
                String daycount = dataSnapshot.child("Day_Count").getValue().toString();
                String calories = dataSnapshot.child("Calories").getValue().toString();
                String maxspeed = dataSnapshot.child("Max_Speed").getValue().toString();
                String averagespeed = dataSnapshot.child("Average speed").getValue().toString();
                String runtime = dataSnapshot.child("Runtime").getValue().toString();
                String timedurationstring = dataSnapshot.child("Song_time").getValue().toString();
                txt_Welcome.setText(welcome);
                txt_Encourage.setText(encourage);
                txt_DayCount.setText(daycount);
                txt_Calories.setText(calories);
                txt_MaxSpeed.setText(maxspeed);
                txt_Average_Speed.setText(averagespeed);
                txt_Runtime.setText(runtime);
                startProgress=1;
                timeduration = Integer.parseInt(timedurationstring);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError dataError) {
            }
        });
    }
    //Telos tou kainouriou Speedometer-------------------------------------------------------------------------------------------------------------
}