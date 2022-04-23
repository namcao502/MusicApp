package com.example.musicapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.adapters.SongAdapter;
import com.example.musicapp.models.SongModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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

    @SuppressLint("NotifyDataSetChanged")
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

}