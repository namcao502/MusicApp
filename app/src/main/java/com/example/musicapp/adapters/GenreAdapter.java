package com.example.musicapp.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.musicapp.activities.ListSongActivity;
import com.example.musicapp.models.GenreModel;

import java.util.List;


public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    Context context;
    List<GenreModel> list;

    public GenreAdapter(Context context, List<GenreModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_genre_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(list.get(position).getImg_url()).into(holder.imageView);
        holder.textView.setText(list.get(position).getName());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ListSongActivity.class);
            intent.putExtra(Variables.INTENT_TYPE, "genreIntent");
            intent.putExtra(Variables.GENRE_TITLE, list.get(position).getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewGenreItem);
            textView = itemView.findViewById(R.id.textViewGenreItem);
        }
    }
}
