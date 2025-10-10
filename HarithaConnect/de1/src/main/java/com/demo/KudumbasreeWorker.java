package com.demo;

import java.util.List;

public class KudumbasreeWorker extends User {

    public KudumbasreeWorker(String username, String password) {
        super(username, password);
    }
    @Override
    public String getDataFileName() {
        return "kudumbasreeuserdata.csv";
    }

    public void viewRealTimeDumpReports() {
        System.out.println("Viewing real-time dump reports (worker)...");
    }

    public void addAvailableTimeSlots(List<String> timeSlots) {
        System.out.println("Available timeslots added: " + timeSlots);
    }

    public void viewBookedPickups() {
        System.out.println("Viewing booked pickups...");
    }

    public void collectWaste(String pickupId) {
        System.out.println("Collected waste at pickup ID: " + pickupId);
    }

    public void updatePickupStatus(String pickupId, String status) {
        System.out.println("Pickup " + pickupId + " updated to: " + status);
    }
}
