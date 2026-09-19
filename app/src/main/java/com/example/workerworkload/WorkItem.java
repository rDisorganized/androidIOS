package com.example.workerworkload;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class WorkItem {
    @Exclude
    public String id;
    public String workerName;
    public String workType;
    public double quantity;
    public String unit;
    public double hours;
    public double productivity;
    public long timestamp;

    public WorkItem() {
    }

    public WorkItem(String workerName, String workType, double quantity,
                    String unit, double hours, double productivity) {
        this.workerName = workerName;
        this.workType = workType;
        this.quantity = quantity;
        this.unit = unit;
        this.hours = hours;
        this.productivity = productivity;
        this.timestamp = System.currentTimeMillis();
    }

    @Exclude
    public boolean sameContent(WorkItem other) {
        if (other == null) {
            return false;
        }
        return timestamp == other.timestamp
                && Double.compare(quantity, other.quantity) == 0
                && Double.compare(hours, other.hours) == 0
                && Double.compare(productivity, other.productivity) == 0
                && eq(workerName, other.workerName)
                && eq(workType, other.workType)
                && eq(unit, other.unit);
    }

    private static boolean eq(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }
}
