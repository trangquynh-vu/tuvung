package vn.edu.tlu.btln9.tuvung;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText edtSearch;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private List<User> allUsers = new ArrayList<>(); // giữ toàn bộ danh sách gốc
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ánh xạ view
        edtSearch = findViewById(R.id.edtSearch);
        recyclerView = findViewById(R.id.recyclerUsers);

        // RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(userList, this::deleteUser);
        recyclerView.setAdapter(adapter);

        // Firebase
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        loadUsers();

        // Lắng nghe sự kiện tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                allUsers.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String id = userSnap.getKey();
                    String email = userSnap.child("email").getValue(String.class);
                    String password = userSnap.child("password").getValue(String.class);
                    String role = userSnap.child("role").getValue(String.class);

                    if (email != null && role != null && !role.equals("admin")) {
                        User user = new User(id, email, password, role);
                        userList.add(user);
                        allUsers.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageUsersActivity.this, "Lỗi tải người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser(String userId) {
        usersRef.child(userId).removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                    loadUsers(); // refresh
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi xoá", Toast.LENGTH_SHORT).show());
    }

    private void filterUsers(String keyword) {
        List<User> filteredList = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                    user.getRole().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(user);
            }
        }

        userList.clear();
        userList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
