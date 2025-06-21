package vn.edu.tlu.btln9.tuvung;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainUserActivity extends AppCompatActivity {

    private Button btnLearn, btnQuiz, btnProgress, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        // Kiểm tra nếu chưa đăng nhập thì quay về Login
        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        String username = prefs.getString("username", null);
        if (username == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Lưu userId giả lập
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("userId", 1); // Giả lập userId = 1
        editor.apply();

        // Ánh xạ các nút
        btnLearn = findViewById(R.id.btnLearn);
        btnQuiz = findViewById(R.id.btnQuiz);
        btnProgress = findViewById(R.id.btnProgress);
        btnProfile = findViewById(R.id.btnProfile); // Đổi từ btnLogout sang btnProfile

        // Sự kiện click
        btnLearn.setOnClickListener(v ->
                startActivity(new Intent(this, TopicListActivity.class))
        );

        btnQuiz.setOnClickListener(v ->
                startActivity(new Intent(this, QuizListActivity.class))
        );

        btnProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ProgressActivity.class))
        );

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, UserProfileActivity.class)) // Chuyển đến hồ sơ
        );



        // Chào người dùng
        Toast.makeText(this, "Chào mừng, " + username + "!", Toast.LENGTH_SHORT).show();
    }
}
