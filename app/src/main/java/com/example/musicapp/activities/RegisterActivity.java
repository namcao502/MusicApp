package com.example.musicapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextName;
    TextView textViewSignIn;
    Button buttonSignUp;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        viewBinding();
        listener();
        //getSupportActionBar().hide();
    }
    private void viewBinding(){

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewSignIn = findViewById(R.id.textViewSignIn);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        firebaseAuth = FirebaseAuth.getInstance();

    }
    private void listener(){
        buttonSignUp.setOnClickListener(view -> {
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

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

            firebaseAuth.createUserWithEmailAndPassword(email, password)
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