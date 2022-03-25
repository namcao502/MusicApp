package com.example.musicapp.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.adapters.ListSongAdapter;
import com.example.musicapp.adapters.ListSongDialogAdapter;
import com.example.musicapp.models.PlaylistModel;
import com.example.musicapp.models.SongModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListSongActivity extends AppCompatActivity {

    RecyclerView recyclerViewListSong;
    List<SongModel> songModelList;
    ListSongAdapter listSongAdapter;
    List<String> songTitleList = new ArrayList<>();

    FirebaseFirestore db;
    FirebaseAuth auth;

    Toolbar toolbar;

    Intent intent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_song_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);

        ViewBinding();

        LoadToolbar();

        if (GetDataFromIntent().equals("playlistIntent")){
            LoadAllReceivedSongFromPlaylist((List<String>) intent.getSerializableExtra(Variables.PLAYLIST_OBJECT));
        }

        if (GetDataFromIntent().equals("artistIntent")){
            String artistTitle = intent.getStringExtra(Variables.ARTIST_TITLE);
            LoadAllReceivedSongFromArtist(artistTitle);
            toolbar.setVisibility(View.GONE);
        }

        if (GetDataFromIntent().equals("genreIntent")){
            String genreTitle = intent.getStringExtra(Variables.GENRE_TITLE);
            LoadAllReceivedSongFromGenre(genreTitle);
            toolbar.setVisibility(View.GONE);
        }

        if (GetDataFromIntent().equals("countryIntent")){
            String countryTitle = intent.getStringExtra(Variables.COUNTRY_TITLE);
            LoadAllReceivedSongFromCountry(countryTitle);
            toolbar.setVisibility(View.GONE);
        }
    }

    private void LoadAllReceivedSongFromCountry(String countryTitle) {
        db.collection("Song").whereEqualTo("country", countryTitle).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    SongModel songModel = doc.toObject(SongModel.class);
                    songModelList.add(songModel);
                }
                listSongAdapter.notifyDataSetChanged();
            }
        });
    }

    private void LoadAllReceivedSongFromGenre(String genreTitle) {
        db.collection("Song").whereEqualTo("genre", genreTitle).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    SongModel songModel = doc.toObject(SongModel.class);
                    songModelList.add(songModel);
                }
                listSongAdapter.notifyDataSetChanged();
            }
        });
    }

    private void LoadAllReceivedSongFromArtist(String artistTitle) {
        db.collection("Song").whereArrayContains("artist", artistTitle).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    SongModel songModel = doc.toObject(SongModel.class);
                    songModelList.add(songModel);
                }
                listSongAdapter.notifyDataSetChanged();
            }
        });
    }

    private String GetDataFromIntent() {
        //get data from intent
        intent = getIntent();
        return intent.getStringExtra(Variables.INTENT_TYPE);
    }

    private void ViewBinding() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerViewListSong = findViewById(R.id.recyclerView_list_song);
        toolbar = findViewById(R.id.list_song_toolbar);

        recyclerViewListSong.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        songModelList = new ArrayList<>();
        listSongAdapter = new ListSongAdapter(getApplicationContext(), songModelList, intent);
        recyclerViewListSong.setAdapter(listSongAdapter);
    }

    private void LoadToolbar() {
        //toolbar

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.playlist_menu_add:

                        Dialog dialog = new Dialog(ListSongActivity.this);
                        dialog.setContentView(R.layout.activity_list_song_add_new_song_dialog);

                        RecyclerView recyclerViewAddNewSongDialog = dialog.findViewById(R.id.recyclerView_list_song_dialog);
                        Button buttonAddAll = dialog.findViewById(R.id.buttonAddNewListDialogAddAll);

                        recyclerViewAddNewSongDialog.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        List<SongModel> songModelListTemp = new ArrayList<>();
                        ListSongDialogAdapter listSongDialogAdapter = new ListSongDialogAdapter(getApplicationContext(), songModelListTemp, intent);
                        recyclerViewAddNewSongDialog.setAdapter(listSongDialogAdapter);

                        db.collection("Song").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot doc : task.getResult()){
                                        SongModel songModel = doc.toObject(SongModel.class);
                                        songModelListTemp.add(songModel);
                                    }
                                    listSongDialogAdapter.notifyDataSetChanged();
                                }
                            }
                        });

                        buttonAddAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //get playlist title from intent
                                String playlistID = intent.getStringExtra(Variables.PLAYLIST_ID);
                                String playlistTitle = intent.getStringExtra(Variables.PLAYLIST_TITLE);
                                //get song that already in playlist
                                final List<String>[] songTitleFirst = new List[]{new ArrayList<>()};

                                DocumentReference documentReference = db.collection("Playlist")
                                        .document(auth.getCurrentUser().getUid()).collection("User").document(playlistID);
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.get("song") != null){
                                            PlaylistModel playlistModel = documentSnapshot.toObject(PlaylistModel.class);
                                            songTitleFirst[0] = playlistModel.getSong();
                                        }
                                    }
                                });

                                documentReference.delete();

                                //get all song title in db
                                List<String> songTitle = new ArrayList<>();
                                for (SongModel x : songModelListTemp){
                                    songTitle.add(x.getTitle());
                                }
                                if (songTitleFirst[0] != null){
                                    for (String x : songTitleFirst[0]){
                                        songTitle.add(x);
                                    }
                                }

                                PlaylistModel playlistModel = new PlaylistModel(playlistTitle, songTitle);
                                playlistModel.setId(playlistID);
                                db.collection("Playlist").document(auth.getUid())
                                        .collection("User").document(playlistID).set(playlistModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ListSongActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ListSongActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });

                        dialog.show();

                        return true;
                }
                return true;
            }
        });
    }

    private void LoadAllReceivedSongFromPlaylist(List<String> songTitleList) {

        //get data from song collection
        if (songTitleList != null){
            for (String x : songTitleList){
                db.collection("Song")
                        .whereEqualTo("title", x).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            SongModel songModel = doc.toObject(SongModel.class);
                            songModelList.add(songModel);
//                            Log.i("TAG1", "list song activity: " + songModelList.toString());
                        }
                        listSongAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }
}