package com.example.musicapp.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.musicapp.R;
import com.example.musicapp.adapters.SongAdapter;
import com.example.musicapp.models.ArtistModel;

import com.example.musicapp.models.SongModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllSongFragment extends Fragment {

    RecyclerView recyclerViewAllSong;
    List<SongModel> songModelList;
    SongAdapter songAdapter;

    ProgressDialog progressDialog;
    LinearLayout linearLayoutAllSong;

    FirebaseFirestore db;

    public AllSongFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_song, container, false);

        //binding
        recyclerViewAllSong = view.findViewById(R.id.recyclerView_all_song);
        linearLayoutAllSong = view.findViewById(R.id.all_song_layout);
        linearLayoutAllSong.setVisibility(View.GONE);
        db = FirebaseFirestore.getInstance();

        LoadProgessDialog();

        LoadAllSong();

//        db.collection("Song").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot doc : task.getResult()) {
//                    //get a song
//                    SongModel songModel = doc.toObject(SongModel.class);
//                    //songModelList.add(songModel);
//                    //get id that song
//                    String docId = doc.getId();
////                    Log.d("TAG1", "ID: " + docId);
////                    Log.d("TAG1", "song model: " + songModel.toString());
//
//                    listIdSong.add(docId);
//                    listUrlSong.add(songModel.getUrl());
//                    listTitleSong.add(songModel.getTitle());
//                    listImgUrlSong.add(songModel.getImg_url());
//                }
////                Log.d("TAG1", "song model list size: " + songModelList.size());
//                //get artist data
//                getArtistFromIdSong(0);
//                linearLayoutAllSong.setVisibility(View.VISIBLE);
//                progressDialog.dismiss();
////                Log.i("TAG1", "finish: ");
//            }
//        });
//
        return view;
    }

    private void LoadProgessDialog() {
        //processDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Đang tải tất cả bài hát...");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void LoadAllSong() {
        //setup data for song
        recyclerViewAllSong.setLayoutManager(new LinearLayoutManager(getContext()));
        songModelList = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(), songModelList);
        recyclerViewAllSong.setAdapter(songAdapter);

        db.collection("Song").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    SongModel songModel = doc.toObject(SongModel.class);
                    songModelList.add(songModel);
                }
                songAdapter.notifyDataSetChanged();
                linearLayoutAllSong.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });
    }
//
//    private void getArtistFromIdSong(int index) {
//        indexID = index;
////        Log.d("TAG1", "id index :" + index);
//        String idSong = listIdSong.get(index);
//        String urlSong = listUrlSong.get(index);
//        String titleSong = listTitleSong.get(index);
//        String imgUrlSong = listImgUrlSong.get(index);
////        Log.d("TAG1", "getArtistFromIdSong: " + idSong);
//        db.collection("Song").document(idSong).collection("Artist").get().addOnCompleteListener(task -> {
////            Log.d("TAG1", ""+task.isSuccessful());
//            if (task.isSuccessful()) {
//                List<ArtistModel> artistModelListTemp = new ArrayList<>();
//                for (QueryDocumentSnapshot doc : task.getResult()) {
//                    ArtistModel artistModel = doc.toObject(ArtistModel.class);
//                    artistModelListTemp.add(artistModel);
////                    Log.d("TAG1", " artist :" + artistModel);
//                }
////                Log.d("TAG1", " artist list size :" + artistModelListTemp.size());
//                SongModel songModelTemp = new SongModel(titleSong, urlSong, imgUrlSong);
//                //SongModel songModelTemp = new SongModel();
//                songModelTemp.setArtist(artistModelListTemp);
//                songModelList.add(songModelTemp);
//                //songModelList.get(index).setArtist(artistModelListTemp);
//                songAdapter.notifyDataSetChanged();
//                if (indexID < listIdSong.size() - 1) {
//                    getArtistFromIdSong(indexID + 1);
//                }
//            }
//        });
//     }
}