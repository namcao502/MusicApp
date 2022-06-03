package com.example.musicapp.activities.crud;

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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.models.ArtistModel;
import com.example.musicapp.models.CountryModel;
import com.example.musicapp.models.GenreModel;
import com.example.musicapp.models.SongModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SongCRUDActivity extends AppCompatActivity {

    EditText editTextTitle, editTextArtist, editTextGenre, editTextCountry;
    ImageView imageViewImage, imageViewFileAudio;
    Button buttonAdd, buttonUpdate, buttonRemove, buttonAll;
    ListView listView;
    Spinner spinnerArtist, spinnerCountry, spinnerGenre;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;

    ArrayAdapter arrayAdapterSong, arrayAdapterArtist, arrayAdapterCountry, arrayAdapterGenre;
    List<SongModel> songModelList;
    List<ArtistModel> artistList;
    List<GenreModel> genreList;
    List<CountryModel> countryList;

    private Uri AudioFilePath, ImageFilePath;

    private final int PICK_IMAGE_REQUEST = 502;
    private final int PICK_AUDIO_REQUEST = 205;

    int currentPosition = -1;

    String audioUrl = "";
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_crud);

        ViewBinding();

        LoadAllSong();

        LoadAllSpinner();

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
                UploadImageFileToFirestore();
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
                UploadAudioFileToFirestore();
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
                        progressDialog.setMessage("Uploaded " + (int)progress + "%");
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn lại ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void UploadSongToFirebase() {

        String songTitle = editTextTitle.getText().toString();

        if (songTitle.isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
            editTextTitle.setFocusable(true);
            return;
        }

        if (editTextArtist.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng chọn tên nghệ sĩ", Toast.LENGTH_SHORT).show();
            editTextArtist.setFocusable(true);
            return;
        }

        if (editTextCountry.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng chọn tên quốc gia", Toast.LENGTH_SHORT).show();
            editTextCountry.setFocusable(true);
            return;
        }

        if (editTextGenre.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng chọn thể loại", Toast.LENGTH_SHORT).show();
            editTextGenre.setFocusable(true);
            return;
        }

        editTextArtist.setText(editTextArtist.getText().toString().substring(0, editTextArtist.getText().toString().length() - 1));
        List<String> artistTemp = Arrays.asList(editTextArtist.getText().toString().split(","));

        editTextGenre.setText(editTextGenre.getText().toString().substring(0, editTextGenre.getText().toString().length() - 1));
        List<String> genreTemp = Arrays.asList(editTextGenre.getText().toString().split(","));

        editTextCountry.setText(editTextCountry.getText().toString().substring(0, editTextCountry.getText().toString().length() - 1));
        List<String> countryTemp = Arrays.asList(editTextCountry.getText().toString().split(","));

        StorageReference imageRef = storageReference.child("Song Images/"+ songTitle);
        StorageReference songRef = storageReference.child("Songs/"+ songTitle);

        songRef.getDownloadUrl().addOnCompleteListener(task -> {
            audioUrl = task.getResult().toString();
            imageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                imageUrl = task1.getResult().toString();
                DocumentReference doc = db.collection("Song").document();
                //id
                SongModel songModel = new SongModel(doc.getId(), songTitle, audioUrl, imageUrl, artistTemp, countryTemp, genreTemp);
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Đang tải...");
                progressDialog.show();

                doc.set(songModel).addOnCompleteListener(task2 -> {
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

    }

    private void Listener() {

        spinnerArtist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editTextArtist.setText(editTextArtist.getText() + artistList.get(position).getId() + ", ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editTextCountry.setText(editTextCountry.getText() + countryList.get(position).getId() + ", ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editTextGenre.setText(editTextGenre.getText() + genreList.get(position).getId() + ", ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageViewImage.setOnClickListener(view -> {
            LoadImageFromStorage();
            UploadImageFileToFirestore();
        });

        imageViewFileAudio.setOnClickListener(view -> {
            LoadAudioFromStorage();
            UploadAudioFileToFirestore();
        });

        buttonAdd.setOnClickListener(view -> {
            UploadSongToFirebase();
            LoadAllSong();
        });

        buttonAll.setOnClickListener(view -> {
            LoadAllSong();
        });

        buttonRemove.setOnClickListener(view -> {
            if (currentPosition != -1){
                db.collection("Song").document(songModelList.get(currentPosition).getId()).delete()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(SongCRUDActivity.this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                            LoadAllSong();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(SongCRUDActivity.this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                        });
            }
            else {
                Toast.makeText(SongCRUDActivity.this, "Vui lòng chọn", Toast.LENGTH_SHORT).show();
            }

        });

        buttonUpdate.setOnClickListener(view -> {
            if (currentPosition != -1){
                String currentSongId = songModelList.get(currentPosition).getId();
                UpdateSongInFirebase(currentSongId);
                LoadAllSong();
            }
            else {
                Toast.makeText(SongCRUDActivity.this, "Vui lòng chọn", Toast.LENGTH_SHORT).show();
            }
        });


        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            String title = songModelList.get(i).getTitle();
            editTextTitle.setText(title);

            List<String> artistList = songModelList.get(i).getArtist();
            List<String> countryList = songModelList.get(i).getCountry();
            List<String> genreList = songModelList.get(i).getGenre();

            String artistText = "", countryText = "", genreText = "";

            for (String x : artistList){
                artistText += x;
            }
            editTextArtist.setText(artistText);

            for (String x : countryList){
                countryText += x;
            }
            editTextCountry.setText(countryText);

            for (String x : genreList){
                genreText += x;
            }
            editTextGenre.setText(genreText);

            Glide.with(SongCRUDActivity.this).load(songModelList.get(i).getImg_url()).into(imageViewImage);

            currentPosition = i;
        });
    }

    private void UpdateSongInFirebase(String currentSongId) {

        String songTitle = editTextTitle.getText().toString();

        if (songTitle.isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
            editTextTitle.setFocusable(true);
            return;
        }

        if (editTextArtist.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng chọn tên nghệ sĩ", Toast.LENGTH_SHORT).show();
            editTextArtist.setFocusable(true);
            return;
        }

        if (editTextCountry.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng chọn tên quốc gia", Toast.LENGTH_SHORT).show();
            editTextCountry.setFocusable(true);
            return;
        }

        if (editTextGenre.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Vui lòng chọn thể loại", Toast.LENGTH_SHORT).show();
            editTextGenre.setFocusable(true);
            return;
        }

        editTextArtist.setText(editTextArtist.getText().toString().substring(0, editTextArtist.getText().toString().length() - 1));
        List<String> artistTemp = Arrays.asList(editTextArtist.getText().toString().split(","));

        editTextGenre.setText(editTextGenre.getText().toString().substring(0, editTextGenre.getText().toString().length() - 1));
        List<String> genreTemp = Arrays.asList(editTextGenre.getText().toString().split(","));

        editTextCountry.setText(editTextCountry.getText().toString().substring(0, editTextCountry.getText().toString().length() - 1));
        List<String> countryTemp = Arrays.asList(editTextCountry.getText().toString().split(","));

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        db.collection("Song").document(currentSongId).update("title", songTitle, "artist", artistTemp, "country", countryTemp, "genre", genreTemp)
                .addOnCompleteListener(task2 -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Chỉnh sửa bài hát thành công", Toast.LENGTH_SHORT).show();
                    LoadAllSong();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Chỉnh sửa bài hát thất bại", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
    }

    private void LoadAllSong() {
        songModelList.clear();
        db.collection("Song").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    SongModel songModel = doc.toObject(SongModel.class);
                    songModelList.add(songModel);
                }
                arrayAdapterSong.notifyDataSetChanged();
            }
        });
    }

    private void LoadAllSpinner(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Artist").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    ArtistModel artistModel = doc.toObject(ArtistModel.class);
                    artistList.add(artistModel);
                }
                arrayAdapterArtist.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(SongCRUDActivity.this, "Không thể tải dữ liệu!", Toast.LENGTH_SHORT).show();
        });

        db.collection("Country").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    CountryModel countryModel = doc.toObject(CountryModel.class);
                    countryList.add(countryModel);
                }
                arrayAdapterCountry.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(SongCRUDActivity.this, "Không thể tải dữ liệu!", Toast.LENGTH_SHORT).show();
        });

        db.collection("Genre").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    GenreModel genreModel = doc.toObject(GenreModel.class);
                    genreList.add(genreModel);
                }
                arrayAdapterGenre.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(SongCRUDActivity.this, "Không thể tải dữ liệu!", Toast.LENGTH_SHORT).show();
        });

    }

    private void ViewBinding() {

        editTextTitle = findViewById(R.id.editTextSongTitleCRUD);
        editTextArtist = findViewById(R.id.editTextArtistCRUD);
        editTextGenre = findViewById(R.id.editTextGenreCRUD);
        editTextCountry = findViewById(R.id.editTextCountryCRUD);

        editTextCountry.setText("");
        editTextArtist.setText("");
        editTextGenre.setText("");

        imageViewImage = findViewById(R.id.imageViewCRUDImage);
        imageViewFileAudio = findViewById(R.id.imageViewCRUDFileAudio);

        buttonAdd = findViewById(R.id.buttonAddSongCRUD);
        buttonRemove = findViewById(R.id.buttonRemoveSongCRUD);
        buttonUpdate = findViewById(R.id.buttonUpdateSongCRUD);
        buttonAll = findViewById(R.id.buttonAllSongCRUD);

        listView = findViewById(R.id.listViewSongCRUD);

        spinnerArtist = findViewById(R.id.spinnerArtist);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        spinnerGenre = findViewById(R.id.spinnerGenre);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        songModelList = new ArrayList<>();

        artistList = new ArrayList<>();
        genreList = new ArrayList<>();
        countryList = new ArrayList<>();

        arrayAdapterSong = new ArrayAdapter(SongCRUDActivity.this, android.R.layout.simple_list_item_1, songModelList);
        listView.setAdapter(arrayAdapterSong);

        arrayAdapterArtist = new ArrayAdapter(SongCRUDActivity.this, android.R.layout.simple_spinner_item, artistList);
        arrayAdapterArtist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArtist.setAdapter(arrayAdapterArtist);

        arrayAdapterCountry = new ArrayAdapter(SongCRUDActivity.this, android.R.layout.simple_spinner_item, countryList);
        arrayAdapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(arrayAdapterCountry);

        arrayAdapterGenre = new ArrayAdapter(SongCRUDActivity.this, android.R.layout.simple_spinner_item, genreList);
        arrayAdapterGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(arrayAdapterGenre);
    }
}