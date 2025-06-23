package vn.edu.tlu.btln9.tuvung;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEditTopicActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription;
    private Button btnSave;
    private DatabaseReference topicRef;
    private String topicId = null;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_topic);

        // Ánh xạ view
        Toolbar toolbar = findViewById(R.id.toolbar);
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        btnSave = findViewById(R.id.btnSave);

        // Thiết lập Toolbar có nút quay lại
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Kết nối Firebase
        topicRef = FirebaseDatabase.getInstance().getReference("topic").child("topics");

        // Nếu là sửa: lấy dữ liệu từ Intent
        if (getIntent() != null && getIntent().hasExtra("topicId")) {
            topicId = getIntent().getStringExtra("topicId");
            edtTitle.setText(getIntent().getStringExtra("title"));
            edtDescription.setText(getIntent().getStringExtra("description"));
            isEditing = true;
        }

        // Xử lý khi bấm nút Lưu
        btnSave.setOnClickListener(v -> saveTopic());
    }

    private void saveTopic() {
        String title = edtTitle.getText().toString().trim();
        String desc = edtDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            edtTitle.setError("Vui lòng nhập tên chủ đề");
            return;
        }

        if (!isEditing) {
            topicId = topicRef.push().getKey(); // tạo ID nếu là thêm
        }

        // Tạo đối tượng Topic và lưu vào Firebase
        Topic topic = new Topic(topicId, title, desc);
        topicRef.child(topicId).setValue(topic)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            isEditing ? "Đã cập nhật chủ đề" : "Đã thêm chủ đề mới",
                            Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại sau khi lưu
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
