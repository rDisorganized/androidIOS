package com.example.workerworkload;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    static final String DB_URL =
            "https://project-doidoi-default-rtdb.asia-southeast1.firebasedatabase.app";

    private static FirebaseDatabase database;

    public static synchronized FirebaseDatabase getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance(DB_URL);
        }
        return database;
    }

    public static DatabaseReference getUserWorksRef() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("Chưa đăng nhập");
        }
        return getDatabase()
                .getReference("users")
                .child(user.getUid())
                .child("works");
    }
}
