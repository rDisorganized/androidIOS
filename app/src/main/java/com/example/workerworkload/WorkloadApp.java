package com.example.workerworkload;

import android.app.Application;

public class WorkloadApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseHelper.getDatabase().setPersistenceEnabled(true);
        } catch (Exception ignored) {
            // persistence chỉ được bật một lần trong đời process
        }
    }
}
