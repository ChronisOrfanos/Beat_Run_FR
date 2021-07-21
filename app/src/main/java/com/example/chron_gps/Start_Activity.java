package com.example.chron_gps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
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

public class Start_Activity extends AppCompatActivity {
    EditText email,phone, name,age, weigh,usual_week_runs, tests ;
    CheckBox remember;
    Button login;
    private static String FILE_NAME_INFOS ;
    String currentTime;
    ArrayList<String> Infos = new ArrayList<String>();

    //Gia INTENT
    public static final String Share_User = "name";
    //public static final String Share_User_2 = "name";
    //


    //Gia to FireBase-----------------------------------------
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    List<String> myList;
    List<String> TableList;
    // Telos FireBase-----------------------------------------
    private Button button;



    //




    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save(View v) {

        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        weigh = (EditText) findViewById(R.id.weigh);
        usual_week_runs = (EditText) findViewById(R.id.w_runs);
        tests = (EditText) findViewById(R.id.tests);

        String Mail = email.getText().toString();
        String Phone = phone.getText().toString();
        String Name = name.getText().toString();
        String Age = age.getText().toString();
        String Weigh = weigh.getText().toString();
        String U_w_runs = usual_week_runs.getText().toString();
        String Tests = tests.getText().toString();

        //Firebase---------------------------------------------------
        rootNode = FirebaseDatabase.getInstance();
        if (Name.equals("Chronis")||Name.equals("Chronis ")){reference = rootNode.getReference("Chronis_Infos");
        } else if (Name.equals("Xirorafas")||Name.equals("Xirorafas ")){reference = rootNode.getReference("Xiro_Infos");
        } else if (Name.equals("Sousanis")||Name.equals("Sousanis ")){reference = rootNode.getReference("Sousanis_Infos");
        } else if (Name.equals("Guru")||Name.equals("Guru ")){reference = rootNode.getReference("Guru_Infos");
        } else if (Name.equals("Moustakas")||Name.equals("Moustakas ")){reference = rootNode.getReference("Moustakas_Infos");
        } else if (Name.equals("Levis")||Name.equals("Levis ")){reference = rootNode.getReference("Levis_Infos");
        } else if (Name.equals("Dad")||Name.equals("Dad ")){reference = rootNode.getReference("Dadys_Infos");
        } else {reference = rootNode.getReference("New_User_Infos");}




            //else {
            //reference = rootNode.getReference("Infos");
        //} else if(Name.equals("Xirorafas")){
          //  reference = rootNode.getReference("Xiro_Infos");
        //}
        //reference = rootNode.getReference("Infos");
        //reference.setValue("llll");
        //Telos FireBase----------------------------------------------


        FileOutputStream fos = null;

        currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Infos.clear();
        Infos.add("Name: "+ Name +" Age: "+ Age +" Weigh: "+ Weigh + " Usual week runs: "+ U_w_runs + " Tests: "+ Tests +" Email: "+ Mail + " Phone: "+ Phone +" Time "+currentTime+ " Entrie Date "+ LocalDate.now() );


        // plhrofories hmera
        try {
//            FILE_NAME_INFOS = LocalDate.now().toString()+"Infos";
            FILE_NAME_INFOS = "Infos";

            fos = openFileOutput(FILE_NAME_INFOS, MODE_APPEND);
            for (int i=0; i< Infos.size(); i++)
                fos.write(((Infos.get(i)+"\n").getBytes()));

            //fos.write(text.getBytes());


            email.getText().clear();
            phone.getText().clear();
            name.getText().clear();
            age.getText().clear();
            weigh.getText().clear();
            usual_week_runs.getText().clear();
            tests.getText().clear();

            Toast.makeText(this, "Saved to" + getFilesDir() + "/" + FILE_NAME_INFOS, Toast.LENGTH_LONG).show();
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



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        //Gia to xrwma tou Activity
        statusbarcolor();
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"yellow\">" + getString(R.string.app_name) + "</font>"));
        //Telos xrwmatos Activity


        remember = findViewById(R.id.rememberMe);
        login = findViewById(R.id.loginBtn);


        //Gia to Firebase---------------------------------------------------------------------------
        myList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("data-location");
        TableList = new ArrayList<>();

        name = (EditText) findViewById(R.id.name);
        String Name = name.getText().toString();

        //Telos FireBase----------------------------------------------------------------------------





        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        if (checkbox.equals("true")){
            Intent intent = new Intent(Start_Activity.this, MainActivity.class);
            startActivity(intent);
        }else if (checkbox.equals("false")){
            Toast.makeText(this, "Please Sing In.",Toast.LENGTH_SHORT).show();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {


                //name = (EditText) findViewById(R.id.name);
                String User_Name = name.getText().toString();

                Intent intent = new Intent(Start_Activity.this,Move_Skill.class);
                intent.putExtra(Share_User, User_Name);

                startActivity(intent);
                save(v);
                uploadList(v);

                //String username = name.getText().toString();
                //intent.putExtra("keyname", Name);


                //Gia Firebase






                //
            }
        });

        remember.setOnCheckedChangeListener((compoundButton, b) -> {

            if (compoundButton.isChecked()){
                SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                editor.putString("remember","true");

                //String User_Name_Tel = name.getText().toString();
                //Intent intent_2 = new Intent(Start_Activity.this,MainActivity.class);
                //intent_2.putExtra(Share_User_2, User_Name_Tel);

                editor.apply();
                Toast.makeText(Start_Activity.this, "checked", Toast.LENGTH_SHORT).show();

            }else if (!compoundButton.isChecked()){
                SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                editor.putString("remember","false");
                editor.apply();
                Toast.makeText(Start_Activity.this, "Unchecked", Toast.LENGTH_SHORT).show();
            }

        });

        



    }


    //Synartiseis gia FireBase---------------------------------------------------------------------------------------------
    public void uploadList(View v){
        //myList.add("12 AA");
        myList.add(Infos.toString());

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
            getWindow().setStatusBarColor(getResources().getColor(R.color.teal_200,this.getTheme()));
        }else if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.teal_200));
        }
    }
    //





}