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
import com.example.musicapp.adapters.PlaylistAdapter;
import com.example.musicapp.models.PlaylistModel;
import com.example.musicapp.models.SongModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlaylistFragment extends Fragment {

    RecyclerView recyclerViewPlaylist;
    List<PlaylistModel> playlistModelList;
    PlaylistAdapter playlistAdapter;
    FirebaseFirestore db;
    FirebaseAuth auth;

    ProgressDialog progressDialog;
    LinearLayout linearLayoutPlaylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        recyclerViewPlaylist = view.findViewById(R.id.recyclerView_playlist);
        linearLayoutPlaylist = view.findViewById(R.id.playlist_layout);
        linearLayoutPlaylist.setVisibility(View.GONE);

        //processDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Đang tải tất cả danh sách...");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //setup data for playlist
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistModelList = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(getContext(), playlistModelList);
        recyclerViewPlaylist.setAdapter(playlistAdapter);

        //setup data for playlist
//        db.collection("Playlist").whereEqualTo(FieldPath.documentId(), auth.getUid()).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot doc : task.getResult()){
//                    PlaylistModel playlistModel = doc.toObject(PlaylistModel.class);
//                    playlistModelList.add(playlistModel);
//                    playlistAdapter.notifyDataSetChanged();
//                    linearLayoutPlaylist.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        List<String> listSong = new ArrayList<>();
//        listSong.add("Numa numa 2");
//        listSong.add("Walk Thru Fire");
//        listSong.add("Savannah");
//        Map<String, Object> map = new HashMap<>();
//        map.put("name", "Favorite");
//        map.put("song", listSong);
//
//        db.collection("Playlist").document(auth.getCurrentUser().getUid()).collection("User")
//                .document().set(map).addOnCompleteListener(task -> {
//            if (task.isSuccessful()){
//                Log.i("TAG1", "onCreateView: " + "success");
//            }
//        });


        db.collection("Playlist").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).
                collection("User").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            PlaylistModel playlistModel = doc.toObject(PlaylistModel.class);
                            playlistModelList.add(playlistModel);
                            Log.i("TAG1", "onCreateView: " + playlistModelList.toString());
                        }
                        playlistAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                        linearLayoutPlaylist.setVisibility(View.VISIBLE);
                    }
        });

        return view;
    }
}