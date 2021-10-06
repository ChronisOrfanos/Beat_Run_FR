package com.example.chron_gps;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

public class Splush extends AppCompatActivity {

    Handler h = new Handler();
    ImageView imageView;
    VideoView videostart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splush);

        //Gia to xrwma tou Activity
        statusbarcolor();
        //Telos gia to xrwma tou Actiity

        imageView = findViewById(R.id.imageview);

//        videostart = (VideoView) findViewById(R.id.video);
//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(videostart);
//        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/hope-44da4.appspot.com/o/treleee.mp4?alt=media&token=16fd66e6-3b34-4fe9-ba51-72b83cfe4862");
//        videostart.setVideoURI(uri);
//        videostart.start();








        getSupportActionBar().hide();

        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent_arxikh = new Intent(Splush.this, Start_Activity.class);
                startActivity(intent_arxikh);
                finish();



            }
        }, 3000);

        startAnimation(imageView);
//
//        final Handler handler = new Handler();
//        final  int delay = 3000;
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent_arxikh = new Intent(Splush.this, Start_Activity.class );
//
//                handler.postDelayed(this, delay);
//
//            }
//
//        }, delay);
//    }

    }

    public void startAnimation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        animator.setDuration(3000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();

    }

    //Gia thn allagh tou xrwmatos sto activity
    private void statusbarcolor()
    {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.welcome_activity1,this.getTheme()));
        }else if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.welcome_activity1));
        }
    }
}