package vn.edu.tlu.btln9.tuvung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TopicListActivity extends AppCompatActivity {

    private ListView lvTopics;
    private ArrayList<Topic> topicList = new ArrayList<>();
    private ArrayList<String> topicTitles = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private DatabaseReference topicsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);

        lvTopics = findViewById(R.id.lvTopics);

        // Adapter để hiển thị tiêu đề chủ đề
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topicTitles);
        lvTopics.setAdapter(adapter);

        // Tham chiếu đến "topic/topics" trong Firebase
        topicsRef = FirebaseDatabase.getInstance().getReference("topic").child("topics");

        // Lấy dữ liệu chủ đề từ Firebase
        topicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicList.clear();
                topicTitles.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Topic topic = child.getValue(Topic.class);
                    if (topic != null) {
                        topicList.add(topic);
                        topicTitles.add("📁 " + topic.getTitle());
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TopicListActivity.this, "❌ Lỗi khi tải danh sách chủ đề", Toast.LENGTH_SHORT).show();
            }
        });

        // Bắt sự kiện khi chọn một chủ đề
        lvTopics.setOnItemClickListener((parent, view, position, id) -> {
            Topic selectedTopic = topicList.get(position);

            Intent intent = new Intent(TopicListActivity.this, WordStudyActivity.class);
            intent.putExtra("topicId", selectedTopic.getTopicId());   // Gửi topicId
            intent.putExtra("topicTitle", selectedTopic.getTitle()); // Gửi tiêu đề chủ đề
            startActivity(intent);
        });
    }
}
