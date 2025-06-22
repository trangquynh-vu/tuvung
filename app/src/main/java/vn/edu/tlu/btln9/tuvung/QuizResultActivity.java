package vn.edu.tlu.btln9.tuvung;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class QuizResultActivity extends AppCompatActivity {

    private TextView tvResult, tvTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        tvResult = findViewById(R.id.tvResult);
        tvTopic = findViewById(R.id.tvTopic);

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);
        String topicTitle = getIntent().getStringExtra("topicTitle");

        tvTopic.setText("Kết quả quiz: " + (topicTitle != null ? topicTitle : ""));
        tvResult.setText("Bạn trả lời đúng " + score + "/" + total + " câu hỏi.");

        // ✅ Lưu quiz history và cập nhật tiến độ học
        saveQuizHistoryAndProgress(topicTitle, score, total);

        // ✅ Nút quay lại QuizListActivity
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResultActivity.this, QuizListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void saveQuizHistoryAndProgress(String topicTitle, int score, int total) {
        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        String userKey = prefs.getString("userKey", null);

        if (userKey == null) {
            Toast.makeText(this, "Không xác định được người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userKey);

        // ✅ Ghi quiz history
        DatabaseReference historyRef = userRef.child("quizHistory").push();
        HashMap<String, Object> quizData = new HashMap<>();
        quizData.put("topic", topicTitle);
        quizData.put("score", score);
        quizData.put("total", total);

        // Thêm thời gian định dạng dễ nhìn
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        quizData.put("timestamp", timestamp);

        historyRef.setValue(quizData);

        // ✅ Cập nhật tiến độ
        DatabaseReference progressRef = userRef.child("progress");

        progressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int topicsLearned = 0;
                int quizzesCompleted = 0;
                double averageScore = 0;
                int overallProgress;

                if (snapshot.child("topicsLearned").exists()) {
                    topicsLearned = snapshot.child("topicsLearned").getValue(Integer.class);
                }
                if (snapshot.child("quizzesCompleted").exists()) {
                    quizzesCompleted = snapshot.child("quizzesCompleted").getValue(Integer.class);
                }
                if (snapshot.child("averageScore").exists()) {
                    averageScore = snapshot.child("averageScore").getValue(Double.class);
                }

                // ✅ Cập nhật số quiz, tính điểm trung bình mới
                quizzesCompleted++;
                double quizScorePercent = score * 100.0 / total;
                double newAverage = ((averageScore * (quizzesCompleted - 1)) + quizScorePercent) / quizzesCompleted;

                overallProgress = (int) ((topicsLearned * 10 + quizzesCompleted * 10 + newAverage) / 3);

                // ✅ Ghi lại
                progressRef.child("quizzesCompleted").setValue(quizzesCompleted);
                progressRef.child("averageScore").setValue(newAverage);
                progressRef.child("overallProgress").setValue(overallProgress);

                Toast.makeText(QuizResultActivity.this, "✅ Đã lưu tiến độ học!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizResultActivity.this, "❌ Lỗi cập nhật tiến độ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
