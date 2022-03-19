package com.example.musicapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.models.SongModel;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class SimplePlayerActivity extends AppCompatActivity {

    TextView textViewStart, textViewEnd, textViewTitle, textViewArtist;
    ImageView imageView;
    SeekBar seekBar, seekBarVolume;
    ImageView imageViewPlay;
    SongModel songModel;
    MediaPlayer mediaPlayer;
    AudioManager audioManager = null;

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_player);
        getSupportActionBar().hide();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        ViewBinding();
        SetTime();
        try {
            CreateMediaPlayer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Listener();

    }

    private void Listener() {
        imageViewPlay.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                imageViewPlay.setImageResource(R.drawable.icons8_play_64);
            }
            else {
                mediaPlayer.start();
                imageViewPlay.setImageResource(R.drawable.icons8_pause_64);
            }
            SetTime();
            UpdateProgress();
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                UpdateProgress();
            }
        });
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            seekBarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            seekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UpdateProgress(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentPosition = mediaPlayer.getCurrentPosition();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                textViewStart.setText(simpleDateFormat.format(currentPosition));
                seekBar.setProgress(currentPosition);
                if (currentPosition == mediaPlayer.getDuration()){
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        try {
                            CreateMediaPlayer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                        imageViewPlay.setImageResource(R.drawable.icons8_pause_64);
                    }
                    else {
                        try {
                            CreateMediaPlayer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    SetTime();
                    UpdateProgress();
                }
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    private void SetTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        textViewEnd.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void ViewBinding(){
        textViewStart = findViewById(R.id.textViewStart);
        textViewEnd = findViewById(R.id.textViewEnd);
        textViewTitle = findViewById(R.id.textViewTitlePlayer);
        seekBar = findViewById(R.id.seekBar);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        imageViewPlay = findViewById(R.id.imageViewPlay);
        imageView = findViewById(R.id.imageViewPlayer);
        textViewArtist = findViewById(R.id.textViewArtistPlayer);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.start();
            imageViewPlay.setImageResource(R.drawable.icons8_pause_64);
            SetTime();
            UpdateProgress();
        });

        Intent intent = getIntent();
        songModel = (SongModel) intent.getSerializableExtra(Variables.SONG_MODEL_OBJECT);
        Glide.with(getApplicationContext()).load(songModel.getImg_url()).into(imageView);
        textViewTitle.setText(songModel.getTitle());
        int artistListLength = songModel.getArtistModelList().size();
        String artistText = "";
        for (int i=0; i<artistListLength; i++){
            if (i == artistListLength - 1){
                artistText += songModel.getArtistModelList().get(i).getName();
            }
            else {
                artistText += songModel.getArtistModelList().get(i).getName() + ", ";
            }
        }
        textViewArtist.setText(artistText);
    }

    private void CreateMediaPlayer() throws IOException {
        mediaPlayer.setDataSource(songModel.getUrl());
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}