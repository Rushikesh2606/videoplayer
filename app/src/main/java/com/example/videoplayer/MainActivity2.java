package com.example.videoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity2 extends AppCompatActivity {
VideoView video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent=getIntent();
        String path=intent.getStringExtra("videoPath");
       video=findViewById(R.id.video);
        MediaController md=new MediaController(MainActivity2.this);
        md.setAnchorView(video);
        video.setVideoPath(path);
        video.setMediaController(md);
        video.start();
        video.pause();
    }
}