package com.example.musicapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.Variables;
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
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.List;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.ViewHolder> {

    Context context;
    List<SongModel> songModelList;
    Intent intent;

    public ListSongAdapter(Context context, List<SongModel> songModelList, Intent intent) {
        this.context = context;
        this.songModelList = songModelList;
        this.intent = intent;
    }

    public ListSongAdapter(Context context, List<SongModel> songModelList) {
        this.context = context;
        this.songModelList = songModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(songModelList.get(position).getImg_url()).into(holder.imageView);

        holder.textViewTitle.setText(songModelList.get(position).getTitle());

        int artistListLength = songModelList.get(position).getArtist().size();

//        Log.i("TAG1", "onBindViewHolder: " + artistListLength + "   ----   " + songModelList.get(position).getTitle());

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
            Intent intent = new Intent(context, SimplePlayerActivity.class);
            intent.putExtra(Variables.LIST_SONG_MODEL_OBJECT, (Serializable) songModelList);
            intent.putExtra(Variables.POSITION, position);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getRootView().getContext());
                alertDialog.setIcon(R.drawable.icons8_delete_dialog_96);
                alertDialog.setTitle("Có chắc muốn xoá chứ?");
                alertDialog.setCancelable(false);

                alertDialog.setPositiveButton("Xoá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String playlistID = intent.getStringExtra(Variables.PLAYLIST_ID);
                        String playlistTitle = intent.getStringExtra(Variables.PLAYLIST_TITLE);

                        //get all song already in playlist
                        final List<String>[] songTitleFirst = new List[]{new ArrayList<>()};
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        DocumentReference documentReference = db.collection("Playlist")
                                .document(auth.getCurrentUser().getUid()).collection("User").document(playlistID);
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.get("song") != null){
                                    PlaylistModel playlistModel = documentSnapshot.toObject(PlaylistModel.class);
                                    songTitleFirst[0] = playlistModel.getSong();
                                    //add this row song to that list
                                    List<String> songTitle = new ArrayList<>();
                                    if (songTitleFirst[0] != null){
                                        for (String x : songTitleFirst[0]){
                                            songTitle.add(x);
                                        }
                                    }

//                                    if (songTitle != null){
//                                        for (String x : songTitle){
//                                            if (x.equals(songModelList.get(position).getTitle())){
//                                                songTitle.remove(x);
//                                            }
//                                        }
//                                    }
                                    songTitle.removeIf((String x) -> x.equals(songModelList.get(position).getTitle()));

                                    documentReference.delete();
                                    //update
                                    playlistModel = new PlaylistModel(playlistTitle, songTitle);
                                    playlistModel.setID(playlistID);
//                            Log.i("TAG1", "onClick2: " + playlistModel);
                                    db.collection("Playlist").document(auth.getUid())
                                            .collection("User").document(playlistID).set(playlistModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "Xoá thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });
                    }
                });
                alertDialog.setNegativeButton("Không xoá nữa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
                return false;
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
            imageView = itemView.findViewById(R.id.imageViewListSongItem);
            textViewTitle = itemView.findViewById(R.id.textViewTitleListSongItem);
            textViewArtist = itemView.findViewById(R.id.textViewArtistListSongItem);
        }
    }
}
