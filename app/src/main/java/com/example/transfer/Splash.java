package com.example.transfer;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class Splash extends Activity
{
    VideoView vidHolder;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            vidHolder = new VideoView(this);
            setContentView(vidHolder);
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
            vidHolder.setVideoURI(video);
            vidHolder.setZOrderOnTop(true);
            vidHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                public void onCompletion(MediaPlayer mp) {
                    jump();
                }});
            vidHolder.start();

        } catch(Exception ex) {
            jump();
        }
    }

    private void jump()
    {
        if(isFinishing())
            return;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}