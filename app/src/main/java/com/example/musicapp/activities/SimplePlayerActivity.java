package com.example.musicapp.activities;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.models.SongModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class SimplePlayerActivity extends AppCompatActivity {

    TextView textViewStart, textViewEnd, textViewTitle, textViewArtist;
    ImageView imageView, imageViewPlay, imageViewPrevious, imageViewNext, imageViewDownload;
    SeekBar seekBar, seekBarVolume;
    List<SongModel> songModelList;
    int songPosition = 0;
    MediaPlayer mediaPlayer;
    AudioManager audioManager = null;

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
        imageViewPlay.setImageResource(R.drawable.icons8_play_64);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_player);
        //getSupportActionBar().hide();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        ViewBinding();
        try {
            CreateMediaPlayer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SetTime();
        UpdateProgress();
        Listener();
    }

    private void Listener() {

        imageViewDownload.setOnClickListener(view -> {
            DowloadMusicFile();
        });

        imageViewNext.setOnClickListener(view -> {
            songPosition += 1;
            int maxLength = songModelList.size();
            if (songPosition > maxLength - 1){
                songPosition = 0;
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
            songPosition -= 1;
            int maxLength = songModelList.size();
            if (songPosition < 0){
                songPosition = maxLength - 1;
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

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer = new MediaPlayer();
            songPosition += 1;
            int maxLength = songModelList.size();
            if (songPosition > maxLength - 1){
                songPosition = 0;
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
    }

    private void DownloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }

    private void DowloadMusicFile() {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();;
        StorageReference reference;

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Đang chuẩn bị tải về!");
        pd.setIndeterminate(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();

        String songTitle = songModelList.get(songPosition).getTitle();

        reference = storageReference.child( "Songs/"+ songTitle + ".mp3");

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                DownloadFile(SimplePlayerActivity.this, songTitle, "mp3", DIRECTORY_DOWNLOADS, url);
                Toast.makeText(SimplePlayerActivity.this, "Đang tải về", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SimplePlayerActivity.this, "Không tải được", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        });
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
        textViewArtist = findViewById(R.id.textViewArtistPlayer);

        seekBar = findViewById(R.id.seekBar);
        seekBarVolume = findViewById(R.id.seekBarVolume);

        imageViewPlay = findViewById(R.id.imageViewPlay);
        imageView = findViewById(R.id.imageViewPlayer);
        imageViewNext = findViewById(R.id.imageViewNext);
        imageViewPrevious = findViewById(R.id.imageViewPrevious);
        imageViewDownload = findViewById(R.id.imageViewDownload);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        Intent intent = getIntent();
        songModelList = (List<SongModel>) intent.getSerializableExtra(Variables.LIST_SONG_MODEL_OBJECT);
        songPosition = intent.getIntExtra(Variables.POSITION, 0);

    }

    private void CreateMediaPlayer() throws IOException {

        Glide.with(getApplicationContext()).load(songModelList.get(songPosition).getImg_url()).into(imageView);
        textViewTitle.setText(songModelList.get(songPosition).getTitle());
        int artistListLength = songModelList.get(songPosition).getArtist().size();
        String artistText = "";
        for (int i=0; i<artistListLength; i++){
            if (i == artistListLength - 1){
                artistText += songModelList.get(songPosition).getArtist().get(i);
            }
            else {
                artistText += songModelList.get(songPosition).getArtist().get(i) + ", ";
            }
        }
        textViewArtist.setText(artistText);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(songModelList.get(songPosition).getUrl());
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        imageViewPlay.setImageResource(R.drawable.icons8_pause_64);
    }
}