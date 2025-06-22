package vn.edu.tlu.btln9.tuvung;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ProgressActivity extends AppCompatActivity {

    private TextView tvTopicsLearned, tvQuizzesCompleted, tvAverageScore, tvOverallProgress;
    private ProgressBar progressBar;
    private Button btnBack;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
       // int userId = prefs.getInt("userId", -1);

      //  if (userId == -1) {
       //     Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
        //    finish();
        //    return;
       // }
        String userKey = prefs.getString("userKey", null);

        if (userKey == null) {
            Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các view
        tvTopicsLearned = findViewById(R.id.tvTopicsLearned);
        tvQuizzesCompleted = findViewById(R.id.tvQuizzesCompleted);
        tvAverageScore = findViewById(R.id.tvAverageScore);
        tvOverallProgress = findViewById(R.id.tvOverallProgress);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);

        // Firebase Database Reference
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Truy cập vào user theo userId (giả sử userId là key của mỗi user)
        //usersRef.child(String.valueOf(userId)).child("progress")
        usersRef.child(userKey).child("progress")

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Progress progress = snapshot.getValue(Progress.class);

                            if (progress != null) {
                                tvTopicsLearned.setText("Chủ đề đã học: " + progress.getTopicsLearned());
                                tvQuizzesCompleted.setText("Bài kiểm tra đã hoàn thành: " + progress.getQuizzesCompleted());
                                tvAverageScore.setText("Điểm trung bình: " + String.format(Locale.US, "%.2f", progress.getAverageScore()));
                                tvOverallProgress.setText("Tiến độ tổng thể: " + progress.getOverallProgress() + "%");
                                progressBar.setProgress(progress.getOverallProgress());
                            }
                        } else {
                            Toast.makeText(ProgressActivity.this, "Chưa có dữ liệu tiến độ!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProgressActivity.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                });

        // Quay lại MainUserActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProgressActivity.this, MainUserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
