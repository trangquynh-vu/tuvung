package vn.edu.tlu.btln9.tuvung;

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
    private String topicId; // Dùng để lọc từ vựng theo chủ đề

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_study);

        // Nhận dữ liệu từ Intent
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

        // Hiển thị tên chủ đề
        tvTopic.setText("📚 Chủ đề: " + topicId);

        // Khởi tạo TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "❌ Ngôn ngữ không được hỗ trợ hoặc thiếu dữ liệu!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "❌ Không thể khởi tạo Text-to-Speech!", Toast.LENGTH_LONG).show();
            }
        });

        // Tải danh sách từ vựng từ Firebase
        DatabaseReference wordsRef = FirebaseDatabase.getInstance().getReference("vocabulary");
        wordsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wordList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Word word = child.getValue(Word.class);
                    if (word != null && word.getTopic() != null &&
                            word.getTopic().trim().equalsIgnoreCase(topicId.trim())) {
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
                Toast.makeText(WordStudyActivity.this, "Lỗi tải dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút phát âm
        btnSpeak.setOnClickListener(v -> {
            if (!wordList.isEmpty()) {
                String wordText = wordList.get(currentIndex).getWord();
                if (tts != null && !wordText.isEmpty()) {
                    tts.speak(wordText, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    Toast.makeText(this, "❌ Không thể phát âm!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nút từ tiếp theo
        btnNext.setOnClickListener(v -> {
            if (currentIndex < wordList.size() - 1) {
                currentIndex++;
                showWord(currentIndex);
            } else {
                Toast.makeText(this, "🎉 Bạn đã học hết từ trong chủ đề!", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
