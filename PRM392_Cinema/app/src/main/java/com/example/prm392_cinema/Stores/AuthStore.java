package com.example.prm392_cinema.Stores;

public class AuthStore {
    public static int userId;
    public static String jwtToken; // Biến để lưu JWT token

    // Phương thức để xóa thông tin khi đăng xuất
    public static void clear() {
        userId = 0;
        jwtToken = null;
    }
}
