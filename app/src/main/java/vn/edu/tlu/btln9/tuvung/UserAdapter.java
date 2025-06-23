package vn.edu.tlu.btln9.tuvung;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onDelete(String userId);
    }

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.txtEmail.setText(user.getEmail());
        holder.txtRole.setText("Quyền: " + user.getRole());

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(user.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ✅ Dùng để cập nhật lại danh sách khi lọc tìm kiếm
    public void updateData(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmail, txtRole;
        Button btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtRole = itemView.findViewById(R.id.txtRole);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
