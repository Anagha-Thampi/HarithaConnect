package com.demo;

import java.time.LocalDateTime;

public class WastePickup {
    private String wasteType;
    private LocalDateTime scheduledDate;
    private String location;
    private String status;
    private boolean recurring;

    public WastePickup(String wasteType, LocalDateTime scheduledDate, String location, String status, boolean recurring) {
        this.wasteType = wasteType;
        this.scheduledDate = scheduledDate;
        this.location = location;
        this.status = status;
        this.recurring = recurring;
    }

    public String getWasteType() { return wasteType; }
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public boolean isRecurring() { return recurring; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Pickup for " + wasteType + " at " + location + " on " + scheduledDate + " (Status: " + status + ")";
    }
}
