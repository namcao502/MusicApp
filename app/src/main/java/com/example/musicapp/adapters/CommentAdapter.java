package com.example.musicapp.adapters;

import android.content.Context;
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
import com.example.musicapp.models.CommentModel;
import com.example.musicapp.models.GenreModel;
import com.example.musicapp.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").whereEqualTo("id", list.get(position).getUser_id()).get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    UserModel userModel = doc.toObject(UserModel.class);
//                    Log.i("TAG502", "onBindViewHolder: " + userModel);
                    holder.textViewUserEmail.setText(userModel.getName());
                }
            }
        });
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
