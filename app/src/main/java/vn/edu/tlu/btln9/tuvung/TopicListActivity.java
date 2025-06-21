package vn.edu.tlu.btln9.tuvung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topicTitles);
        lvTopics.setAdapter(adapter);

        // ✅ Đường dẫn đúng tới danh sách topic
        topicsRef = FirebaseDatabase.getInstance().getReference("topic").child("topics");

        topicsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(TopicListActivity.this, "Lỗi tải chủ đề", Toast.LENGTH_SHORT).show();
            }
        });

        lvTopics.setOnItemClickListener((adapterView, view, position, id) -> {
            Topic selectedTopic = topicList.get(position);
            Intent intent = new Intent(TopicListActivity.this, WordStudyActivity.class);
            intent.putExtra("topicId", selectedTopic.getTopicId());
            intent.putExtra("topicTitle", selectedTopic.getTitle());
            startActivity(intent);
        });
    }
}
