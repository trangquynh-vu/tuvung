package vn.edu.tlu.btln9.tuvung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class QuizListActivity extends AppCompatActivity {

    private ListView listViewTopics;
    private Button btnViewHistory;
    private ArrayList<Topic> topicList = new ArrayList<>();
    private ArrayList<String> topicTitles = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        // Ánh xạ view
        listViewTopics = findViewById(R.id.listViewTopics);
        btnViewHistory = findViewById(R.id.btnViewHistory);

        // Adapter hiển thị tiêu đề chủ đề quiz
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topicTitles);
        listViewTopics.setAdapter(adapter);

        // Lấy danh sách chủ đề từ Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("topic").child("topics");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                topicList.clear();
                topicTitles.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Topic topic = child.getValue(Topic.class);
                    if (topic != null) {
                        topicList.add(topic);
                        topicTitles.add(topic.getTitle());
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(QuizListActivity.this, "Lỗi khi tải chủ đề", Toast.LENGTH_SHORT).show();
            }
        });

        // Khi người dùng chọn một chủ đề
        listViewTopics.setOnItemClickListener((parent, view, position, id) -> {
            Topic selected = topicList.get(position);
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("topicId", selected.getTopicId());
            intent.putExtra("topicTitle", selected.getTitle());
            startActivity(intent);
        });

        // Khi bấm "Xem lịch sử quiz"
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(QuizListActivity.this, QuizHistoryActivity.class);
            startActivity(intent);
        });
    }
}
