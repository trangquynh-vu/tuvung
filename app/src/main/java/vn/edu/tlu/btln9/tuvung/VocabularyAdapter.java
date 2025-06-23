package vn.edu.tlu.btln9.tuvung;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Vocabulary vocab);
    }

    private List<Vocabulary> vocabList;
    private OnItemClickListener listener;

    public VocabularyAdapter(List<Vocabulary> vocabList, OnItemClickListener listener) {
        this.vocabList = vocabList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VocabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocabulary, parent, false);
        return new VocabViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabViewHolder holder, int position) {
        Vocabulary vocab = vocabList.get(position);
        holder.tvWord.setText(vocab.getWord());
        holder.tvMeaning.setText(vocab.getMeaning());
        holder.tvTopic.setText("Chủ đề: " + vocab.getTopic());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(vocab);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vocabList.size();
    }

    public static class VocabViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord, tvMeaning, tvTopic;

        public VocabViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
            tvTopic = itemView.findViewById(R.id.tvTopic);
        }
    }
}