package vn.edu.tlu.btln9.tuvung;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProgressActivity extends AppCompatActivity {

    private TextView tvTopicsLearned, tvQuizzesCompleted, tvAverageScore, tvOverallProgress;
    private ProgressBar progressBarOverall;
    private Button btnContinueLearning;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // Ánh xạ
        tvTopicsLearned = findViewById(R.id.tvTopicsLearned);
        tvQuizzesCompleted = findViewById(R.id.tvQuizzesCompleted);
        tvAverageScore = findViewById(R.id.tvAverageScore);
        tvOverallProgress = findViewById(R.id.tvOverallProgress);
        progressBarOverall = findViewById(R.id.progressBarOverall);
        btnContinueLearning = findViewById(R.id.btnContinueLearning);

        // Tham chiếu tới bảng "users"
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tìm user có userId tương ứng và lấy dữ liệu progress
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    Long dbUserId = userSnap.child("userId").getValue(Long.class);
                    if (dbUserId != null && dbUserId == userId) {
                        found = true;
                        DataSnapshot progressSnap = userSnap.child("progress");

                        int topicsLearned = progressSnap.child("topicsLearned").getValue(Integer.class);
                        int quizzesCompleted = progressSnap.child("quizzesCompleted").getValue(Integer.class);
                        double averageScore = progressSnap.child("averageScore").getValue(Double.class);
                        int overallProgress = progressSnap.child("overallProgress").getValue(Integer.class);

                        tvTopicsLearned.setText("Chủ đề đã học: " + topicsLearned + "/10");
                        tvQuizzesCompleted.setText("Quiz hoàn thành: " + quizzesCompleted);
                        tvAverageScore.setText("Điểm trung bình: " + averageScore);
                        tvOverallProgress.setText(overallProgress + "% đã hoàn thành");
                        progressBarOverall.setProgress(overallProgress);
                        break;
                    }
                }

                if (!found) {
                    Toast.makeText(ProgressActivity.this, "Không tìm thấy tiến độ người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProgressActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnContinueLearning.setOnClickListener(v -> {
            Toast.makeText(this, "Tiếp tục học nào!", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, TopicListActivity.class));
        });
    }
}
