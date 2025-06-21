package vn.edu.tlu.btln9.tuvung;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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

        Button btnViewHistory = findViewById(R.id.btnViewHistory);
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResultActivity.this, QuizHistoryActivity.class);
            startActivity(intent);
        });
    }

}
