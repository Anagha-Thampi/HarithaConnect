package com.demo;

public class WastePickup {
    private String pickupId;
    private String username;
    private String wasteType;
    private String timeSlotId;
    private String location;
    private String status;
    private String assignedWorker;
    private String createdAt;

    public WastePickup(String pickupId, String username, String wasteType, String timeSlotId,
                       String location, String status, String assignedWorker, String createdAt) {
        this.pickupId = pickupId;
        this.username = username;
        this.wasteType = wasteType;
        this.timeSlotId = timeSlotId;
        this.location = location;
        this.status = status;
        this.assignedWorker = assignedWorker;
        this.createdAt = createdAt;
    }



    // Getters and Setters
    public String getPickupId() { return pickupId; }
    public void setPickupId(String pickupId) { this.pickupId = pickupId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getWasteType() { return wasteType; }
    public void setWasteType(String wasteType) { this.wasteType = wasteType; }

    public String getTimeSlot() { return timeSlotId; }
    public void setTimeSlot(String timeSlotId) { this.timeSlotId = timeSlotId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedWorker() { return assignedWorker; }
    public void setAssignedWorker(String assignedWorker) { this.assignedWorker = assignedWorker; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return pickupId + "," + username + "," + wasteType + "," + timeSlotId + "," +
                location + "," + status + "," + assignedWorker + "," + createdAt;
    }
}
