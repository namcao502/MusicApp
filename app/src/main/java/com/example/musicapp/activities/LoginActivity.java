package com.example.musicapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.fragments.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    TextView textViewSignUp;
    Button buttonSignIn, buttonSignInWithGoogle;
    private FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewBinding();
        listener();
        //getSupportActionBar().hide();
    }

    private void listener() {
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                if (email.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập tên tài khoản của bạn!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu của bạn!", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại hoặc đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        buttonSignInWithGoogle.setOnClickListener(view -> {
//            GoogleSignInActivity googleSignInActivity = new GoogleSignInActivity();
//            googleSignInActivity.signIn();
            Toast.makeText(LoginActivity.this, "Sẽ ra mắt sau!", Toast.LENGTH_SHORT).show();
        });
        textViewSignUp.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void viewBinding(){
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        buttonSignIn = findViewById(R.id.buttonSignUp);
        buttonSignInWithGoogle = findViewById(R.id.buttonSignInWithGoogle);

        editTextEmail.setText("nam502@gmail.com");
        editTextPassword.setText("nam502");

        sharedPreferences = getSharedPreferences("FirstTimeScreen", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);
        if (isFirstTime){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();

            Intent intent = new Intent(LoginActivity.this, FirstTimeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}