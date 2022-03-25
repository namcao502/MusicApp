package com.example.musicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.models.CommentModel;
import com.example.musicapp.models.GenreModel;

import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    List<CommentModel> list;

    public CommentAdapter(Context context, List<CommentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_simple_player_comment_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textViewUserEmail.setText(list.get(position).getUser_email());
        holder.textViewDetail.setText(list.get(position).getDetail());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserEmail, textViewDetail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserEmail = itemView.findViewById(R.id.textViewUserEmailItem);
            textViewDetail = itemView.findViewById(R.id.textViewDetailCommentItem);
        }
    }
}
