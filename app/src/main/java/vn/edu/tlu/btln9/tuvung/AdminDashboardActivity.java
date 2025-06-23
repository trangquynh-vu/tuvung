package vn.edu.tlu.btln9.tuvung;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnManageUsers, btnManageTopics, btnManageVocabulary, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Ánh xạ các nút từ XML
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageTopics = findViewById(R.id.btnManageTopics);
        btnManageVocabulary = findViewById(R.id.btnManageVocabulary);
        btnLogout = findViewById(R.id.btnLogout);

        // Sự kiện nút "Quản lý người dùng"
        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageUsersActivity.class);
            startActivity(intent);
        });

        // Sự kiện nút "Quản lý chủ đề"
        btnManageTopics.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageTopicsActivity.class);
            startActivity(intent);
        });
        // Sự kiện nút "Quản lý tu vung"
        btnManageVocabulary.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageVocabularyActivity.class);
            startActivity(intent);
        });

        // Sự kiện nút "Đăng xuất"
        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

