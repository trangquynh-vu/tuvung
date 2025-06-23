package vn.edu.tlu.btln9.tuvung;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ManageQuizzActivity extends AppCompatActivity {

    private RecyclerView recyclerViewQuiz;
    private QuizAdapter adapter;
    private List<Question> questionList = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();

    private Button btnAddQuestion;
    private Spinner spinnerTopic;
    private DatabaseReference quizRef;

    private List<String> topicList = new ArrayList<>();

    private ValueEventListener topicsListener;
    private ValueEventListener questionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_quizz);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerViewQuiz = findViewById(R.id.recyclerViewQuiz);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        spinnerTopic = findViewById(R.id.spinnerTopic);

        recyclerViewQuiz.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuizAdapter(questionList, keyList, this::showEditDialog);
        recyclerViewQuiz.setAdapter(adapter);

        quizRef = FirebaseDatabase.getInstance().getReference("quiz_questions");

        topicList.add("Tất cả");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, topicList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTopic.setAdapter(spinnerAdapter);

        spinnerTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTopic = topicList.get(position);
                loadQuestions(selectedTopic);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadQuestions("Tất cả");
            }
        });

        loadTopics();

        btnAddQuestion.setOnClickListener(v -> showAddDialog());
    }

    private void loadTopics() {
        if (topicsListener != null) {
            quizRef.removeEventListener(topicsListener);
        }

        topicsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicList.clear();
                topicList.add("Tất cả");
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String topic = ds.getKey();
                    if (topic != null && !topic.trim().isEmpty() && !topicList.contains(topic.trim())) {
                        topicList.add(topic.trim());
                    }
                }
                Log.d("ManageQuizz", "Topics loaded: " + topicList);
                ((ArrayAdapter<String>) spinnerTopic.getAdapter()).notifyDataSetChanged();

                if (!topicList.contains(spinnerTopic.getSelectedItem())) {
                    spinnerTopic.setSelection(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageQuizzActivity.this, "Không thể tải danh sách chủ đề", Toast.LENGTH_SHORT).show();
            }
        };

        quizRef.addValueEventListener(topicsListener);
    }

    private void loadQuestions(String topic) {
        if (questionsListener != null) {
            quizRef.removeEventListener(questionsListener);
            for (String t : topicList) {
                quizRef.child(t).removeEventListener(questionsListener);
            }
        }

        if (topic.equals("Tất cả")) {
            questionsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    questionList.clear();
                    keyList.clear();
                    for (DataSnapshot topicSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot questionSnapshot : topicSnapshot.getChildren()) {
                            Question question = questionSnapshot.getValue(Question.class);
                            if (question != null) {
                                questionList.add(question);
                                keyList.add(topicSnapshot.getKey() + "/" + questionSnapshot.getKey());
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ManageQuizzActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            };
            quizRef.addValueEventListener(questionsListener);
        } else {
            questionsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    questionList.clear();
                    keyList.clear();
                    for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null) {
                            questionList.add(question);
                            keyList.add(questionSnapshot.getKey());
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ManageQuizzActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            };
            quizRef.child(topic).addValueEventListener(questionsListener);
        }
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_quiz, null);
        AlertDialog dialog = createQuestionDialog(view, null, null);
        dialog.show();
    }

    private void showEditDialog(Question question, String key) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_quiz, null);
        AlertDialog dialog = createQuestionDialog(view, question, key);
        dialog.show();
    }

    private AlertDialog createQuestionDialog(View view, Question question, String key) {
        EditText edtQuestion = view.findViewById(R.id.edtQuestion);
        EditText edtOptionA = view.findViewById(R.id.edtOptionA);
        EditText edtOptionB = view.findViewById(R.id.edtOptionB);
        EditText edtOptionC = view.findViewById(R.id.edtOptionC);
        EditText edtOptionD = view.findViewById(R.id.edtOptionD);
        EditText edtCorrectAnswer = view.findViewById(R.id.edtCorrectAnswer);
        EditText edtTopic = view.findViewById(R.id.edtTopic);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        // ⚠️ btnDelete có thể bị null nếu layout không có
        Button btnDelete = view.findViewById(R.id.btnDelete);

        if (question != null) {
            edtQuestion.setText(question.getQuestion());
            edtOptionA.setText(question.getOption1());
            edtOptionB.setText(question.getOption2());
            edtOptionC.setText(question.getOption3());
            edtOptionD.setText(question.getOption4());
            edtCorrectAnswer.setText(question.getCorrectAnswer());
            edtTopic.setText(question.getTopic());
        } else {
            // Nếu là thêm mới → ẩn nút Xóa nếu có
            if (btnDelete != null) {
                btnDelete.setVisibility(View.GONE);
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String q = edtQuestion.getText().toString().trim();
            String a = edtOptionA.getText().toString().trim();
            String b = edtOptionB.getText().toString().trim();
            String c = edtOptionC.getText().toString().trim();
            String d = edtOptionD.getText().toString().trim();
            String correct = edtCorrectAnswer.getText().toString().trim();
            String topic = edtTopic.getText().toString().trim();

            if (q.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || correct.isEmpty() || topic.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Question newQ = new Question(q, a, b, c, d, correct, topic);
            DatabaseReference ref = quizRef.child(topic);

            if (key == null) {
                ref.push().setValue(newQ);
            } else {
                if (key.contains("/")) {
                    String[] parts = key.split("/");
                    quizRef.child(parts[0]).child(parts[1]).setValue(newQ);
                } else {
                    ref.child(key).setValue(newQ);
                }
            }

            dialog.dismiss();
        });

        // ⚠️ Nếu btnDelete có tồn tại mới set sự kiện xoá
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                if (key != null) {
                    DatabaseReference deleteRef = quizRef;
                    if (key.contains("/")) {
                        String[] parts = key.split("/");
                        deleteRef = deleteRef.child(parts[0]).child(parts[1]);
                    } else {
                        deleteRef = deleteRef.child(edtTopic.getText().toString().trim()).child(key);
                    }
                    deleteRef.removeValue();
                    dialog.dismiss();
                }
            });
        }

        return dialog;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (topicsListener != null) quizRef.removeEventListener(topicsListener);
        if (questionsListener != null) quizRef.removeEventListener(questionsListener);
        for (String t : topicList) {
            quizRef.child(t).removeEventListener(questionsListener);
        }
    }
}
