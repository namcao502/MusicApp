package com.example.musicapp.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.Variables;
import com.example.musicapp.activities.ListSongActivity;
import com.example.musicapp.activities.SimplePlayerActivity;
import com.example.musicapp.fragments.AllSongFragment;
import com.example.musicapp.fragments.HomeFragment;
import com.example.musicapp.models.PlaylistModel;
import com.example.musicapp.models.SongModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    Context context;
    List<PlaylistModel> list;

    public PlaylistAdapter(Context context, List<PlaylistModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_playlist_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView.setText(list.get(position).getTitle());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ListSongActivity.class);
            intent.putExtra(Variables.PLAYLIST_OBJECT, (Serializable) list.get(position).getSong());
            intent.putExtra(Variables.PLAYLIST_ID, list.get(position).getId());
            intent.putExtra(Variables.PLAYLIST_TITLE, list.get(position).getTitle());
//            Log.i("TAG1", "playlist adapter: ok " + list.get(position).getSong());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_list_song_delete_change_name_song_dialog);

                Button buttonDelete, buttonChangeName;
                EditText editTextPlaylistName;

                buttonDelete = dialog.findViewById(R.id.buttonDeletePlaylist);
                buttonChangeName = dialog.findViewById(R.id.buttonChangePlaylistNameDialog);
                editTextPlaylistName = dialog.findViewById(R.id.editTextPlaylistName);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                editTextPlaylistName.setText(list.get(position).getTitle());

                buttonChangeName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String playlistName = editTextPlaylistName.getText().toString();

                        if (!(playlistName.isEmpty())){
                            db.collection("Playlist").document(auth.getUid()).collection("User")
                                    .document(list.get(position).getId())
                                    .update("title", playlistName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Đổi tên thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                        else {
                            Toast.makeText(context, "Vui lòng nhập tên mới", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        db.collection("Playlist").document(auth.getUid()).collection("User")
                                .document(list.get(position).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Xoá thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });
                dialog.show();
                return false;
            }
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
            imageView = itemView.findViewById(R.id.imageViewPlaylistItem);
            textView = itemView.findViewById(R.id.textViewTitlePlaylistItem);
        }
    }
}
