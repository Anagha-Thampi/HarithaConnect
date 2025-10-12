package com.demo;

public class WastePickup {
    private String pickupId;
    private final String wasteType;
    private String timeSlot;
    private final String location;
    private String status;
    private String assignedWorker;

    public WastePickup(String wasteType, String location) {
        this.pickupId = null;
        this.wasteType = wasteType;
        this.timeSlot = "";
        this.location = location;
        this.status = "Pending";
        this.assignedWorker = "";
    }

    // setters / getters
    public String getPickupId() { return pickupId; }
    public void setPickupId(String pickupId) { this.pickupId = pickupId; }

    public String getWasteType() { return wasteType; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedWorker() { return assignedWorker; }
    public void setAssignedWorker(String assignedWorker) { this.assignedWorker = assignedWorker; }
}
