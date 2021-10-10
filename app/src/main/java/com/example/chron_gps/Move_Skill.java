package com.example.chron_gps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Move_Skill extends AppCompatActivity implements SensorEventListener {

       //---------------------------- Gia na doume thn anapiria tou. Kathe fora pou pataei to play midenizei to Step Counter ---------------------------------\\




    Button btn_next;
    private ImageView imagePlayPause, Done_Check;
    private TextView textCurrentTime, textTotalDuration;
    private SeekBar playerSeekBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    //Gia ton fakelo----------------------------------------------------------------------------------------------------------------------------
    private static String FILE_NAME_SKILLS ;
    String currentTime;
    ArrayList<String> Table_Skill = new ArrayList();
    //Telos gia fakelo--------------------------------------------------------------------------------------------------------------------------

    //Gia INTENT---------------------------------------------
    String User_Name;
    public static final String Share_User_2 = "name";
    //Telos INTENT-------------------------------------------

    //Gia to FireBase-----------------------------------------
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    List<String> myList;
    List<String> TableList;
    // Telos FireBase-----------------------------------------


    //Gia to StepCounter------------------------------------------------------------------------------------------------------------------------
    private TextView textViewStepDetector;
    private SensorManager sensorManager;
    private Sensor mStepDetector;
    private boolean  isDetectorSensorPresent;
    int stepDetect = 0;
    int user_class;
    //Telos tou StepCounter---------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_skill);

        getSupportActionBar().hide();


        //Gia to xrwma tou Activity
        statusbarcolor();
        //Telos gia to xrwma tou Actiity


        btn_next = findViewById(R.id.bnt_next);
        imagePlayPause = findViewById(R.id.imagePlayPause);
        textCurrentTime =  findViewById(R.id.textCurrentTime);
        textTotalDuration = findViewById(R.id.textTotalDuration);
        playerSeekBar = findViewById(R.id.playerSeekBar);
        mediaPlayer = new MediaPlayer();
        playerSeekBar.setMax(100);

        //Gia to INTENT-----------------------------------------------------------------------------
        Intent intent = getIntent();
        String Name_Activity_1 = intent.getStringExtra(Start_Activity.Share_User);
        User_Name = Name_Activity_1;


        //Gia to Firebase---------------------------------------------------------------------------
        myList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("data-location");
        TableList = new ArrayList<>();

        //Telos FireBase----------------------------------------------------------------------------

        //Gia to StepCounter------------------------------------------------------------------------
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //textViewStepDetector = findViewById(R.id.textViewStepDetector);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) !=null) {
            mStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isDetectorSensorPresent = true;

        }
        else{
            textViewStepDetector.setText("Detector Sensor is not Present");
            isDetectorSensorPresent = false;
        }
        // Telos tou StepCounter--------------------------------------------------------------------








        btn_next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String User_Name = Name_Activity_1;
                save();


                Intent intent = new Intent(Move_Skill.this, Game_Activity.class );
                intent.putExtra(Share_User_2, User_Name);

                mediaPlayer.stop();
                uploadList(v);
                startActivity(intent);
            }
        });

        imagePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imagePlayPause.setImageResource(R.drawable.ic_play_game);
                } else {
                    mediaPlayer.start();
                    stepDetect=0;
                    imagePlayPause.setImageResource(R.drawable.ic_pause_game);
                    updateSeekBar();
                }
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





    // Gia Sinartiseis tou StepCounter-------------------------------------------------------------------------------------------------------

    @Override
    public void onSensorChanged(SensorEvent event) {
        //if (event.sensor == mStepCounter){
        //stepCount = (int) event.values[0];
        //textViewStepCounter.setText(String.valueOf(stepCount));
        //}
        //else
        if (event.sensor == mStepDetector){
            stepDetect = (int) (stepDetect + event.values[0]);
            //textViewStepDetector.setText(String.valueOf(stepDetect));
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
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) !=null)
            sensorManager.unregisterListener(this, mStepDetector);
    }

    // Telos Sinartisewn tou StepCounter ----------------------------------------------------------------------------------------------------


    //Gia ton Fakelo-------------------------------------------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save() {


        FileOutputStream fos = null;
        currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Table_Skill.clear();
        if ((stepDetect>=48) && (stepDetect<=70)){user_class = 1;}
        else{if (((stepDetect>=40) && (stepDetect<=47)) || ((stepDetect>=71) && (stepDetect<=77))){user_class=2;}else user_class=3;}
        Table_Skill.add("User Class is "+ user_class + " Number of Steps: "+ stepDetect +" Time "+currentTime+ " Entrie Date "+ LocalDate.now() );

        //Firebase---------------------------------------------------
        rootNode = FirebaseDatabase.getInstance();
        if (User_Name.equals("Chronis")||User_Name.equals("Chronis ")){reference = rootNode.getReference("Chronis_Skills");
        } else if (User_Name.equals("Xirorafas")||User_Name.equals("Xirorafas ")){reference = rootNode.getReference("Xiro_Skills");
        } else if (User_Name.equals("Sousanis")||User_Name.equals("Sousanis ")){reference = rootNode.getReference("Sousanis_Skills");
        } else if (User_Name.equals("Guru")||User_Name.equals("Guru ")){reference = rootNode.getReference("Guru_Skills");
        } else if (User_Name.equals("Moustakas")||User_Name.equals("Moustakas ")){reference = rootNode.getReference("Moustakas_Skills");
        } else if (User_Name.equals("Levis")||User_Name.equals("Levis ")){reference = rootNode.getReference("Skills_Skills");
//        } else if (User_Name.equals("Dad")||User_Name.equals("Dad ")){reference = rootNode.getReference("Dadys_Skills");
        } else if (User_Name.equals("Panagiwta")||User_Name.equals("Panagiwta ")){reference = rootNode.getReference("Panagiwtas_Skills");

        } else {reference = rootNode.getReference("New_User_Skills");}
        //reference = rootNode.getReference("Skills");
        //reference.setValue("llll");

        //Telos FireBase----------------------------------------------


        // plhrofories hmera
        try {
            //FILE_NAME_SKILLS = LocalDate.now().toString()+"Skill";
            FILE_NAME_SKILLS = "Skill";

            fos = openFileOutput(FILE_NAME_SKILLS, MODE_APPEND);
            for (int i=0; i<Table_Skill.size();i++){
                fos.write((Table_Skill.get(i)+"\n").getBytes());
            }
            //fos.write(Table_Skill.get(0).getBytes());


            //fos.write(text.getBytes());





            Toast.makeText(this, "Saved to" + getFilesDir() + "/" + FILE_NAME_SKILLS, Toast.LENGTH_LONG).show();
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
    //Telos gia fakelo-----------------------------------------------------------------------------------------------------------------------------------

    //Synartiseis gia FireBase---------------------------------------------------------------------------------------------
    public void uploadList(View w){
        //myList.add("12 AA");
        myList.add(Table_Skill.toString());

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
    //Telos Synartisewn tou FireBase---------------------------------------------------------------------------------------
  //Gia thn allagh tou xrwmatos sto activity
    private void statusbarcolor()
    {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.second_activity2,this.getTheme()));
        }else if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.second_activity2));
        }
    }
    //




}