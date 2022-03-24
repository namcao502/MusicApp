package com.example.musicapp.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.adapters.PlaylistAdapter;
import com.example.musicapp.models.PlaylistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    Toolbar toolbar;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.playlist_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

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
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.playlist_menu_add:

                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.fragment_playlist_add_new_list_dialog);
                    dialog.show();

                    Button buttonAdd = dialog.findViewById(R.id.buttonAddNewListDialog);
                    EditText editTextTitle = dialog.findViewById(R.id.editTextAddNewListDialog);

                    buttonAdd.setOnClickListener(view1 -> {

                        String playlistTitle = editTextTitle.getText().toString();

                        if (playlistTitle.isEmpty()) {
                            Toast.makeText(getContext(), "Vui lòng nhập tên danh sách phát", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<String> listSong = new ArrayList<>();
                        PlaylistModel playlistModel = new PlaylistModel(playlistTitle, listSong);
                        DocumentReference documentReference = db.collection("Playlist").document(auth.getUid()).collection("User").document();
                        playlistModel.setId(documentReference.getId());
                        documentReference.set(playlistModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                    });
                    return true;
            }
            return true;
        });

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

        return view;
    }
}