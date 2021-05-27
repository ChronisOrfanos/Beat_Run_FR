package com.example.chron_gps;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Music_List extends AppCompatActivity {
    ListView listView;
    ArrayList <String> items;



    // Gia thn prospatheia na anoigei ton fakelo

    private static String FILE_NAME2;
    String currentTime;
    int Song_number =0;
    ArrayList<String> Table_File = new ArrayList<String>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void save() {

        FileOutputStream fos = null;
        Song_number += 1;
        currentTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        Table_File.add("Song no:"+Song_number+" -------------------------------------------------------" + " Time: "+ currentTime);




        // plhrofories hmera
        try {
            FILE_NAME2 = LocalDate.now().toString()+"Data.txt";

            //FILE_NAME2 = LocalDate.now().toString()+"Errors.txt";
            fos = openFileOutput(FILE_NAME2, MODE_APPEND);
            for (int i=0; i< Table_File.size(); i++)
                fos.write(((Table_File.get(i)+"\n").getBytes()));








            Toast.makeText(this, "Saved to" + getFilesDir() + "/" + FILE_NAME2, Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.activity_music_list);

        listView = findViewById(R.id.listViewSong);

        runtimePermission();
    }

    public void runtimePermission()
    {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();

                    }
                }).check();

    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();

        for (File singlefile : files) {
            if (singlefile.isDirectory() && !singlefile.isHidden()) {
                arrayList.addAll(findSong(singlefile));
            } else
            {
                if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")) {
                    arrayList.add(singlefile);
                }
            }
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    void displaySongs()
    {
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());


        items = new ArrayList<String>();

        for (int i = 0; i<mySongs.size();i++)
        {
            items.add(mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", ""));
            Collections.sort(items);
        }

        /*ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(myAdapter);*/

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                String songName = (String) listView.getItemAtPosition(i);
                //String songName = (String) items.get(i);
                save();



                startActivity(new Intent(getApplicationContext(), Music_Player.class)
                        .putExtra("songs", mySongs)
                        .putExtra("songname", songName)
                        .putExtra("pos",i));



            }
            //save(v);



        });
    }

    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View View, ViewGroup viewGroup) {
            View myView =  getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items.get(i));
            //textsong.setText(listView.get(i));

            return myView;
        }
    }
}