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
import java.util.List;

public class SimplePlayerActivity extends AppCompatActivity {

    TextView textViewStart, textViewEnd, textViewTitle, textViewArtist;
    ImageView imageView, imageViewPlay, imageViewPrevious, imageViewNext;
    SeekBar seekBar, seekBarVolume;
    //SongModel songModel;
    List<SongModel> songModelList;
    int position = 0;
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
        imageViewNext.setOnClickListener(view -> {
            position += 1;
            int maxLength = songModelList.size();
            if (position > maxLength - 1){
                position = 0;
            }
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
        });

        imageViewPrevious.setOnClickListener(view -> {
            position -= 1;
            int maxLength = songModelList.size();
            if (position < 0){
                position = maxLength - 1;
            }
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
        });
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
        imageViewNext = findViewById(R.id.imageViewNext);
        imageViewPrevious = findViewById(R.id.imageViewPrevious);
        textViewArtist = findViewById(R.id.textViewArtistPlayer);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        Intent intent = getIntent();
//        songModel = (SongModel) intent.getSerializableExtra(Variables.SONG_MODEL_OBJECT);
//        Glide.with(getApplicationContext()).load(songModel.getImg_url()).into(imageView);
//        textViewTitle.setText(songModel.getTitle());
//        int artistListLength = songModel.getArtist().size();
//        String artistText = "";
//        for (int i=0; i<artistListLength; i++){
//            if (i == artistListLength - 1){
//                artistText += songModel.getArtist().get(i).getName();
//            }
//            else {
//                artistText += songModel.getArtist().get(i).getName() + ", ";
//            }
//        }
//        textViewArtist.setText(artistText);

        songModelList = (List<SongModel>) intent.getSerializableExtra(Variables.LIST_SONG_MODEL_OBJECT);
        position = intent.getIntExtra(Variables.POSITION, 0);
    }

    private void CreateMediaPlayer() throws IOException {
        Glide.with(getApplicationContext()).load(songModelList.get(position).getImg_url()).into(imageView);
        textViewTitle.setText(songModelList.get(position).getTitle());
        int artistListLength = songModelList.get(position).getArtist().size();
        String artistText = "";
        for (int i=0; i<artistListLength; i++){
            if (i == artistListLength - 1){
                artistText += songModelList.get(position).getArtist().get(i).getName();
            }
            else {
                artistText += songModelList.get(position).getArtist().get(i).getName() + ", ";
            }
        }
        textViewArtist.setText(artistText);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(songModelList.get(position).getUrl());
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }
}