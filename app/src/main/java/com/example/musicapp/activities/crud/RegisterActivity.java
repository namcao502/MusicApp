package com.example.musicapp.activities.crud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.activities.LoginActivity;
import com.example.musicapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextName;
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
        editTextName = findViewById(R.id.editTextName);
        textViewSignIn = findViewById(R.id.textViewSignIn);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }
    private void listener(){
        buttonSignUp.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String name = editTextName.getText().toString();

            if (name.isEmpty()){
                Toast.makeText(RegisterActivity.this, "Vui lòng nhập tên của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }
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
                            UserModel userModel = new UserModel(auth.getCurrentUser().getUid(), "user", name, email, password);
                            db.collection("User").document().set(userModel).addOnCompleteListener(task1 -> {
                                Toast.makeText(RegisterActivity.this, "Đã thêm người dùng!", Toast.LENGTH_SHORT).show();
                            });
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        textViewSignIn.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }
}