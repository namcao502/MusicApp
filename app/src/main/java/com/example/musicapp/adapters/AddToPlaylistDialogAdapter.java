package com.example.musicapp.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.activities.ListSongActivity;
import com.example.musicapp.models.PlaylistModel;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddToPlaylistDialogAdapter extends RecyclerView.Adapter<AddToPlaylistDialogAdapter.ViewHolder> {

    Context context;
    List<PlaylistModel> list;
    String songTitle = "";
    String playlistTitle = "";

    public AddToPlaylistDialogAdapter(Context context, List<PlaylistModel> list, String songTitle) {
        this.context = context;
        this.list = list;
        this.songTitle = songTitle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_simple_player_add_to_playlist_dialog_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.textView.setText(list.get(position).getTitle());
        Log.i("TAG1", "onBindViewHolder: " + list.get(position).getTitle());
        holder.itemView.setOnClickListener(view -> {

            //get all song already in playlist
            final List<String>[] songTitleFirst = new List[]{new ArrayList<>()};
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DocumentReference documentReference = db.collection("Playlist")
                    .document(auth.getCurrentUser().getUid()).collection("User").document(list.get(position).getId());
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.get("song") != null){
                    PlaylistModel playlistModel = documentSnapshot.toObject(PlaylistModel.class);
                    songTitleFirst[0] = playlistModel.getSong();
                    playlistTitle = playlistModel.getTitle();
                    //add this row song to that list
                    List<String> songTitleList = new ArrayList<>();
                    if (songTitleFirst[0] != null){
                        for (String x : songTitleFirst[0]){
                            songTitleList.add(x);
                        }
                    }

                    songTitleList.add(songTitle);

                    db.collection("Playlist").document(auth.getUid()).collection("User").document(list.get(position).getId())
                            .update("song", songTitleList).addOnCompleteListener(task ->
                                Toast.makeText(context, "Thêm " + songTitle + " vào " + playlistTitle +  " thành công", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show());

                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewTitleAddToPlaylistDialogItem);
        }
    }
}
