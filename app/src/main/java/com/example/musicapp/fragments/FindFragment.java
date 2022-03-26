package com.example.musicapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.adapters.ArtistAdapter;
import com.example.musicapp.adapters.CountryAdapter;
import com.example.musicapp.adapters.GenreAdapter;
import com.example.musicapp.adapters.SongAdapter;
import com.example.musicapp.models.ArtistModel;
import com.example.musicapp.models.CountryModel;
import com.example.musicapp.models.GenreModel;
import com.example.musicapp.models.SongModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FindFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;
    RecyclerView recyclerViewFindArtist, recyclerViewFindSong, recyclerViewFindGenre, recyclerViewFindCountry;

    EditText editTextFind;
    Button buttonFind;

    //genre
    GenreAdapter genreAdapter;
    List<GenreModel> genreModelList;

    //artist
    ArtistAdapter artistAdapter;
    List<ArtistModel> artistModelList;

    //country
    CountryAdapter countryAdapter;
    List<CountryModel> countryModelList;

    //song
    SongAdapter songAdapter;
    List<SongModel> songModelList;

    LinearLayout linearLayoutFindGenre, linearLayoutFindArtist, linearLayoutFindSong, linearLayoutFindCountry;


    public FindFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find, container, false);

        //binding
        editTextFind = view.findViewById(R.id.editTextFindFragment);
        buttonFind = view.findViewById(R.id.buttonFindFragment);

        recyclerViewFindArtist = view.findViewById(R.id.recyclerView_find_artist);
        recyclerViewFindSong = view.findViewById(R.id.recyclerView_find_song);
        recyclerViewFindGenre = view.findViewById(R.id.recyclerView_find_genre);
        recyclerViewFindCountry = view.findViewById(R.id.recyclerView_find_country);

        linearLayoutFindArtist = view.findViewById(R.id.find_artist_layout);
        linearLayoutFindGenre = view.findViewById(R.id.find_genre_layout);
        linearLayoutFindSong = view.findViewById(R.id.find_song_layout);
        linearLayoutFindCountry = view.findViewById(R.id.find_country_layout);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();



        buttonFind.setOnClickListener(view1 -> {
            String keyword = editTextFind.getText().toString();
            if (keyword.isEmpty()){
                Toast.makeText(getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                LoadAllGenre(keyword);
                if (genreModelList == null){
                    linearLayoutFindGenre.setVisibility(View.GONE);
                }
                LoadAllArtist(keyword);
                if (artistModelList == null){
                    linearLayoutFindArtist.setVisibility(View.GONE);
                }
                LoadAllCountry(keyword);
                if (songModelList == null){
                    linearLayoutFindCountry.setVisibility(View.GONE);
                }
                LoadAllSong(keyword);
                if (countryModelList == null){
                    linearLayoutFindSong.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    private void LoadAllSong(String keyword) {
        //setup data for song
        recyclerViewFindSong.setLayoutManager(new LinearLayoutManager(getContext()));
        songModelList = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(), songModelList);
        recyclerViewFindSong.setAdapter(songAdapter);

        db.collection("Song").whereEqualTo("title", keyword).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    SongModel songModel = doc.toObject(SongModel.class);
                    songModelList.add(songModel);
                }
                songAdapter.notifyDataSetChanged();
                linearLayoutFindSong.setVisibility(View.VISIBLE);
            }
        });
    }

    private void LoadAllCountry(String keyword) {

        //setup data for country
        recyclerViewFindCountry.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        countryModelList = new ArrayList<>();
        countryAdapter = new CountryAdapter(getContext(), countryModelList);
        recyclerViewFindCountry.setAdapter(countryAdapter);
        db.collection("Country").whereEqualTo("name", keyword).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    CountryModel countryModel = doc.toObject(CountryModel.class);
                    countryModelList.add(countryModel);
                }
                countryAdapter.notifyDataSetChanged();
                linearLayoutFindCountry.setVisibility(View.VISIBLE);
            }
        });
    }

    private void LoadAllGenre(String keyword) {

        //setup data for genre
        recyclerViewFindGenre.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        genreModelList = new ArrayList<>();
        genreAdapter = new GenreAdapter(getContext(), genreModelList);
        recyclerViewFindGenre.setAdapter(genreAdapter);

        db.collection("Genre").whereEqualTo("name", keyword).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    GenreModel genreModel = doc.toObject(GenreModel.class);
                    genreModelList.add(genreModel);
                }
                genreAdapter.notifyDataSetChanged();
                linearLayoutFindGenre.setVisibility(View.VISIBLE);
            }
        });
    }

    private void LoadAllArtist(String keyword) {
        //setup data for artist
        recyclerViewFindArtist.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        artistModelList = new ArrayList<>();
        artistAdapter = new ArtistAdapter(getContext(), artistModelList);
        recyclerViewFindArtist.setAdapter(artistAdapter);

        db.collection("Artist").whereEqualTo("name", keyword).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    ArtistModel artistModel = doc.toObject(ArtistModel.class);
                    artistModelList.add(artistModel);
                }
                artistAdapter.notifyDataSetChanged();
                linearLayoutFindArtist.setVisibility(View.VISIBLE);
            }
        });
    }
}