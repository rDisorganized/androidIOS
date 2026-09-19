package com.example.workerworkload;

public class WorkItem {
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
}
