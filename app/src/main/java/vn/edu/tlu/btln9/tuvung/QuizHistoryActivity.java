package vn.edu.tlu.btln9.tuvung;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;

public class QuizHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> displayList = new ArrayList<>();
    private DatabaseReference resultsRef;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_history);

        listView = findViewById(R.id.lvHistory);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không xác định người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        resultsRef = FirebaseDatabase.getInstance().getReference("quiz");

        resultsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                displayList.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setText("Chưa có kết quả quiz nào.");
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        QuizResult result = child.getValue(QuizResult.class);
                        if (result != null) {
                            String uidInData = String.valueOf(result.getUserId());
                            String currentUserId = String.valueOf(userId);

                            if (uidInData.equals(currentUserId)) {
                                displayList.add(formatQuizResult(result));
                            }
                        }
                    }

                    if (displayList.isEmpty()) {
                        tvEmpty.setText("Chưa có kết quả quiz nào.");
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(QuizHistoryActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Format hiển thị 1 kết quả quiz
    private String formatQuizResult(QuizResult r) {
        return "\uD83D\uDCD8 Chủ đề: " + safeString(r.getTopic()) +
                "\n✅ Đúng: " + r.getCorrectAnswers() +
                " ❌ Sai: " + r.getWrongAnswers() +
                "\n\uD83C\uDFAF Điểm: " + r.getScore() + " / " + r.getTotalQuestions() +
                "\n\uD83D\uDD52 Thời gian: " + r.getDurationSeconds() + " giây" +
                (r.getTimestamp() != null ? ("\n🗓️ Ngày: " + r.getTimestamp()) : "");
    }

    // Tránh lỗi null
    private String safeString(String s) {
        return s == null ? "" : s;
    }
}
