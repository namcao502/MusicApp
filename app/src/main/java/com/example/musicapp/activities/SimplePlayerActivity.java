package com.example.musicapp.activities;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.adapters.AddToPlaylistDialogAdapter;
import com.example.musicapp.adapters.CommentAdapter;
import com.example.musicapp.models.ArtistModel;
import com.example.musicapp.models.CommentModel;
import com.example.musicapp.models.PlaylistModel;
import com.example.musicapp.models.SongModel;
import com.example.musicapp.services.CreateNotification;
import com.example.musicapp.services.OnClearFromRecentService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SimplePlayerActivity extends AppCompatActivity {

    TextView textViewStart, textViewEnd, textViewTitle, textViewArtist;
    ImageView imageViewPlay, imageViewPrevious, imageViewNext, imageViewDownload, imageViewAddToPlaylist, imageViewShuffleAndLoop;
    SeekBar seekBar, seekBarVolume;
    RecyclerView recyclerViewComment;
    EditText editTextComment;
    Button buttonAddComment;
    CircleImageView circleImageView;
    ListView listViewListSong;

    List<SongModel> songModelList;
    int songPosition = 0;
    MediaPlayer mediaPlayer;
    AudioManager audioManager = null;

    FirebaseAuth auth;
    FirebaseFirestore db;

    List<CommentModel> commentModelList;
    CommentAdapter commentAdapter;

    NotificationManager notificationManager;

    String playState = "Go";

    ObjectAnimator rotatingImageAnimation;

    final String[] artistText = {""};


    @SuppressLint("NotifyDataSetChanged")
    private void Listener() {

        listViewListSong.setOnItemClickListener((adapterView, view, i, l) -> {
            songPosition = i;
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            try {
                CreateMediaPlayer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        imageViewShuffleAndLoop.setOnClickListener(view -> {
            if (playState.equals("Loop")) {
                playState = "Shuffle";
                imageViewShuffleAndLoop.setImageResource(R.drawable.icons8_shuffle_64);
            }
            else {
                if (playState.equals("Shuffle")){
                    playState = "Go";
                    imageViewShuffleAndLoop.setImageResource(R.drawable.icons8_arrow_64);
                }
                else {
                    if (playState.equals("Go")){
                        playState = "Loop";
                        imageViewShuffleAndLoop.setImageResource(R.drawable.icons8_repeat_64);
                    }
                }
            }
        });

        buttonAddComment.setOnClickListener(view -> {
            String detail = editTextComment.getText().toString();
            if (detail.isEmpty()){
                Toast.makeText(SimplePlayerActivity.this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                CommentModel commentModel = new CommentModel(auth.getUid(), songModelList.get(songPosition).getId(), detail);
                db.collection("Comment").document(Objects.requireNonNull(auth.getUid())).collection("User").document().set(commentModel)
                        .addOnCompleteListener(task -> {
                            Toast.makeText(SimplePlayerActivity.this, "Thêm bình luận thành công", Toast.LENGTH_SHORT).show();

                            commentModelList.clear();
                            editTextComment.clearFocus();

                            db.collection("Comment").document(auth.getUid()).collection("User")
                                    .whereEqualTo("song_id", songModelList.get(songPosition).getId())
                                    .get().addOnCompleteListener(task2 -> {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot doc : task2.getResult()){
                                        CommentModel commentModel1 = doc.toObject(CommentModel.class);
                                        commentModelList.add(commentModel1);
                                    }
                                    commentAdapter.notifyDataSetChanged();
                                }
                            });

                            editTextComment.setText("");
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(SimplePlayerActivity.this, "Thêm bình luận thất bại", Toast.LENGTH_SHORT).show());
            }
        });

        imageViewAddToPlaylist.setOnClickListener(view -> {

            Dialog dialog = new Dialog(SimplePlayerActivity.this);
            dialog.setContentView(R.layout.activity_simple_player_add_to_playlist_dialog);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            dialog.getWindow().setLayout((6 * width)/7, (4 * height)/5);
            dialog.show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();

//            Button buttonAddToPlaylistDialogAddAll = dialog.findViewById(R.id.buttonAddToPlaylistDialogAddAll);
//            buttonAddToPlaylistDialogAddAll.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });

            List<PlaylistModel> playlistModelList = new ArrayList<>();
            AddToPlaylistDialogAdapter addToPlaylistDialogAdapter = new AddToPlaylistDialogAdapter(SimplePlayerActivity.this, playlistModelList, songModelList.get(songPosition).getId());
            RecyclerView recyclerViewAddToPlaylist = dialog.findViewById(R.id.recyclerView_add_to_playlist_dialog);
            recyclerViewAddToPlaylist.setLayoutManager(new LinearLayoutManager(SimplePlayerActivity.this));
            recyclerViewAddToPlaylist.setAdapter(addToPlaylistDialogAdapter);

            //load all playlist in db
            db.collection("Playlist").document(auth.getUid()).collection("User")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                PlaylistModel playlistModel = doc.toObject(PlaylistModel.class);
                                playlistModelList.add(playlistModel);
                            }
                            addToPlaylistDialogAdapter.notifyDataSetChanged();
                            dialog.show();
                        }
                    });
        });

        imageViewDownload.setOnClickListener(view -> {
            DowloadMusicFile();
        });

        imageViewNext.setOnClickListener(view -> {
            Next();
        });

        imageViewPrevious.setOnClickListener(view -> {
            Previous();
        });

        imageViewPlay.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()){
                Pause();
            }
            else {
                Play();
            }
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
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference reference;

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Đang chuẩn bị tải về!");
        pd.setIndeterminate(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();

        String songTitle = songModelList.get(songPosition).getTitle();

        reference = storageReference.child("Songs/"+ songTitle + ".mp3");

        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            String url = uri.toString();
            DownloadFile(SimplePlayerActivity.this, songTitle, "mp3", DIRECTORY_DOWNLOADS, url);
            Toast.makeText(SimplePlayerActivity.this, "Đang tải về", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(SimplePlayerActivity.this, "Không tải được", Toast.LENGTH_LONG).show();
            pd.dismiss();
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
                        if (playState.equals("Shuffle")){
                            CreateRandomTrackPosition();
                        }
                        else {
                            if (playState.equals("Loop")){
                                mediaPlayer.reset();
                            }
                            else {
                                songPosition++;
                            }
                        }
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
        textViewTitle.setSelected(true);
        textViewArtist = findViewById(R.id.textViewArtistPlayer);
        textViewArtist.setSelected(true);

        seekBar = findViewById(R.id.seekBar);
        seekBarVolume = findViewById(R.id.seekBarVolume);

        imageViewPlay = findViewById(R.id.imageViewPlay);
//        imageView = findViewById(R.id.imageViewPlayer);
        imageViewNext = findViewById(R.id.imageViewNext);
        imageViewPrevious = findViewById(R.id.imageViewPrevious);
        imageViewDownload = findViewById(R.id.imageViewDownload);
        imageViewAddToPlaylist = findViewById(R.id.imageViewAddToPlaylist);
        imageViewShuffleAndLoop = findViewById(R.id.imageViewShuffleAndLoop);
        circleImageView = findViewById(R.id.imageViewPlayer);

        recyclerViewComment = findViewById(R.id.recyclerView_comment);
        listViewListSong = findViewById(R.id.listView_list_song);

        editTextComment = findViewById(R.id.editTextComment);

        buttonAddComment = findViewById(R.id.buttonAddComment);

        rotatingImageAnimation = ObjectAnimator.ofFloat(circleImageView, "rotation", 0, 360);
        rotatingImageAnimation.setDuration(10000);
        rotatingImageAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotatingImageAnimation.setRepeatMode(ObjectAnimator.REVERSE);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerViewComment.setLayoutManager(new LinearLayoutManager(SimplePlayerActivity.this));
        commentModelList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentModelList);
        recyclerViewComment.setAdapter(commentAdapter);

        List<String> listSong = new ArrayList<>();
        if (songModelList != null){
            for (SongModel x : songModelList){
               listSong.add(x.getTitle());
            }
        }

        if (listSong != null){
            ArrayAdapter arrayAdapter = new ArrayAdapter(SimplePlayerActivity.this, android.R.layout.simple_list_item_1, listSong);
            listViewListSong.setAdapter(arrayAdapter);
        }

    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
            return imageBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        Glide.with(SimplePlayerActivity.this).asBitmap().load(imageUrl)
//                .into(new CustomTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//
//            }
//
//            @Override
//            public void onLoadCleared(@Nullable Drawable placeholder) {
//
//            }
//        });
//
//        Bitmap bitmap = Glide.
//                with(this).
//                asBitmap().
//                load(imageUrl).submit().get();
//        return bitmap;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CreateMediaPlayer() throws IOException {

        commentModelList.clear();
        editTextComment.setText("");
        editTextComment.clearFocus();

        //load comment
        db.collection("Comment").document(auth.getUid()).collection("User")
                .whereEqualTo("song_id", songModelList.get(songPosition).getId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            CommentModel commentModel = doc.toObject(CommentModel.class);
                            commentModelList.add(commentModel);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }
                });

        Glide.with(getApplicationContext()).load(songModelList.get(songPosition).getImg_url()).into(circleImageView);
        textViewTitle.setText(songModelList.get(songPosition).getTitle());
        int artistListLength = songModelList.get(songPosition).getArtist().size();

        artistText[0] = "";

        for (int i=0; i<artistListLength; i++){
            FirebaseFirestore.getInstance().collection("Artist").document(songModelList.get(songPosition).getArtist().get(i))
                    .get().addOnCompleteListener(task -> {
                DocumentSnapshot doc = task.getResult();
                ArtistModel artistModel = doc.toObject(ArtistModel.class);
                artistText[0] += artistModel.getName() + ", ";
                textViewArtist.setText(artistText[0] + "");
            });
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(songModelList.get(songPosition).getUrl());
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        imageViewPlay.setImageResource(R.drawable.icons8_pause_64);
        CreateNotification.CreateNotification(SimplePlayerActivity.this, songModelList.get(songPosition),
                R.drawable.ic_pause_black_24dp, songPosition, songModelList.size() - 1, getBitmapFromURL(songModelList.get(songPosition).getImg_url()), artistText[0]);

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if (playState.equals("Loop"))
                mediaPlayer.reset();
            else {
                if (playState.equals("Shuffle")){
                    CreateRandomTrackPosition();
                    try {
                        CreateMediaPlayer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    if (playState.equals("Go")){
                        Next();
                    }
                }
            }
        });

        rotatingImageAnimation.start();
    }

    private void CreateRandomTrackPosition() {
        int limit = songModelList.size();
        Random random = new Random();
        int randomNumber = random.nextInt(limit);
        songPosition = randomNumber;
    }


    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        unregisterReceiver(broadcastReceiver);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getExtras().getString("actionName");

            switch (action){
                case CreateNotification.ACTION_PREVIOUS:
                    Previous();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (mediaPlayer.isPlaying()){
                        Pause();
                    }
                    else {
                        Play();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    Next();
                    break;
            }
        }
    };

    private void Play(){
        mediaPlayer.start();
        imageViewPlay.setImageResource(R.drawable.icons8_pause_64);
        SetTime();
        UpdateProgress();
        CreateNotification.CreateNotification(SimplePlayerActivity.this, songModelList.get(songPosition),
                R.drawable.ic_pause_black_24dp, songPosition, songModelList.size() - 1, getBitmapFromURL(songModelList.get(songPosition).getImg_url()), artistText[0]);

        if (rotatingImageAnimation.isPaused()){
            rotatingImageAnimation.resume();
        }
    }

    private void Pause(){
        mediaPlayer.pause();
        imageViewPlay.setImageResource(R.drawable.icons8_play_64);
        SetTime();
        UpdateProgress();
        CreateNotification.CreateNotification(SimplePlayerActivity.this, songModelList.get(songPosition),
                R.drawable.ic_play_arrow_black_24dp, songPosition, songModelList.size() - 1, getBitmapFromURL(songModelList.get(songPosition).getImg_url()), artistText[0]);

        rotatingImageAnimation.pause();
    }

    private void Previous() {
        songPosition -= 1;
        int maxLength = songModelList.size();
        if (songPosition < 0){
            songPosition = maxLength - 1;
        }
        if (playState.equals("Shuffle")){
            CreateRandomTrackPosition();
        }
        else {
            if (playState.equals("Loop")){
                songPosition += 1;
                mediaPlayer.reset();
            }
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
        CreateNotification.CreateNotification(SimplePlayerActivity.this, songModelList.get(songPosition),
                R.drawable.ic_pause_black_24dp, songPosition, songModelList.size() - 1, getBitmapFromURL(songModelList.get(songPosition).getImg_url()), artistText[0]);
//
    }

    private void Next() {
        songPosition += 1;
        int maxLength = songModelList.size();
        if (songPosition > maxLength - 1){
            songPosition = 0;
        }
        if (playState.equals("Shuffle")){
            CreateRandomTrackPosition();
        }
        else {
            if (playState.equals("Loop")){
                songPosition -= 1;
                mediaPlayer.reset();
            }
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
        CreateNotification.CreateNotification(SimplePlayerActivity.this, songModelList.get(songPosition),
                R.drawable.ic_pause_black_24dp, songPosition, songModelList.size() - 1, getBitmapFromURL(songModelList.get(songPosition).getImg_url()), artistText[0]);
//
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_simple_player);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Intent intent = getIntent();
        songModelList = (List<SongModel>) intent.getSerializableExtra(Variables.LIST_SONG_MODEL_OBJECT);
        songPosition = intent.getIntExtra(Variables.POSITION, 0);

        ViewBinding();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CreateChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        try {
            CreateMediaPlayer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SetTime();
        UpdateProgress();
        Listener();
    }

    private void CreateChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, "MusicPlayer", NotificationManager.IMPORTANCE_HIGH);
            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            notificationManager.cancelAll();
//        }
//        unregisterReceiver(broadcastReceiver);
        mediaPlayer.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }
}