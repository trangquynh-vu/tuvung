package vn.edu.tlu.btln9.tuvung;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.Map;

public class ManageTopicsActivity extends AppCompatActivity {

    private ListView lvTopics;
    private Button btnAddTopic, btnEditTopic, btnDeleteTopic;
    private ArrayList<Topic> topicList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayList<String> topicTitles = new ArrayList<>();
    private int selectedIndex = -1;

    private DatabaseReference topicsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_topics);

        lvTopics = findViewById(R.id.lvTopics);
        btnAddTopic = findViewById(R.id.btnAddTopic);
        btnEditTopic = findViewById(R.id.btnEditTopic);
        btnDeleteTopic = findViewById(R.id.btnDeleteTopic);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, topicTitles);
        lvTopics.setAdapter(adapter);
        lvTopics.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        topicsRef = FirebaseDatabase.getInstance().getReference("topics");

        loadTopics();

        lvTopics.setOnItemClickListener((parent, view, position, id) -> selectedIndex = position);

        btnAddTopic.setOnClickListener(v -> showAddDialog());

        btnEditTopic.setOnClickListener(v -> {
            if (selectedIndex != -1) {
                showEditDialog(topicList.get(selectedIndex));
            } else {
                Toast.makeText(this, "Vui lòng chọn chủ đề để sửa", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteTopic.setOnClickListener(v -> {
            if (selectedIndex != -1) {
                confirmDelete(topicList.get(selectedIndex));
            } else {
                Toast.makeText(this, "Vui lòng chọn chủ đề để xoá", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTopics() {
        topicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageTopicsActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddDialog() {
        EditText input = new EditText(this);
        input.setHint("Nhập tên chủ đề mới");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("Thêm chủ đề")
                .setView(input)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String title = input.getText().toString().trim();
                    if (!title.isEmpty()) {
                        String id = topicsRef.push().getKey();
                        Topic topic = new Topic(id, title);
                        topicsRef.child(id).setValue(topic);
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void showEditDialog(Topic topic) {
        EditText input = new EditText(this);
        input.setText(topic.getTitle());
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("Sửa chủ đề")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newTitle = input.getText().toString().trim();
                    if (!newTitle.isEmpty()) {
                        Map<String, Object> update = new HashMap<>();
                        update.put("title", newTitle);
                        topicsRef.child(topic.getTopicId()).updateChildren(update);
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void confirmDelete(Topic topic) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc muốn xoá chủ đề này?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    topicsRef.child(topic.getTopicId()).removeValue();
                    selectedIndex = -1;
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}
