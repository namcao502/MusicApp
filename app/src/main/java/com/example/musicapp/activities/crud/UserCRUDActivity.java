package com.example.musicapp.activities.crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.activities.LoginActivity;
import com.example.musicapp.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserCRUDActivity extends AppCompatActivity {
    EditText editTextName, editTextRole, editTextEmail, editTextPassword;
    Button buttonAdd, buttonUpdate, buttonRemove, buttonAll;
    ListView listView;
    ArrayAdapter arrayAdapter;
    List<UserModel> userModelList;
    FirebaseFirestore db;
    FirebaseAuth auth;

    int currentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_crud);

        ViewBinding();

        LoadAllUser();

        Listener();
    }

    private void Listener() {

        buttonAll.setOnClickListener(view -> {
            LoadAllUser();
            Reset();
        });

        buttonUpdate.setOnClickListener(view -> {
            String password = editTextPassword.getText().toString();
            String name = editTextName.getText().toString();
            String id = userModelList.get(currentPosition).getId();
            if (name.isEmpty()){
                Toast.makeText(UserCRUDActivity.this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()){
                Toast.makeText(UserCRUDActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("User").document(id).update("name", name).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(UserCRUDActivity.this, "Đổi tên thành công", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserCRUDActivity.this, "Đổi tên thất bại", Toast.LENGTH_SHORT).show();
                }
            });
            Reset();
        });

        buttonRemove.setOnClickListener(view -> {
            String userId = userModelList.get(currentPosition).getId();
            if (auth.getCurrentUser().getUid().equals(userId)) {
                Toast.makeText(UserCRUDActivity.this, "Không thể xoá tài khoản đang đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }
            //send request delete account for admin
            Reset();
        });

        buttonAdd.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String name = editTextName.getText().toString();

            if (name.isEmpty()){
                Toast.makeText(UserCRUDActivity.this, "Vui lòng nhập tên của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()){
                Toast.makeText(UserCRUDActivity.this, "Vui lòng nhập tên tài khoản của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()){
                Toast.makeText(UserCRUDActivity.this, "Vui lòng nhập mật khẩu của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(UserCRUDActivity.this, task -> {
                        if (task.isSuccessful()){

                            Toast.makeText(UserCRUDActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            UserModel userModel = new UserModel(auth.getCurrentUser().getUid(), "user", name, email, password);

                            db.collection("User").document().set(userModel).addOnCompleteListener(task1 -> {
                                Toast.makeText(UserCRUDActivity.this, "Đã thêm người dùng!", Toast.LENGTH_SHORT).show();
                            });

                            startActivity(new Intent(UserCRUDActivity.this, LoginActivity.class));

                        }
                        else {
                            Toast.makeText(UserCRUDActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });
            Reset();
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            editTextName.setText(userModelList.get(i).getName());
            editTextRole.setText(userModelList.get(i).getRole());
            editTextEmail.setText(userModelList.get(i).getEmail());
            editTextPassword.setText(userModelList.get(i).getPassword());
            currentPosition = i;
        });
    }

    private void Reset() {
        editTextName.setText("");
        editTextEmail.setText("");
        editTextRole.setText("");
        editTextPassword.setText("");
        currentPosition = -1;
    }

    private void LoadAllUser() {
        userModelList.clear();
        db.collection("User").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot doc : task.getResult()){
                    UserModel userModel = doc.toObject(UserModel.class);
                    userModelList.add(userModel);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void ViewBinding() {
        editTextEmail = findViewById(R.id.editTextUserEmailCRUD);
        editTextName = findViewById(R.id.editTextUsernameCRUD);
        editTextRole = findViewById(R.id.editTextUserRoleCRUD);
        editTextPassword = findViewById(R.id.editTextUserPasswordCRUD);

        buttonAdd = findViewById(R.id.buttonAddUserCRUD);
        buttonUpdate = findViewById(R.id.buttonUpdateUserCRUD);
        buttonRemove = findViewById(R.id.buttonRemoveUserCRUD);
        buttonAll = findViewById(R.id.buttonAllUserCRUD);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        listView = findViewById(R.id.listViewUserCRUD);
        userModelList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(UserCRUDActivity.this, android.R.layout.simple_list_item_1, userModelList);
        listView.setAdapter(arrayAdapter);
    }
}