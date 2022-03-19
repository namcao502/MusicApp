package com.example.musicapp.fragments;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.musicapp.R;
import com.example.musicapp.adapters.ArtistAdapter;
import com.example.musicapp.adapters.ComposerAdapter;
import com.example.musicapp.adapters.CountryAdapter;
import com.example.musicapp.adapters.GenreAdapter;
import com.example.musicapp.adapters.SongAdapter;
import com.example.musicapp.models.ArtistModel;
import com.example.musicapp.models.ComposerModel;
import com.example.musicapp.models.CountryModel;
import com.example.musicapp.models.GenreModel;
import com.example.musicapp.models.SongModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerViewGenre, recyclerViewArtist, recyclerViewSong, recyclerViewComposer, recyclerViewCountry;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    LinearLayout linearLayoutHome;

    //genre
    GenreAdapter genreAdapter;
    List<GenreModel> genreModelList;

    //artist
    ArtistAdapter artistAdapter;
    List<ArtistModel> artistModelList;

    //composer
    ComposerAdapter composerAdapter;
    List<ComposerModel> composerModelList;

    //country
    CountryAdapter countryAdapter;
    List<CountryModel> countryModelList;


    public HomeFragment(){

    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //binding
        recyclerViewGenre = view.findViewById(R.id.recyclerView_genre);
        recyclerViewArtist = view.findViewById(R.id.recyclerView_artist);
        recyclerViewComposer = view.findViewById(R.id.recyclerView_composer);
        recyclerViewCountry = view.findViewById(R.id.recyclerView_country);
        linearLayoutHome = view.findViewById(R.id.home_layout);

        linearLayoutHome.setVisibility(View.GONE);
        db = FirebaseFirestore.getInstance();

        //image slider
        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.ncs_slide, "NoCopyRights", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.alan_walker_slide, "Alan X Walker", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.bolero_slide, "Sến", ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels);

        //progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Chào mừng bạn đến với MusicApp!");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //setup data for category
        recyclerViewGenre.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        genreModelList = new ArrayList<>();
        genreAdapter = new GenreAdapter(getContext(), genreModelList);
        recyclerViewGenre.setAdapter(genreAdapter);

        db.collection("Genre").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    GenreModel genreModel = doc.toObject(GenreModel.class);
                    genreModelList.add(genreModel);
                    linearLayoutHome.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
                genreAdapter.notifyDataSetChanged();
            }
        });

        //setup data for artist
        recyclerViewArtist.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        artistModelList = new ArrayList<>();
        artistAdapter = new ArtistAdapter(getContext(), artistModelList);
        recyclerViewArtist.setAdapter(artistAdapter);

        db.collection("Artist").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    ArtistModel artistModel = doc.toObject(ArtistModel.class);
                    artistModelList.add(artistModel);
                }
                artistAdapter.notifyDataSetChanged();
            }
        });

        //setup data for composer
        recyclerViewComposer.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        composerModelList = new ArrayList<>();
        composerAdapter = new ComposerAdapter(getContext(), composerModelList);
        recyclerViewComposer.setAdapter(composerAdapter);
        db.collection("Composer").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    ComposerModel composerModel = doc.toObject(ComposerModel.class);
                    composerModelList.add(composerModel);
                }
                composerAdapter.notifyDataSetChanged();
            }
        });

        //setup data for country
        recyclerViewCountry.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        countryModelList = new ArrayList<>();
        countryAdapter = new CountryAdapter(getContext(), countryModelList);
        recyclerViewCountry.setAdapter(countryAdapter);
        db.collection("Country").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    CountryModel countryModel = doc.toObject(CountryModel.class);
                    countryModelList.add(countryModel);
                }
                countryAdapter.notifyDataSetChanged();
            }
        });

//        //setup data for song
//        recyclerViewSong.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        songModelList = new ArrayList<>();
//        songAdapter = new SongAdapter(getContext(), songModelList);
//        recyclerViewSong.setAdapter(songAdapter);
//
//        db.collection("Song").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()){
//                for (QueryDocumentSnapshot doc : task.getResult()){
//                    SongModel songModel = doc.toObject(SongModel.class);
//                    String docId = doc.getId();
//                    List<ArtistModel> artistModelListTemp = new ArrayList<>();
//                    db.collection("Song").document(docId).collection("Artist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
//                            if (task2.isSuccessful()){
//                                for (QueryDocumentSnapshot doc2 : task2.getResult()){
//                                    ArtistModel artistModel = doc2.toObject(ArtistModel.class);
//                                    artistModelListTemp.add(artistModel);
//                                }
//                            }
//                        }
//                    });
//                    songModel.setArtistModelList(artistModelListTemp);
//                    songModelList.add(songModel);
//                    songAdapter.notifyDataSetChanged();
//                }
//            }
//        });

        return view;
    }
}