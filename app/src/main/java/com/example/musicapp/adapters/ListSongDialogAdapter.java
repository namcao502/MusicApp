package com.example.musicapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.models.PlaylistModel;
import com.example.musicapp.models.SongModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

        holder.itemView.setOnClickListener(view -> {

            String playlistID = intent.getStringExtra(Variables.PLAYLIST_ID);
            String playlistTitle = intent.getStringExtra(Variables.PLAYLIST_TITLE);

//                Log.i("TAG1", "onClick: " + playlistID + "  ---   " + playlistTitle);
            //get all song already in playlist
            final List<String>[] songIdFirst = new List[]{new ArrayList<>()};
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            DocumentReference documentReference = db.collection("Playlist")
                    .document(auth.getCurrentUser().getUid()).collection("User").document(playlistID);

            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.get("song_id") != null){

                    PlaylistModel playlistModel = documentSnapshot.toObject(PlaylistModel.class);
                    songIdFirst[0] = playlistModel.getSong_id();

                    //add this row song to that list
                    List<String> songId = new ArrayList<>();
                    if (songIdFirst[0] != null){
                        for (String x : songIdFirst[0]){
                            songId.add(x);
                        }
                    }
                    songId.add(songModelList.get(position).getId());

                    documentReference.delete();

                    //update
                    playlistModel = new PlaylistModel(playlistTitle, songId);
                    playlistModel.setId(playlistID);
                    db.collection("Playlist").document(auth.getUid())
                            .collection("User").document(playlistID).set(playlistModel)
                            .addOnCompleteListener(task ->
                                    Toast.makeText(context, "Thêm " + songModelList.get(position).getTitle() + " thành công", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show());

                }
            });
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
