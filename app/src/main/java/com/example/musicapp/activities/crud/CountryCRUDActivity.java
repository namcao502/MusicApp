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
import com.example.musicapp.models.CountryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CountryCRUDActivity extends AppCompatActivity {

    EditText editTextCountryNameCRUD;
    ImageView imageViewCountryCRUDImage;
    Button buttonAdd, buttonUpdate, buttonRemove;
    ListView listView;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;

    ArrayAdapter arrayAdapter;
    List<CountryModel> countryModelList;

    private Uri ImageFilePath;

    private final int PICK_IMAGE_REQUEST = 502;

    int currentPosition = -1;

    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_crud);

        ViewBinding();

        LoadAllCountry();

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
                imageViewCountryCRUDImage.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            UploadImageFileToFirestore();
        }
    }

    private void UploadImageFileToFirestore(){
        String countryName = editTextCountryNameCRUD.getText().toString();
        if(ImageFilePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Country Images/"+ countryName);
            ref.putFile(ImageFilePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnCompleteListener(task -> {
                            imageUrl = task.getResult().toString();
                        });
                        Toast.makeText(CountryCRUDActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(CountryCRUDActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

        String countryName = editTextCountryNameCRUD.getText().toString();

        if (countryName.isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên quốc gia", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference imageRef = storageReference.child("Country Images/" + countryName);

        imageRef.getDownloadUrl().addOnCompleteListener(task -> {

            imageUrl = task.getResult().toString();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            DocumentReference doc = db.collection("Country").document();
            doc.set(new CountryModel(doc.getId(), countryName, imageUrl)).addOnCompleteListener(task2 -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Thêm quốc gia thành công", Toast.LENGTH_SHORT).show();
                LoadAllCountry();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Thêm quốc gia thất bại", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                LoadAllCountry();
            });
        });
    }

    private void Reset(){
        editTextCountryNameCRUD.setText("");
        imageViewCountryCRUDImage.setImageResource(R.drawable.icons8_user_64);
    }

    private void Listener() {

        imageViewCountryCRUDImage.setOnClickListener(view -> {
            LoadImageFromStorage();
        });

        buttonAdd.setOnClickListener(view -> {
            UploadArtistToFirebase();
            LoadAllCountry();
            Reset();
        });

        buttonRemove.setOnClickListener(view -> {
            if (currentPosition != -1){
                String countryId = countryModelList.get(currentPosition).getId();
                db.collection("Country").document(countryId).delete().addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Xoá quốc gia thành công", Toast.LENGTH_SHORT).show();
                    LoadAllCountry();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Xoá quốc gia thất bại", Toast.LENGTH_SHORT).show();
                    LoadAllCountry();
                });
                Reset();
            }
            else {
                Toast.makeText(CountryCRUDActivity.this, "Vui lòng chọn", Toast.LENGTH_SHORT).show();
            }

        });

        buttonUpdate.setOnClickListener(view -> {
            if (currentPosition != -1){
                UpdateCountry(countryModelList.get(currentPosition).getId());
                LoadAllCountry();
                Reset();
            }
            else {
                Toast.makeText(CountryCRUDActivity.this, "Vui lòng chọn", Toast.LENGTH_SHORT).show();
            }
        });


        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            editTextCountryNameCRUD.setText(countryModelList.get(i).getName());

            Glide.with(CountryCRUDActivity.this).load(countryModelList.get(i).getImg_url()).into(imageViewCountryCRUDImage);

            currentPosition = i;
        });
    }

    private void UpdateCountry(String artistId){
        String countryName = editTextCountryNameCRUD.getText().toString();
        db.collection("Country").document(artistId).update("name", countryName, "img_url", imageUrl)
                .addOnCompleteListener(task -> Toast.makeText(getApplicationContext(), "Cập nhật quốc gia thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Cập nhật quốc gia thất bại", Toast.LENGTH_SHORT).show());
    }
    private void LoadAllCountry() {
        countryModelList.clear();
        db.collection("Country").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    CountryModel countryModel = doc.toObject(CountryModel.class);
                    countryModelList.add(countryModel);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void ViewBinding() {

        editTextCountryNameCRUD = findViewById(R.id.editTextCountryNameCRUD);

        imageViewCountryCRUDImage = findViewById(R.id.imageViewCountryCRUDImage);

        buttonAdd = findViewById(R.id.buttonAddCountryCRUD);
        buttonRemove = findViewById(R.id.buttonRemoveCountryCRUD);
        buttonUpdate = findViewById(R.id.buttonUpdateCountryCRUD);

        listView = findViewById(R.id.listViewCountryCRUD);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        countryModelList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(CountryCRUDActivity.this, android.R.layout.simple_list_item_1, countryModelList);
        listView.setAdapter(arrayAdapter);
    }
}