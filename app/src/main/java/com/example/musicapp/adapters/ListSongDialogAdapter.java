package com.example.musicapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.activities.ListSongActivity;
import com.example.musicapp.activities.SimplePlayerActivity;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListSongDialogAdapter extends RecyclerView.Adapter<ListSongDialogAdapter.ViewHolder> {

    Context context;
    List<SongModel> songModelList;
    Intent intent;

    public ListSongDialogAdapter(Context context, List<SongModel> songModelList, Intent intent) {
        this.context = context;
        this.songModelList = songModelList;
        this.intent = intent;
    }

    public ListSongDialogAdapter(Context context, List<SongModel> songModelList) {
        this.context = context;
        this.songModelList = songModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_song_add_new_song_dialog_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(songModelList.get(position).getImg_url()).into(holder.imageView);

        holder.textViewTitle.setText(songModelList.get(position).getTitle());

        int artistListLength = songModelList.get(position).getArtist().size();

        String artistText = "";

        for (int i=0; i<artistListLength; i++){
            if (i == artistListLength - 1){
                artistText += songModelList.get(position).getArtist().get(i);
            }
            else {
                artistText += songModelList.get(position).getArtist().get(i) + ", ";
            }
        }
        if (artistText.isEmpty())
            holder.textViewArtist.setText("Không có nghệ sĩ");

        else
            holder.textViewArtist.setText(artistText);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playlistID = intent.getStringExtra(Variables.PLAYLIST_ID);
                String playlistTitle = intent.getStringExtra(Variables.PLAYLIST_TITLE);
                Log.i("TAG1", "onClick: " + playlistID + "  ---   " + playlistTitle);
                //get all song already in playlist
                final List<String>[] songTitleFirst = new List[]{new ArrayList<>()};
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DocumentReference documentReference = db.collection("Playlist")
                        .document(auth.getCurrentUser().getUid()).collection("User").document(playlistID);
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.get("song") != null){
                        PlaylistModel playlistModel = documentSnapshot.toObject(PlaylistModel.class);
                        songTitleFirst[0] = playlistModel.getSong();
//                            Log.i("TAG1", "onClick: " + documentSnapshot.getClass());

                        //add this row song to that list
                        List<String> songTitle = new ArrayList<>();
                        if (songTitleFirst[0] != null){
                            for (String x : songTitleFirst[0]){
                                songTitle.add(x);
                            }
                        }
                        songTitle.add(songModelList.get(position).getTitle());
                        documentReference.delete();
                        //update
                        playlistModel = new PlaylistModel(playlistTitle, songTitle);
                        playlistModel.setId(playlistID);
//                            Log.i("TAG1", "onClick2: " + playlistModel);
                        db.collection("Playlist").document(auth.getUid())
                                .collection("User").document(playlistID).set(playlistModel).addOnCompleteListener(task -> Toast.makeText(context, "Thêm " + songModelList.get(position).getTitle() + " thành công", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show());

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return songModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle, textViewArtist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewAddNewSongDialogItem);
            textViewTitle = itemView.findViewById(R.id.textViewTitleAddNewSongDialogItem);
            textViewArtist = itemView.findViewById(R.id.textViewArtistAddNewSongDialogItem);
        }

    }
}
