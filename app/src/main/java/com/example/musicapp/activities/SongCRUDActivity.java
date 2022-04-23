package com.example.musicapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.models.ArtistModel;
import com.example.musicapp.models.CountryModel;
import com.example.musicapp.models.GenreModel;
import com.example.musicapp.models.SongModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SongCRUDActivity extends AppCompatActivity {

    EditText editTextTitle, editTextArtist, editTextGenre, editTextCountry;
    ImageView imageViewImage, imageViewFileAudio;
    Button buttonAdd, buttonUpdate, buttonRemove;
    ListView listView;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;

    ArrayAdapter arrayAdapter;
    List<SongModel> songModelList;

    private Uri AudioFilePath, ImageFilePath;

    private final int PICK_IMAGE_REQUEST = 502;
    private final int PICK_AUDIO_REQUEST = 205;

    int currentPosition;

    String audioUrl = "";
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_crud);

        ViewBinding();

        LoadAllSong();

        Listener();

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
                imageViewImage.setImageBitmap(bitmap);
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
                imageViewFileAudio.setImageResource(R.drawable.icons8_ok_128);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void UploadAudioFileToFirestore(){
        String songTitle = editTextTitle.getText().toString();
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
                        Toast.makeText(SongCRUDActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(SongCRUDActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int)progress+"%");
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn lại bài hát", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void UploadImageFileToFirestore(){
        String songTitle = editTextTitle.getText().toString();
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
                        Toast.makeText(SongCRUDActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(SongCRUDActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn lại ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void UploadSongToFirebase() {

        UploadAudioFileToFirestore();
        UploadImageFileToFirestore();

        String songTitle = editTextTitle.getText().toString();
        List<ArtistModel> artistList = new ArrayList<>();
        List<GenreModel> genreList = new ArrayList<>();
        List<CountryModel> countryList = new ArrayList<>();

        if (songTitle.isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
            editTextTitle.setFocusable(true);
            return;
        }

        if (editTextArtist.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
            editTextArtist.setFocusable(true);
            return;
        }

        if (editTextCountry.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên quốc gia", Toast.LENGTH_SHORT).show();
            editTextCountry.setFocusable(true);
            return;
        }

        if (editTextGenre.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập thể loại", Toast.LENGTH_SHORT).show();
            editTextGenre.setFocusable(true);
            return;
        }

        String[] artistTemp = editTextArtist.getText().toString().split(",");
        for (String x : artistTemp){
            ArtistModel artistModel = new ArtistModel(x, "");
            artistList.add(artistModel);
        }

        String[] genreTemp = editTextGenre.getText().toString().split(",");
        for (String x : genreTemp){
            GenreModel genreModel = new GenreModel(x, "");
            genreList.add(genreModel);
        }

        String[] countryTemp = editTextCountry.getText().toString().split(",");
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

                //id
                SongModel songModel = new SongModel(FieldPath.documentId().toString(), songTitle, audioUrl, imageUrl, preparedArtist, preparedCountry, preparedGenre);
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
//        db.collection("Artist").document().set(artistList);
//        db.collection("Genre").document().set(genreList);
//        db.collection("Country").document().set(countryList);
//        ContributorModel contributorModel = new ContributorModel(songTitle, auth.getUid(), auth.getCurrentUser().getEmail());
//        db.collection("Contributor").document().set(contributorModel);
    }

    private void Listener() {

        imageViewImage.setOnClickListener(view -> {
            LoadImageFromStorage();
        });

        imageViewFileAudio.setOnClickListener(view -> {
            LoadAudioFromStorage();
        });

        buttonAdd.setOnClickListener(view -> {
            UploadSongToFirebase();
            LoadAllSong();
        });

        buttonRemove.setOnClickListener(view -> {
            db.collection("Song").document(songModelList.get(currentPosition).getId()).delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(SongCRUDActivity.this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                        LoadAllSong();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(SongCRUDActivity.this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                    });
        });

        buttonUpdate.setOnClickListener(view -> {
            String currentSongId = songModelList.get(currentPosition).getId();
            UpdateSongInFirebase(currentSongId);
            LoadAllSong();
        });


        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String title = songModelList.get(i).getTitle();
            editTextTitle.setText(title);

            int artistListLength = songModelList.get(i).getArtist().size();
            String artistText = "";
            for (int j=0; j<artistListLength; j++){
                if (j == artistListLength - 1){
                    artistText += songModelList.get(i).getArtist().get(j);
                }
                else {
                    artistText += songModelList.get(i).getArtist().get(j) + ", ";
                }
            }
            editTextArtist.setText(artistText);

            int countryListLength = songModelList.get(i).getCountry().size();
            String countryText = "";
            for (int j=0; j<countryListLength; j++){
                if (j == countryListLength - 1){
                    countryText += songModelList.get(i).getCountry().get(j);
                }
                else {
                    countryText += songModelList.get(i).getCountry().get(j) + ", ";
                }
            }
            editTextCountry.setText(countryText);

            int genreListLength = songModelList.get(i).getGenre().size();
            String genreText = "";
            for (int j=0; j<genreListLength; j++){
                if (j == genreListLength - 1){
                    genreText += songModelList.get(i).getGenre().get(j);
                }
                else {
                    genreText += songModelList.get(i).getGenre().get(j) + ", ";
                }
            }
            editTextGenre.setText(genreText);

            Glide.with(SongCRUDActivity.this).load(songModelList.get(i).getImg_url()).into(imageViewImage);

            currentPosition = i;
        });
    }

    private void UpdateSongInFirebase(String currentSongId) {
        UploadAudioFileToFirestore();
        UploadImageFileToFirestore();

        String songTitle = editTextTitle.getText().toString();
        List<ArtistModel> artistList = new ArrayList<>();
        List<GenreModel> genreList = new ArrayList<>();
        List<CountryModel> countryList = new ArrayList<>();

        if (songTitle.isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
            editTextTitle.setFocusable(true);
            return;
        }

        if (editTextArtist.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
            editTextArtist.setFocusable(true);
            return;
        }

        if (editTextCountry.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên quốc gia", Toast.LENGTH_SHORT).show();
            editTextCountry.setFocusable(true);
            return;
        }

        if (editTextGenre.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập thể loại", Toast.LENGTH_SHORT).show();
            editTextGenre.setFocusable(true);
            return;
        }

        String[] artistTemp = editTextArtist.getText().toString().split(",");
        for (String x : artistTemp){
            ArtistModel artistModel = new ArtistModel(x, "");
            artistList.add(artistModel);
        }

        String[] genreTemp = editTextGenre.getText().toString().split(",");
        for (String x : genreTemp){
            GenreModel genreModel = new GenreModel(x, "");
            genreList.add(genreModel);
        }

        String[] countryTemp = editTextCountry.getText().toString().split(",");
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

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                db.collection("Song").document(currentSongId).update("title", songTitle, "url", audioUrl, "img_url", imageUrl, "artist", preparedArtist, "country", preparedCountry, "genre", preparedGenre)
                        .addOnCompleteListener(task2 -> {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Chỉnh sửa bài hát thành công", Toast.LENGTH_SHORT).show();
                            LoadAllSong();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Chỉnh sửa bài hát thất bại", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            });
        });
//        db.collection("Artist").document().set(artistList);
//        db.collection("Genre").document().set(genreList);
//        db.collection("Country").document().set(countryList);
//        ContributorModel contributorModel = new ContributorModel(songTitle, auth.getUid(), auth.getCurrentUser().getEmail());
//        db.collection("Contributor").document().set(contributorModel);
    }

    private void LoadAllSong() {
        songModelList.clear();
        db.collection("Song").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    SongModel songModel = doc.toObject(SongModel.class);
                    songModelList.add(songModel);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void ViewBinding() {

        editTextTitle = findViewById(R.id.editTextSongTitleCRUD);
        editTextArtist = findViewById(R.id.editTextArtistCRUD);
        editTextGenre = findViewById(R.id.editTextGenreCRUD);
        editTextCountry = findViewById(R.id.editTextCountryCRUD);

        imageViewImage = findViewById(R.id.imageViewCRUDImage);
        imageViewFileAudio = findViewById(R.id.imageViewCRUDFileAudio);

        buttonAdd = findViewById(R.id.buttonAddSongCRUD);
        buttonRemove = findViewById(R.id.buttonRemoveSongCRUD);
        buttonUpdate = findViewById(R.id.buttonUpdateSongCRUD);

        listView = findViewById(R.id.listViewSongCRUD);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        songModelList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(SongCRUDActivity.this, android.R.layout.simple_list_item_1, songModelList);
        listView.setAdapter(arrayAdapter);
    }
}