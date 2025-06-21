package vn.edu.tlu.btln9.tuvung;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion;
    private RadioGroup radioGroup;
    private RadioButton r1, r2, r3, r4;
    private Button btnNext;

    private List<Question> questionList = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;

    private String topicId, topicTitle;

    // Biến để kiểm tra trạng thái đã trả lời câu hỏi chưa
    private boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroup = findViewById(R.id.radioGroup);
        r1 = findViewById(R.id.option1);
        r2 = findViewById(R.id.option2);
        r3 = findViewById(R.id.option3);
        r4 = findViewById(R.id.option4);
        btnNext = findViewById(R.id.btnNext);

        topicId = getIntent().getStringExtra("topicId");
        topicTitle = getIntent().getStringExtra("topicTitle");

        Log.d("QuizActivity", "topicId: " + topicId + ", topicTitle: " + topicTitle);

        loadQuestions();

        btnNext.setOnClickListener(v -> checkAnswer());
    }

    private void loadQuestions() {
        if (topicId == null || topicId.isEmpty()) {
            Toast.makeText(this, "Không xác định chủ đề", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("quiz_questions").child(topicId);
        Log.d("QuizActivity", "Đọc dữ liệu tại đường dẫn: quiz_questions/" + topicId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                questionList.clear();

                if (!snapshot.exists()) {
                    Log.w("QuizActivity", "Không tồn tại dữ liệu tại: quiz_questions/" + topicId);
                    Toast.makeText(QuizActivity.this, "Chủ đề chưa có câu hỏi", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                for (DataSnapshot child : snapshot.getChildren()) {
                    Question q = child.getValue(Question.class);
                    if (q != null && q.getQuestion() != null && q.getCorrectAnswer() != null) {
                        questionList.add(q);
                        Log.d("QuizActivity", "Thêm: " + q.getQuestion());
                    } else {
                        Log.w("QuizActivity", "Dữ liệu bị thiếu hoặc không đúng định dạng: " + child.toString());
                    }
                }

                if (questionList.isEmpty()) {
                    Toast.makeText(QuizActivity.this, "Không có câu hỏi hợp lệ", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    currentIndex = 0;
                    score = 0;
                    showQuestion();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(QuizActivity.this, "Lỗi tải câu hỏi", Toast.LENGTH_SHORT).show();
                Log.e("QuizActivity", "Firebase error: " + error.getMessage());
            }
        });
    }

    private void showQuestion() {
        if (currentIndex >= questionList.size()) {
            Intent intent = new Intent(this, QuizResultActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("total", questionList.size());
            intent.putExtra("topicTitle", topicTitle);
            startActivity(intent);
            finish();
            return;
        }

        Question q = questionList.get(currentIndex);

        tvQuestion.setText((currentIndex + 1) + ". " + q.getQuestion());
        r1.setText(q.getOption1());
        r2.setText(q.getOption2());
        r3.setText(q.getOption3());
        r4.setText(q.getOption4());

        radioGroup.clearCheck();

        // reset màu chữ các đáp án về màu mặc định
        resetOptionColors();

        // bật lại chọn được
        setOptionsEnabled(true);

        btnNext.setText("Tiếp tục");
        answered = false;
    }

    private void checkAnswer() {
        if (answered) {
            // Đã trả lời, chuyển câu hỏi tiếp theo
            currentIndex++;
            showQuestion();
            return;
        }

        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Vui lòng chọn một đáp án", Toast.LENGTH_SHORT).show();
            return;
        }

        answered = true;
        btnNext.setText("Câu hỏi tiếp theo");

        RadioButton selected = findViewById(selectedId);
        String answer = selected.getText().toString();

        Question current = questionList.get(currentIndex);
        String correctAnswer = current.getCorrectAnswer();

        // Disable không cho chọn nữa
        setOptionsEnabled(false);

        if (answer.equals(correctAnswer)) {
            score++;
            // Đáp án đúng tô xanh
            selected.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            // Đáp án sai tô đỏ
            selected.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            // Tô xanh đáp án đúng
            highlightCorrectAnswer(correctAnswer);
        }
    }
    private void highlightCorrectAnswer(String correctAnswer) {
        if (r1.getText().toString().equals(correctAnswer)) {
            r1.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (r2.getText().toString().equals(correctAnswer)) {
            r2.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (r3.getText().toString().equals(correctAnswer)) {
            r3.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (r4.getText().toString().equals(correctAnswer)) {
            r4.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void resetOptionColors() {
        int defaultColor = getResources().getColor(android.R.color.black);
        r1.setTextColor(defaultColor);
        r2.setTextColor(defaultColor);
        r3.setTextColor(defaultColor);
        r4.setTextColor(defaultColor);
    }

    private void setOptionsEnabled(boolean enabled) {
        r1.setEnabled(enabled);
        r2.setEnabled(enabled);
        r3.setEnabled(enabled);
        r4.setEnabled(enabled);
    }
}