package vn.edu.tlu.btln9.tuvung;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnGoRegister;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        btnLogin.setOnClickListener(v -> handleLogin());

        btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void handleLogin() {
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

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);
                    String role = userSnapshot.child("role").getValue(String.class);
                    Long userId = userSnapshot.child("userId").getValue(Long.class);  // 👈 lấy userId

                    if (email.equals(dbEmail) && password.equals(dbPassword)) {
                        found = true;

                        // ✅ Lưu vào SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("username", dbEmail);
                        editor.putInt("userId", userId != null ? userId.intValue() : -1); // 👈 lưu userId
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        if ("admin".equalsIgnoreCase(role)) {
                            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
                        }

                        finish();
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
