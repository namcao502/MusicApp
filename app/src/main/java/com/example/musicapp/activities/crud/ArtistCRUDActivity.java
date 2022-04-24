package com.example.musicapp.activities.crud;

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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.models.ArtistModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArtistCRUDActivity extends AppCompatActivity {

    EditText editTextArtistNameCRUD;
    ImageView imageViewArtistCRUDImage;
    Button buttonAdd, buttonUpdate, buttonRemove;
    ListView listView;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;

    ArrayAdapter arrayAdapter;
    List<ArtistModel> artistModelList;

    private Uri ImageFilePath;

    private final int PICK_IMAGE_REQUEST = 502;

    int currentPosition = -1;

    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_crud);

        ViewBinding();

        LoadAllArtist();

        Listener();

    }

    private void LoadImageFromStorage() {
        Intent intent = new Intent();
        intent.setType("Artist Images/*");
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
                imageViewArtistCRUDImage.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            UploadImageFileToFirestore();
        }
    }


    private void UploadImageFileToFirestore(){
        String artistName = editTextArtistNameCRUD.getText().toString();
        if(ImageFilePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Artist Images/"+ artistName);
            ref.putFile(ImageFilePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnCompleteListener(task -> {
                            imageUrl = task.getResult().toString();
                        });
                        Toast.makeText(ArtistCRUDActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ArtistCRUDActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

        String artistName = editTextArtistNameCRUD.getText().toString();

        if (artistName.isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPosition == -1){
            Toast.makeText(getApplicationContext(), "Vui lòng chọn nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference imageRef = storageReference.child("Artist Images/" + artistName);

        imageRef.getDownloadUrl().addOnCompleteListener(task -> {

            imageUrl = task.getResult().toString();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            DocumentReference doc = db.collection("Artist").document();
            doc.set(new ArtistModel(doc.getId(), artistName, imageUrl)).addOnCompleteListener(task2 -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Thêm nghệ sĩ thành công", Toast.LENGTH_SHORT).show();
                LoadAllArtist();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Thêm nghệ sĩ thất bại thất bại", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                LoadAllArtist();
            });
        });
    }

    private void Reset(){
        editTextArtistNameCRUD.setText("");
        imageViewArtistCRUDImage.setImageResource(R.drawable.icons8_user_64);
    }

    private void Listener() {

        imageViewArtistCRUDImage.setOnClickListener(view -> {
            LoadImageFromStorage();
        });

        buttonAdd.setOnClickListener(view -> {
            UploadArtistToFirebase();
            LoadAllArtist();
            Reset();
        });

        buttonRemove.setOnClickListener(view -> {
            String artistId = artistModelList.get(currentPosition).getId();
            db.collection("Artist").document(artistId).delete().addOnSuccessListener(unused -> {
                Toast.makeText(getApplicationContext(), "Xoá nghệ sĩ thành công", Toast.LENGTH_SHORT).show();
                LoadAllArtist();
            }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Xoá nghệ sĩ thất bại", Toast.LENGTH_SHORT).show();
                    LoadAllArtist();
            });
            Reset();
        });

        buttonUpdate.setOnClickListener(view -> {
            UpdateArtist(artistModelList.get(currentPosition).getId());
            LoadAllArtist();
            Reset();
        });


        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            editTextArtistNameCRUD.setText(artistModelList.get(i).getName());

            Glide.with(ArtistCRUDActivity.this).load(artistModelList.get(i).getImg_url()).into(imageViewArtistCRUDImage);

            currentPosition = i;
        });
    }

    private void UpdateArtist(String artistId){
        String artistName = editTextArtistNameCRUD.getText().toString();
        db.collection("Artist").document(artistId).update("name", artistName, "img_url", imageUrl)
                .addOnCompleteListener(task -> Toast.makeText(getApplicationContext(), "Cập nhật nghệ sĩ thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Cập nhật nghệ sĩ thất bại", Toast.LENGTH_SHORT).show());
    }

    private void LoadAllArtist() {
        artistModelList.clear();
        db.collection("Artist").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    ArtistModel artistModel = doc.toObject(ArtistModel.class);
                    artistModelList.add(artistModel);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void ViewBinding() {

        editTextArtistNameCRUD = findViewById(R.id.editTextArtistNameCRUD);

        imageViewArtistCRUDImage = findViewById(R.id.imageViewArtistCRUDImage);

        buttonAdd = findViewById(R.id.buttonAddArtistCRUD);
        buttonRemove = findViewById(R.id.buttonRemoveArtistCRUD);
        buttonUpdate = findViewById(R.id.buttonUpdateArtistCRUD);

        listView = findViewById(R.id.listViewArtistCRUD);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        artistModelList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(ArtistCRUDActivity.this, android.R.layout.simple_list_item_1, artistModelList);
        listView.setAdapter(arrayAdapter);
    }
}