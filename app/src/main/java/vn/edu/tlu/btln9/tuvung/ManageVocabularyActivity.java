package vn.edu.tlu.btln9.tuvung;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;
import java.util.*;

public class ManageVocabularyActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVocabulary;
    private VocabularyAdapter adapter;
    private List<Vocabulary> vocabularyList;
    private List<Vocabulary> filteredList;

    private Button btnAddVocabulary;
    private Spinner spinnerTopic;

    private DatabaseReference vocabRef;
    private DatabaseReference topicRef;

    private List<String> topicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vocabulary);

        // Gắn Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerViewVocabulary = findViewById(R.id.recyclerViewVocabulary);
        btnAddVocabulary = findViewById(R.id.btnAddVocabulary);
        spinnerTopic = findViewById(R.id.spinnerTopic);

        recyclerViewVocabulary.setLayoutManager(new LinearLayoutManager(this));
        vocabularyList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new VocabularyAdapter(filteredList, this::onVocabularyItemClicked);
        recyclerViewVocabulary.setAdapter(adapter);

        vocabRef = FirebaseDatabase.getInstance().getReference("vocabulary");
        topicRef = FirebaseDatabase.getInstance().getReference("topic/topics");

        topicList = new ArrayList<>();
        topicList.add("Tất cả");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, topicList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTopic.setAdapter(spinnerAdapter);

        spinnerTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTopic = topicList.get(position);
                filterVocabularyByTopic(selectedTopic);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterVocabularyByTopic("Tất cả");
            }
        });

        loadVocabulary();
        loadTopics();

        btnAddVocabulary.setOnClickListener(v -> showAddVocabularyDialog());
    }

    private void loadVocabulary() {
        vocabRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vocabularyList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Vocabulary vocab = ds.getValue(Vocabulary.class);
                    if (vocab != null) {
                        vocabularyList.add(vocab);
                    }
                }
                filterVocabularyByTopic(spinnerTopic.getSelectedItem() != null ? spinnerTopic.getSelectedItem().toString() : "Tất cả");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageVocabularyActivity.this, "Lỗi tải dữ liệu từ vựng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTopics() {
        topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicList.clear();
                topicList.add("Tất cả");

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Topic topic = ds.getValue(Topic.class);
                    if (topic != null && topic.getTitle() != null && !topic.getTitle().trim().isEmpty()) {
                        topicList.add(topic.getTitle().trim());
                    }
                }

                ((ArrayAdapter<String>) spinnerTopic.getAdapter()).notifyDataSetChanged();
                spinnerTopic.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageVocabularyActivity.this, "Không thể tải danh sách chủ đề", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterVocabularyByTopic(String topic) {
        filteredList.clear();
        if (topic.equals("Tất cả")) {
            filteredList.addAll(vocabularyList);
        } else {
            for (Vocabulary vocab : vocabularyList) {
                if (vocab.getTopic() != null && vocab.getTopic().trim().equalsIgnoreCase(topic.trim())) {
                    filteredList.add(vocab);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void onVocabularyItemClicked(Vocabulary vocab) {
        vocabRef.orderByChild("vocabId").equalTo(vocab.getVocabId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            showEditDeleteDialog(vocab, ds.getKey());
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void showAddVocabularyDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_vocabulary, null);

        EditText edtWord = dialogView.findViewById(R.id.edtWord);
        EditText edtPronounce = dialogView.findViewById(R.id.edtPronounce);
        EditText edtMeaning = dialogView.findViewById(R.id.edtMeaning);
        EditText edtExample = dialogView.findViewById(R.id.edtExample);
        EditText edtTopic = dialogView.findViewById(R.id.edtTopic);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        btnSave.setOnClickListener(v -> {
            String word = edtWord.getText().toString().trim();
            String pronounce = edtPronounce.getText().toString().trim();
            String meaning = edtMeaning.getText().toString().trim();
            String example = edtExample.getText().toString().trim();
            String topic = edtTopic.getText().toString().trim();

            if (word.isEmpty() || meaning.isEmpty() || topic.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ từ, nghĩa và chủ đề", Toast.LENGTH_SHORT).show();
                return;
            }

            addVocabularyToFirebase(word, pronounce, meaning, example, topic);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void addVocabularyToFirebase(String word, String pronounce, String meaning, String example, String topic) {
        String key = vocabRef.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Lỗi hệ thống, thử lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        Vocabulary newVocab = new Vocabulary();
        newVocab.setWord(word);
        newVocab.setPronounce(pronounce);
        newVocab.setMeaning(meaning);
        newVocab.setExample(example);
        newVocab.setTopic(topic.trim());
        newVocab.setVocabId(key);

        vocabRef.child(key).setValue(newVocab)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Thêm từ thành công", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi thêm từ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showEditDeleteDialog(Vocabulary vocab, String firebaseKey) {
        View dialogView = getLayoutInflater().inflate(R.layout.delete_edit_vocabulary, null);

        EditText edtWord = dialogView.findViewById(R.id.edtWord);
        EditText edtPronounce = dialogView.findViewById(R.id.edtPronounce);
        EditText edtMeaning = dialogView.findViewById(R.id.edtMeaning);
        EditText edtExample = dialogView.findViewById(R.id.edtExample);
        EditText edtTopic = dialogView.findViewById(R.id.edtTopic);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        edtWord.setText(vocab.getWord());
        edtPronounce.setText(vocab.getPronounce());
        edtMeaning.setText(vocab.getMeaning());
        edtExample.setText(vocab.getExample());
        edtTopic.setText(vocab.getTopic());

        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        btnSave.setOnClickListener(v -> {
            String word = edtWord.getText().toString().trim();
            String pronounce = edtPronounce.getText().toString().trim();
            String meaning = edtMeaning.getText().toString().trim();
            String example = edtExample.getText().toString().trim();
            String topic = edtTopic.getText().toString().trim();

            if (word.isEmpty() || meaning.isEmpty() || topic.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ từ, nghĩa và chủ đề", Toast.LENGTH_SHORT).show();
                return;
            }

            Vocabulary updatedVocab = new Vocabulary();
            updatedVocab.setWord(word);
            updatedVocab.setPronounce(pronounce);
            updatedVocab.setMeaning(meaning);
            updatedVocab.setExample(example);
            updatedVocab.setTopic(topic);
            updatedVocab.setVocabId(vocab.getVocabId());

            vocabRef.child(firebaseKey).setValue(updatedVocab)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc chắn muốn xoá từ vựng này không?")
                    .setPositiveButton("Xoá", (d, i) -> {
                        vocabRef.child(firebaseKey).removeValue()
                                .addOnSuccessListener(unused -> Toast.makeText(this, "Xoá thành công", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        dialog.dismiss();
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
