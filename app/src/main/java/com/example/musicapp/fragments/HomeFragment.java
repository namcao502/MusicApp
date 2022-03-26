package com.example.musicapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import com.example.musicapp.adapters.CountryAdapter;
import com.example.musicapp.adapters.GenreAdapter;
import com.example.musicapp.models.ArtistModel;
import com.example.musicapp.models.CountryModel;
import com.example.musicapp.models.GenreModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerViewGenre, recyclerViewArtist, recyclerViewCountry;
    FirebaseFirestore db;

    ProgressDialog progressDialog;
    LinearLayout linearLayoutHome;

    //genre
    GenreAdapter genreAdapter;
    List<GenreModel> genreModelList;

    //artist
    ArtistAdapter artistAdapter;
    List<ArtistModel> artistModelList;

    //country
    CountryAdapter countryAdapter;
    List<CountryModel> countryModelList;

    ImageSlider imageSlider;


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
        recyclerViewCountry = view.findViewById(R.id.recyclerView_country);
        linearLayoutHome = view.findViewById(R.id.home_layout);
        imageSlider = view.findViewById(R.id.image_slider);

        linearLayoutHome.setVisibility(View.GONE);
        db = FirebaseFirestore.getInstance();

        LoadImageSlider();

        LoadDialog();

        LoadAllGenre();

        LoadAllArtist();

        LoadAllCountry();

        return view;
    }

    private void LoadImageSlider() {
        //image slider
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.ncs_slide, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.alan_walker_slide, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.bolero_slide, ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels);
    }

    private void LoadDialog() {
        //progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Chào mừng bạn đến với MusicApp!");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void LoadAllGenre() {
        //setup data for genre
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
    }

    private void LoadAllArtist() {
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
    }

    private void LoadAllCountry() {
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
    }
}