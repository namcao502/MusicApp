package com.example.musicapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.adapters.SongAdapter;
import com.example.musicapp.models.ArtistModel;
import com.example.musicapp.models.ContributorModel;
import com.example.musicapp.models.CountryModel;
import com.example.musicapp.models.GenreModel;
import com.example.musicapp.models.SongModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore db;
    FirebaseAuth auth;

    ImageView imageViewUpload, imageViewUploadFileAudio;
    EditText editTextSongTitleUpload, editTextArtistUpload, editTextCountryUpload, editTextGenreUpload;

    Button buttonUpload;

    private Uri AudioFilePath, ImageFilePath;

    private final int PICK_IMAGE_REQUEST = 502;
    private final int PICK_AUDIO_REQUEST = 205;

    String audioUrl = "";
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        ViewBinding();

        //test
//        editTextSongTitleUpload.setText("The Spectre");
//        editTextGenreUpload.setText("EDM");
//        editTextArtistUpload.setText("Alan Walker");
//        editTextCountryUpload.setText("US-UK");

        Listener();

    }

    private void Listener() {

        imageViewUpload.setOnClickListener(view -> {
            LoadImageFromStorage();
            UploadImageFileToFirestore();
        });

        imageViewUploadFileAudio.setOnClickListener(view -> {
            LoadAudioFromStorage();
            UploadAudioFileToFirestore();
        });

        buttonUpload.setOnClickListener(view -> uploadSongInfoToFirebase());
    }

    private void ViewBinding() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        imageViewUpload = findViewById(R.id.imageViewUploadImage);
        imageViewUploadFileAudio = findViewById(R.id.imageViewUploadFileAudio);
        editTextSongTitleUpload = findViewById(R.id.editTextSongTitleUpload);
        editTextArtistUpload = findViewById(R.id.editTextArtistUpload);
        editTextCountryUpload = findViewById(R.id.editTextCountryUpload);
        editTextGenreUpload = findViewById(R.id.editTextGenreUpload);
        buttonUpload = findViewById(R.id.buttonUpload);
    }

    private void LoadImageFromStorage() {
        Intent intent = new Intent();
        intent.setType("Song Images/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void LoadAudioFromStorage() {
        Intent intent = new Intent();
        intent.setType("Songs/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), PICK_AUDIO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null ) {
            ImageFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageFilePath);
                imageViewUpload.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            AudioFilePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                imageViewUploadFileAudio.setImageResource(R.drawable.icons8_ok_128);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void UploadAudioFileToFirestore(){
        String songTitle = editTextSongTitleUpload.getText().toString();
        if(AudioFilePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Songs/"+ songTitle);
            ref.putFile(AudioFilePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnCompleteListener(task -> {
                            audioUrl = task.getResult().toString();
                        });
                        Toast.makeText(UploadActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(UploadActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn lại bài hát", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void UploadImageFileToFirestore(){
        String songTitle = editTextSongTitleUpload.getText().toString();
        if(ImageFilePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Song Images/"+ songTitle);
            ref.putFile(ImageFilePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnCompleteListener(task -> {
                           imageUrl = task.getResult().toString();
                        });
                        Toast.makeText(UploadActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(UploadActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn lại ảnh", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    private void uploadSongInfoToFirebase() {

        UploadAudioFileToFirestore();
        UploadImageFileToFirestore();

        String songTitle = editTextSongTitleUpload.getText().toString();
        List<ArtistModel> artistList = new ArrayList<>();
        List<GenreModel> genreList = new ArrayList<>();
        List<CountryModel> countryList = new ArrayList<>();

        if (songTitle.isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
            editTextSongTitleUpload.setFocusable(true);
            return;
        }

        if (editTextArtistUpload.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
            editTextArtistUpload.setFocusable(true);
            return;
        }

        if (editTextCountryUpload.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên quốc gia", Toast.LENGTH_SHORT).show();
            editTextCountryUpload.setFocusable(true);
            return;
        }

        if (editTextGenreUpload.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập thể loại", Toast.LENGTH_SHORT).show();
            editTextGenreUpload.setFocusable(true);
            return;
        }

        String[] artistTemp = editTextArtistUpload.getText().toString().split(",");
        for (String x : artistTemp){
            ArtistModel artistModel = new ArtistModel(x, "");
            artistList.add(artistModel);
        }

        String[] genreTemp = editTextGenreUpload.getText().toString().split(",");
        for (String x : genreTemp){
            GenreModel genreModel = new GenreModel(x, "");
            genreList.add(genreModel);
        }

        String[] countryTemp = editTextCountryUpload.getText().toString().split(",");
        for (String x : countryTemp){
            CountryModel countryModel = new CountryModel(x, "");
            countryList.add(countryModel);
        }

        List<String> preparedArtist = new ArrayList<>();
        for (ArtistModel x : artistList){
            preparedArtist.add(x.getName());
        }

        List<String> preparedCountry = new ArrayList<>();
        for (CountryModel x : countryList){
            preparedCountry.add(x.getName());
        }

        List<String> preparedGenre = new ArrayList<>();
        for (GenreModel x : genreList){
            preparedGenre.add(x.getName());
        }

        StorageReference imageRef = storageReference.child("Song Images/"+ songTitle);
        StorageReference songRef = storageReference.child("Songs/"+ songTitle);
        songRef.getDownloadUrl().addOnCompleteListener(task -> {
            audioUrl = task.getResult().toString();
            imageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                imageUrl = task1.getResult().toString();

                SongModel songModel = new SongModel(songTitle, audioUrl, imageUrl, preparedArtist, preparedCountry, preparedGenre);
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                db.collection("Song").document().set(songModel).addOnCompleteListener(task2 -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Đóng góp bài hát thành công", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Đóng góp bài hát thất bại", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            });
        });
        //                db.collection("Artist").document().set(artistList);
//                db.collection("Genre").document().set(genreList);
//                db.collection("Country").document().set(countryList);
//        ContributorModel contributorModel = new ContributorModel(songTitle, auth.getUid(), auth.getCurrentUser().getEmail());
//        db.collection("Contributor").document().set(contributorModel);
    }
}