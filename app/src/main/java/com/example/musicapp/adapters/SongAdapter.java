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

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.activities.SimplePlayerActivity;
import com.example.musicapp.models.SongModel;

import java.io.Serializable;
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

        int artistListLength = songModelList.get(position).getArtist().size();

        String artistText = "";

        for (int i=0; i<artistListLength; i++){
            if (i == artistListLength - 1){
                artistText += songModelList.get(position).getArtist().get(i).getName();
            }
            else {
                artistText += songModelList.get(position).getArtist().get(i).getName() + ", ";
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
            context.startActivity(intent);
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
