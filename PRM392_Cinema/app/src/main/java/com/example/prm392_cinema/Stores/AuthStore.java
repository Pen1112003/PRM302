package com.example.prm392_cinema.Stores;

public class AuthStore {
    public static String userId; // Changed to String
    public static String jwtToken; // Biến để lưu JWT token

    // Phương thức để xóa thông tin khi đăng xuất
    public static void clear() {
        userId = null; // Changed to null
        jwtToken = null;
    }
}
