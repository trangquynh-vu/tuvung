package vn.edu.tlu.btln9.tuvung;


import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class QuizListActivity extends AppCompatActivity {

    private ListView listViewTopics;
    private ArrayList<Topic> topicList = new ArrayList<>();
    private ArrayList<String> topicTitles = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        listViewTopics = findViewById(R.id.listViewTopics);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topicTitles);
        listViewTopics.setAdapter(adapter);

        // Firebase
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
            public void onCancelled(DatabaseError error) {}
        });

        listViewTopics.setOnItemClickListener((AdapterView<?> adapterView, android.view.View view, int i, long l) -> {
            Topic selected = topicList.get(i);
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("topicId", selected.getTopicId());
            intent.putExtra("topicTitle", selected.getTitle());
            startActivity(intent);
        });
    }
}
