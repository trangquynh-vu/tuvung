package vn.edu.tlu.btln9.tuvung;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvEmail, tvRole, tvOverallProgress;
    private Button btnProgress, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Ánh xạ view
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvOverallProgress = findViewById(R.id.tvOverallProgress);
        btnProgress = findViewById(R.id.btnProgress);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        String email = prefs.getString("username", null);
        String userKey = prefs.getString("userKey", null);

        if (userKey == null || email == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị email
        tvEmail.setText("Email: " + email);

        // Lấy dữ liệu người dùng từ Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userKey);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.child("role").getValue(String.class);
                tvRole.setText("Vai trò: " + (role != null ? role : "Không rõ"));

                // Lấy tiến độ tổng thể
                DataSnapshot progressSnap = snapshot.child("progress");
                if (progressSnap.exists()) {
                    Integer overall = progressSnap.child("overallProgress").getValue(Integer.class);
                    tvOverallProgress.setText("Tiến độ tổng thể: " + (overall != null ? overall + "%" : "0%"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Lỗi khi tải hồ sơ!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xem tiến độ chi tiết
        btnProgress.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, ProgressActivity.class);
            startActivity(intent);
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
