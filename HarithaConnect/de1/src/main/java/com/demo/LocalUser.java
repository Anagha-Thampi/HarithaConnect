package com.demo;

public class LocalUser extends User {

    public LocalUser(String username, String password) {
        super(username, password);
    }
    @Override
    public String getDataFileName() {
        return "localuserdata.csv";
    }

    public void selectWasteType(String type) {
        System.out.println("Selected waste type: " + type);
    }

    public void scheduleDoorstepPickup(String timeSlot) {
        System.out.println("Pickup scheduled at: " + timeSlot);
    }

    public void findNearbyEWasteCenters(boolean govtApproved, boolean openNow) {
        System.out.println("Finding e-waste centers (govtApproved=" + govtApproved + ", openNow=" + openNow + ")");
    }

    public void reportPublicGarbageDump(String location) {
        System.out.println("Reported garbage dump at: " + location);
    }

    public void viewHeatmap() {
        System.out.println("Viewing heatmap...");
    }

    public void viewCleanestStreetBadge() {
        System.out.println("Viewing cleanest street badge.");
    }

    public void submitComplaint(String description) {
        System.out.println("Complaint submitted: " + description);
    }
}