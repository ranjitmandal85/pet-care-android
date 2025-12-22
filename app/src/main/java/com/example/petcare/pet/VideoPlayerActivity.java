package com.example.petcare.pet;

import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;

public class VideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 TRUE FULLSCREEN
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_video_player);

        VideoView videoView = findViewById(R.id.fullscreenVideoView);

        String uriString = getIntent().getStringExtra("video_uri");
        if (uriString == null) {
            finish();
            return;
        }

        Uri videoUri = Uri.parse(uriString);

        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);

        videoView.setMediaController(controller);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }
}
