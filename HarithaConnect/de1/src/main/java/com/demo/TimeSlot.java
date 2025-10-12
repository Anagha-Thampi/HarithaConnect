package com.demo;

import java.util.UUID;

public class TimeSlot {
    private final String slotId;
    private final String timeSlot;
    private final int capacity;
    private boolean isOpen;

    public TimeSlot(String timeSlot, int capacity) {
        this.slotId = UUID.randomUUID().toString();
        this.timeSlot = timeSlot;
        this.capacity = capacity;
        this.isOpen = true;
    }

    public String getSlotId() { return slotId; }
    public String getTimeSlot() { return timeSlot; }
    public int getCapacity() { return capacity; }
    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { this.isOpen = open; }
}
