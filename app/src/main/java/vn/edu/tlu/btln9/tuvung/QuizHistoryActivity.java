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
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class QuizHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> displayList = new ArrayList<>();
    private DatabaseReference historyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_history);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Ánh xạ view
        listView = findViewById(R.id.lvHistory);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        // Lấy userKey
        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        String userKey = prefs.getString("userKey", null);

        if (userKey == null) {
            Toast.makeText(this, "Không xác định người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        // Đúng đường dẫn (nơi đã lưu kết quả)
        historyRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userKey)
                .child("quizHistory");

        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                displayList.clear();

                if (!snapshot.exists()) {
                    tvEmpty.setText("Chưa có kết quả quiz nào.");
                    tvEmpty.setVisibility(View.VISIBLE);
                    return;
                }

                for (DataSnapshot child : snapshot.getChildren()) {
                    Map<String, Object> quizData = (Map<String, Object>) child.getValue();

                    if (quizData != null) {
                        String topic = safeString((String) quizData.get("topic"));
                        Object score = quizData.get("score");
                        Object total = quizData.get("total");

                        String formattedTime = safeString((String) quizData.get("timestamp"));
                        String result = "📘 Chủ đề: " + topic
                                + "\n🎯 Điểm: " + score + " / " + total
                                + "\n🕓 Ngày: " + formattedTime;

                        displayList.add(result);
                    }
                }

                if (displayList.isEmpty()) {
                    tvEmpty.setText("Chưa có kết quả quiz nào.");
                    tvEmpty.setVisibility(View.VISIBLE);
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

    private String safeString(String s) {
        return s == null ? "" : s;
    }

    private long parseTimestamp(Object obj) {
        if (obj == null) return 0;
        try {
            if (obj instanceof Long) return (Long) obj;
            if (obj instanceof Double) return ((Double) obj).longValue();
            if (obj instanceof String) return Long.parseLong((String) obj);
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    private String formatTimestamp(long millis) {
        if (millis <= 0) return "Không rõ";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
