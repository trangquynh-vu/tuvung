package vn.edu.tlu.btln9.tuvung;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageTopicsActivity extends AppCompatActivity {

    private RecyclerView recyclerTopics;
    private List<Topic> topicList;
    private TopicAdapter topicAdapter;
    private DatabaseReference topicRef;
    private Button btnAddTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_topics); // đảm bảo layout này đúng

        // ✅ Ánh xạ giao diện
        Toolbar toolbar = findViewById(R.id.toolbar);
        btnAddTopic = findViewById(R.id.btnAddTopic);
        recyclerTopics = findViewById(R.id.recyclerTopics);

        // ✅ Thiết lập Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ✅ Khởi tạo danh sách và Adapter
        topicList = new ArrayList<>();
        topicAdapter = new TopicAdapter(this, topicList);
        recyclerTopics.setLayoutManager(new LinearLayoutManager(this));
        recyclerTopics.setAdapter(topicAdapter);

        // ✅ Kết nối đến node topics trên Firebase
        topicRef = FirebaseDatabase.getInstance().getReference("topic").child("topics");

        // ✅ Tải dữ liệu từ Firebase
        loadTopicsFromFirebase();

        // ✅ Sự kiện thêm chủ đề
        btnAddTopic.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditTopicActivity.class);
            startActivity(intent);
        });
    }

    private void loadTopicsFromFirebase() {
        topicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Topic topic = ds.getValue(Topic.class);
                    if (topic != null) {
                        topicList.add(topic);
                    }
                }
                topicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageTopicsActivity.this, "Lỗi khi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Gọi lại để cập nhật dữ liệu nếu quay lại từ màn hình thêm/sửa
        loadTopicsFromFirebase();
    }
}
