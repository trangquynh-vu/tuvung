package vn.edu.tlu.btln9.tuvung;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnRegister, btnGoLogin;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ giao diện
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoLogin = findViewById(R.id.btnGoLogin);

        // Kết nối đến Firebase Realtime Database, bảng "users"
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Xử lý nút Đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });

        // Chuyển về màn hình đăng nhập
        btnGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void handleRegister() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }

        // Tạo userId bằng push().getKey()
        String userId = usersRef.push().getKey();
        if (userId == null) {
            Toast.makeText(this, "Lỗi hệ thống khi tạo ID người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gán role mặc định là "user"
        String role = "user";

        // Tạo đối tượng người dùng đầy đủ
        User user = new User(userId, email, password, role);

        // Lưu vào Firebase
        usersRef.child(userId).setValue(user)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi đăng ký: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
