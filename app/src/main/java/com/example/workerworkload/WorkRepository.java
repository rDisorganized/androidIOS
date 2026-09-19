package com.example.workerworkload;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tải lịch sử một lần kết nối, giới hạn 100 bản ghi mới nhất,
 * gỡ listener khi rời màn hình để tránh leak / load trùng.
 */
public class WorkRepository {

    public interface Callback {
        void onLoaded(List<WorkItem> items);

        void onError(String message);
    }

    private Query query;
    private ValueEventListener listener;

    public void start(Callback callback) {
        stop();
        query = FirebaseHelper.getUserWorksRef()
                .orderByChild("timestamp")
                .limitToLast(100);
        query.keepSynced(true);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<WorkItem> items = new ArrayList<>((int) snapshot.getChildrenCount());
                for (DataSnapshot child : snapshot.getChildren()) {
                    WorkItem item = child.getValue(WorkItem.class);
                    if (item == null) {
                        continue;
                    }
                    item.id = child.getKey();
                    items.add(item);
                }
                Collections.reverse(items);
                callback.onLoaded(items);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        };
        query.addValueEventListener(listener);
    }

    public void stop() {
        if (query != null && listener != null) {
            query.removeEventListener(listener);
        }
        listener = null;
        query = null;
    }
}
