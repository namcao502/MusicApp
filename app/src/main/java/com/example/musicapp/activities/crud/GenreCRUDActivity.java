package com.example.musicapp.activities.crud;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.models.GenreModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenreCRUDActivity extends AppCompatActivity {

    EditText editTextGenreNameCRUD;
    ImageView imageViewGenreCRUDImage;
    Button buttonAdd, buttonUpdate, buttonRemove;
    ListView listView;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;

    ArrayAdapter arrayAdapter;
    List<GenreModel> genreModelList;

    private Uri ImageFilePath;

    private final int PICK_IMAGE_REQUEST = 502;

    int currentPosition = -1;

    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_crud);

        ViewBinding();

        LoadAllArtist();

        Listener();
    }

    private void LoadImageFromStorage() {
        Intent intent = new Intent();
        intent.setType("Genre Images/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null ) {
            ImageFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageFilePath);
                imageViewGenreCRUDImage.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            UploadImageFileToFirestore();
        }
    }

    private void UploadImageFileToFirestore(){
        String artistName = editTextGenreNameCRUD.getText().toString();
        if(ImageFilePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Genre Images/"+ artistName);
            ref.putFile(ImageFilePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnCompleteListener(task -> {
                            imageUrl = task.getResult().toString();
                        });
                        Toast.makeText(GenreCRUDActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(GenreCRUDActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int)progress + "%");
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn lại ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void UploadArtistToFirebase() {

        String genreName = editTextGenreNameCRUD.getText().toString();

        if (genreName.isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên thể loại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPosition == -1){
            Toast.makeText(getApplicationContext(), "Vui lòng chọn thể loại", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference imageRef = storageReference.child("Genre Images/" + genreName);

        imageRef.getDownloadUrl().addOnCompleteListener(task -> {

            imageUrl = task.getResult().toString();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            DocumentReference doc = db.collection("Genre").document();
            doc.set(new GenreModel(doc.getId(), genreName, imageUrl)).addOnCompleteListener(task2 -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Thêm thể loại thành công", Toast.LENGTH_SHORT).show();
                LoadAllArtist();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Thêm thể lại thất bại thất bại", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                LoadAllArtist();
            });
        });
    }

    private void Reset(){
        editTextGenreNameCRUD.setText("");
        imageViewGenreCRUDImage.setImageResource(R.drawable.icons8_user_64);
    }

    private void Listener() {

        imageViewGenreCRUDImage.setOnClickListener(view -> {
            LoadImageFromStorage();
        });

        buttonAdd.setOnClickListener(view -> {
            UploadArtistToFirebase();
            LoadAllArtist();
            Reset();
        });

        buttonRemove.setOnClickListener(view -> {
            String genreId = genreModelList.get(currentPosition).getId();
            db.collection("Genre").document(genreId).delete().addOnSuccessListener(unused -> {
                Toast.makeText(getApplicationContext(), "Xoá thể loại thành công", Toast.LENGTH_SHORT).show();
                LoadAllArtist();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Xoá thể loại thất bại", Toast.LENGTH_SHORT).show();
                LoadAllArtist();
            });
            Reset();
        });

        buttonUpdate.setOnClickListener(view -> {
            UpdateArtist(genreModelList.get(currentPosition).getId());
            LoadAllArtist();
            Reset();
        });


        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            editTextGenreNameCRUD.setText(genreModelList.get(i).getName());

            Glide.with(GenreCRUDActivity.this).load(genreModelList.get(i).getImg_url()).into(imageViewGenreCRUDImage);

            currentPosition = i;
        });
    }

    private void UpdateArtist(String artistId){
        String genreName = editTextGenreNameCRUD.getText().toString();
        db.collection("Genre").document(artistId).update("name", genreName, "img_url", imageUrl)
                .addOnCompleteListener(task -> Toast.makeText(getApplicationContext(), "Cập nhật thể loại thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Cập nhật thể loại thất bại", Toast.LENGTH_SHORT).show());
    }
    private void LoadAllArtist() {
        genreModelList.clear();
        db.collection("Genre").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    GenreModel genreModel = doc.toObject(GenreModel.class);
                    genreModelList.add(genreModel);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void ViewBinding() {

        editTextGenreNameCRUD = findViewById(R.id.editTextGenreNameCRUD);

        imageViewGenreCRUDImage = findViewById(R.id.imageViewGenreCRUDImage);

        buttonAdd = findViewById(R.id.buttonAddGenreCRUD);
        buttonRemove = findViewById(R.id.buttonRemoveGenreCRUD);
        buttonUpdate = findViewById(R.id.buttonUpdateGenreCRUD);

        listView = findViewById(R.id.listViewGenreCRUD);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        genreModelList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(GenreCRUDActivity.this, android.R.layout.simple_list_item_1, genreModelList);
        listView.setAdapter(arrayAdapter);
    }
}