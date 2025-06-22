package vn.edu.tlu.btln9.tuvung;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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

import java.util.ArrayList;
import java.util.Locale;

public class WordStudyActivity extends AppCompatActivity {

    private TextView tvTopic, tvWord, tvPronounce, tvMeaning, tvExample, tvProgress;
    private Button btnSpeak, btnNext;

    private ArrayList<Word> wordList = new ArrayList<>();
    private int currentIndex = 0;

    private TextToSpeech tts;
    private String topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_study);

        topicId = getIntent().getStringExtra("topicId");

        // Ánh xạ view
        tvTopic = findViewById(R.id.tvTopic);
        tvWord = findViewById(R.id.tvWord);
        tvPronounce = findViewById(R.id.tvPronounce);
        tvMeaning = findViewById(R.id.tvMeaning);
        tvExample = findViewById(R.id.tvExample);
        tvProgress = findViewById(R.id.tvProgress);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnNext = findViewById(R.id.btnNext);

        tvTopic.setText("📚 Chủ đề: " + topicId);

        // TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        // Load dữ liệu từ Firebase
        DatabaseReference wordsRef = FirebaseDatabase.getInstance().getReference("vocabulary");
        wordsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wordList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Word word = child.getValue(Word.class);
                    if (word != null && topicId.equalsIgnoreCase(word.getTopic())) {
                        wordList.add(word);
                    }
                }

                if (!wordList.isEmpty()) {
                    currentIndex = 0;
                    showWord(currentIndex);
                } else {
                    Toast.makeText(WordStudyActivity.this, "❌ Chủ đề chưa có từ vựng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WordStudyActivity.this, "Lỗi Firebase!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSpeak.setOnClickListener(v -> {
            if (!wordList.isEmpty()) {
                String text = wordList.get(currentIndex).getWord();
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < wordList.size() - 1) {
                currentIndex++;
                showWord(currentIndex);
            } else {
                Toast.makeText(this, "🎉 Bạn đã học hết từ trong chủ đề!", Toast.LENGTH_SHORT).show();
                updateProgressOncePerTopic();
            }
        });
    }

    private void showWord(int index) {
        Word w = wordList.get(index);
        tvWord.setText(w.getWord());
        tvPronounce.setText(w.getPronounce());
        tvMeaning.setText("📝 Nghĩa: " + w.getMeaning());
        tvExample.setText("💬 Ví dụ: " + w.getExample());
        tvProgress.setText("Từ " + (index + 1) + " / " + wordList.size());
    }

    private void updateProgressOncePerTopic() {
        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        String userKey = prefs.getString("userKey", null);

        if (userKey == null) {
            Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference progressRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userKey)
                .child("progress");

        progressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int topicsLearned = 0;
                int quizzesCompleted = 0;
                double averageScore = 0;
                int overallProgress;

                boolean alreadyLearned = snapshot.child("learnedTopics").hasChild(topicId);

                if (alreadyLearned) {
                    Toast.makeText(WordStudyActivity.this, "🎓 Chủ đề này đã được tính trước đó!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshot.child("topicsLearned").exists()) {
                    topicsLearned = snapshot.child("topicsLearned").getValue(Integer.class);
                }
                if (snapshot.child("quizzesCompleted").exists()) {
                    quizzesCompleted = snapshot.child("quizzesCompleted").getValue(Integer.class);
                }
                if (snapshot.child("averageScore").exists()) {
                    averageScore = snapshot.child("averageScore").getValue(Double.class);
                }

                topicsLearned++;
                overallProgress = (int) ((topicsLearned * 10 + quizzesCompleted * 10 + averageScore) / 3);

                // Cập nhật dữ liệu
                progressRef.child("topicsLearned").setValue(topicsLearned);
                progressRef.child("overallProgress").setValue(overallProgress);
                progressRef.child("learnedTopics").child(topicId).setValue(true); // đánh dấu chủ đề đã học

                Toast.makeText(WordStudyActivity.this, "✅ Đã cập nhật tiến độ!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WordStudyActivity.this, "❌ Lỗi khi cập nhật tiến độ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
