package com.example.chron_gps;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Start_Activity extends AppCompatActivity {
    EditText email,password, name,age, weigh,usual_week_runs, tests ;
    CheckBox remember;
    Button login;
    private static String FILE_NAME_INFOS ;
    String currentTime;
    ArrayList<String> Infos = new ArrayList<String>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save(View v) {

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        weigh = (EditText) findViewById(R.id.weigh);
        usual_week_runs = (EditText) findViewById(R.id.w_runs);
        tests = (EditText) findViewById(R.id.tests);

        String Mail = email.getText().toString();
        String Password = password.getText().toString();
        String Name = name.getText().toString();
        String Age = age.getText().toString();
        String Weigh = weigh.getText().toString();
        String U_w_runs = usual_week_runs.getText().toString();
        String Tests = tests.getText().toString();

        FileOutputStream fos = null;

        currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Infos.add("Name:"+ Name +" Age:"+ Age +" Weigh:"+ Weigh + " Usual week runs:"+ U_w_runs + " Tests:"+ Tests +" Email:"+ Mail + " Password:"+ Password +" Entrie Date"+currentTime);


        // plhrofories hmera
        try {
            FILE_NAME_INFOS = LocalDate.now().toString()+"Infos";
            fos = openFileOutput(FILE_NAME_INFOS, MODE_APPEND);
            for (int i=0; i< Infos.size(); i++)
                fos.write(((Infos.get(i)+"\n").getBytes()));

            //fos.write(text.getBytes());


            email.getText().clear();
            password.getText().clear();
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

        remember = findViewById(R.id.rememberMe);
        login = findViewById(R.id.loginBtn);




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
                Intent intent = new Intent(Start_Activity.this,MainActivity.class);
                startActivity(intent);
                save(v);
            }
        });

        remember.setOnCheckedChangeListener((compoundButton, b) -> {

            if (compoundButton.isChecked()){
                SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                editor.putString("remember","true");
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






}