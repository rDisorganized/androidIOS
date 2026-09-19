package com.example.workerworkload;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    private static final String DB_URL =
            "https://project-doidoi-default-rtdb.asia-southeast1.firebasedatabase.app";

    public static DatabaseReference getUserWorksRef() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return FirebaseDatabase.getInstance(DB_URL)
                .getReference("users")
                .child(uid)
                .child("works");
    }
}
