package vn.edu.tlu.btln9.tuvung;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    public interface OnQuizClickListener {
        void onQuizClicked(Question question, String key);
    }

    private List<Question> quizList;
    private List<String> keyList;
    private OnQuizClickListener listener;

    public QuizAdapter(List<Question> quizList, List<String> keyList, OnQuizClickListener listener) {
        this.quizList = quizList;
        this.keyList = keyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_question, parent, false);
        return new QuizViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Question q = quizList.get(position);
        holder.tvQuestion.setText(q.getQuestion());
        holder.tvAnswer.setText("Đáp án đúng: " + q.getCorrectAnswer());
        holder.tvTopic.setText("Chủ đề: " + q.getTopic());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuizClicked(q, keyList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswer, tvTopic;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            tvTopic = itemView.findViewById(R.id.tvTopic);
        }
    }
}