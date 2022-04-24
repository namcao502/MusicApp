package com.example.musicapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musicapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextEmail = findViewById(R.id.editTextEmailForgot);
        editTextPassword = findViewById(R.id.editTextPasswordForgot);
        buttonReset = findViewById(R.id.buttonResetPassword);

        editTextEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        buttonReset.setOnClickListener(view -> {
            String password = editTextPassword.getText().toString();
            if (password.isEmpty()){
                Toast.makeText(ResetPasswordActivity.this, "Vui đặt mật khẩu của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(password)
                    .addOnCompleteListener(task ->
                            Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show());
        });
    }
}