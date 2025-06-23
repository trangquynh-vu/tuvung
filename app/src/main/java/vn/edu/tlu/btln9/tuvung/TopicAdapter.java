package vn.edu.tlu.btln9.tuvung;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private final Context context;
    private final List<Topic> topicList;

    public TopicAdapter(Context context, List<Topic> topicList) {
        this.context = context;
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.txtTitle.setText(topic.getTitle());
        holder.txtDescription.setText(topic.getDescription());

        // ✅ Xử lý nút sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTopicActivity.class);
            intent.putExtra("topicId", topic.getTopicId());
            intent.putExtra("title", topic.getTitle());
            intent.putExtra("description", topic.getDescription());
            context.startActivity(intent);
        });

        // ✅ Xử lý nút xoá
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xoá chủ đề")
                    .setMessage("Bạn có chắc muốn xoá chủ đề này?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        FirebaseDatabase.getInstance()
                                .getReference("topic").child("topics")
                                .child(topic.getTopicId())
                                .removeValue()
                                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Đã xoá", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    // ✅ ViewHolder
    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription;
        Button btnEdit, btnDelete;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
