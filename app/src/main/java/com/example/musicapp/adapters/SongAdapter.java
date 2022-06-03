package com.example.musicapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.example.musicapp.activities.SimplePlayerActivity;
import com.example.musicapp.models.ArtistModel;
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

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    Context context;
    List<SongModel> songModelList;

    public SongAdapter(Context context, List<SongModel> songModelList) {
        this.context = context;
        this.songModelList = songModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_all_song_song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(songModelList.get(position).getImg_url()).into(holder.imageView);

        holder.textViewTitle.setText(songModelList.get(position).getTitle());

        if (songModelList.get(position).getArtist() != null){
            int artistListLength = songModelList.get(position).getArtist().size();

            final String[] artistText = {""};
            artistText[0] = "";

            for (int i=0; i<artistListLength; i++){
                FirebaseFirestore.getInstance().collection("Artist").document(songModelList.get(position).getArtist().get(i))
                        .get().addOnCompleteListener(task -> {
                            DocumentSnapshot doc = task.getResult();
                            ArtistModel artistModel = doc.toObject(ArtistModel.class);
                            if (artistModel != null){
                                artistText[0] += artistModel.getName() + ", ";
                                holder.textViewArtist.setText(artistText[0]+ "");
                            }
                        });
            }
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, SimplePlayerActivity.class);
            intent.putExtra(Variables.LIST_SONG_MODEL_OBJECT, (Serializable) songModelList);
            intent.putExtra(Variables.POSITION, position);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(view -> {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getRootView().getContext());
            alertDialog.setIcon(R.drawable.icons8_delete_dialog_96);
            alertDialog.setTitle("Có chắc muốn xoá chứ?");
            alertDialog.setCancelable(false);

            alertDialog.setPositiveButton("Xoá", (dialogInterface, i) -> {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                if (auth.getUid().equals(Variables.ADMIN_ID)){
                    db.collection("Song").document(songModelList.get(position).getId()).delete()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(context, "Xoá thành công", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Xoá thất bại", Toast.LENGTH_SHORT).show());
                }
                else {
                    Toast.makeText(context, "Bạn không có quyền xoá", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.setNegativeButton("Không xoá nữa", (dialogInterface, i) -> {

            });
            alertDialog.show();
            return false;
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
            imageView = itemView.findViewById(R.id.imageViewSongItem);
            textViewTitle = itemView.findViewById(R.id.textViewTitleSongItem);
            textViewArtist = itemView.findViewById(R.id.textViewArtistSongItem);
        }
    }
}
