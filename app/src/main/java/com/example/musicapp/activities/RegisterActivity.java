package com.example.musicapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.adapters.CommentAdapter;
import com.example.musicapp.models.CommentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    TextView textViewSignIn;
    Button buttonSignUp;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        viewBinding();
        listener();
        //getSupportActionBar().hide();
    }
    private void viewBinding(){

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewSignIn = findViewById(R.id.textViewSignIn);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

//        CommentModel commentModel = new CommentModel(auth.getUid(), "Unity", "Hay đấy");
//
//        String userEmail = auth.getCurrentUser().getEmail();
//        commentModel.setUser_email(userEmail);
//
//        db.collection("Comment").document(auth.getUid()).collection("User")
//                .document().set(commentModel).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(RegisterActivity.this, "Đã thêm bình luận!", Toast.LENGTH_SHORT).show();
//            }
//        });

    }
    private void listener(){
        buttonSignUp.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (email.isEmpty()){
                Toast.makeText(RegisterActivity.this, "Vui lòng nhập tên tài khoản của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()){
                Toast.makeText(RegisterActivity.this, "Vui lòng nhập mật khẩu của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        textViewSignIn.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }
}