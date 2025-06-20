package vn.edu.tlu.btln9.tuvung;
public class User {
    public String email;
    public String password;

    public User() {
        // Bắt buộc có constructor rỗng cho Firebase
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}