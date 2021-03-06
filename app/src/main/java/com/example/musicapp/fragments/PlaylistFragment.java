package com.example.musicapp.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.adapters.PlaylistAdapter;
import com.example.musicapp.models.PlaylistModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaylistFragment extends Fragment {

    RecyclerView recyclerViewPlaylist;
    List<PlaylistModel> playlistModelList;
    PlaylistAdapter playlistAdapter;
    FirebaseFirestore db;
    FirebaseAuth auth;

    ProgressDialog progressDialog;
    LinearLayout linearLayoutPlaylist;

    Toolbar toolbar;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.playlist_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("NonConstantResourceId")
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

        //toolbar
        setHasOptionsMenu(true);
        toolbar = view.findViewById(R.id.playlist_toolbar);
        toolbar.setTitle("Danh s??ch ph??t");
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.playlist_menu_add){

                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.fragment_playlist_add_new_list_dialog);
                dialog.show();

                Button buttonAdd = dialog.findViewById(R.id.buttonAddNewListDialog);
                EditText editTextTitle = dialog.findViewById(R.id.editTextAddNewListDialog);

                buttonAdd.setOnClickListener(view1 -> {

                    String playlistTitle = editTextTitle.getText().toString();

                    if (playlistTitle.isEmpty()) {
                        Toast.makeText(getContext(), "Vui l??ng nh???p t??n danh s??ch ph??t", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<String> listSongId = new ArrayList<>();
                    PlaylistModel playlistModel = new PlaylistModel(playlistTitle, listSongId);
                    DocumentReference documentReference = db.collection("Playlist").document(Objects.requireNonNull(auth.getUid())).collection("User").document();
                    playlistModel.setId(documentReference.getId());

                    documentReference.set(playlistModel).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Th??m th??nh c??ng", Toast.LENGTH_SHORT).show();
                            LoadAllPlaylist();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Th??m th???t b???i", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                });
            }
            return true;
        });

        //processDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("??ang t???i t???t c??? danh s??ch...");
        progressDialog.setMessage("Vui l??ng ch???...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //setup data for playlist
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistModelList = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(getContext(), playlistModelList);
        recyclerViewPlaylist.setAdapter(playlistAdapter);

        LoadAllPlaylist();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadAllPlaylist(){
        playlistModelList.clear();
        db.collection("Playlist").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).
                collection("User").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    PlaylistModel playlistModel = doc.toObject(PlaylistModel.class);
                    playlistModel.setId(doc.getId());
                    playlistModelList.add(playlistModel);
                }
                playlistAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
                linearLayoutPlaylist.setVisibility(View.VISIBLE);
            }
        });
    }
}