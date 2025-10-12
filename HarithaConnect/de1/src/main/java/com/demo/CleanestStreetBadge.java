package com.demo;

public class CleanestStreetBadge {
    private final String streetName;
    private final String weekOfRecognition;
    private final String issuedBy;

    public CleanestStreetBadge(String streetName, String weekOfRecognition, String issuedBy) {
        this.streetName = streetName;
        this.weekOfRecognition = weekOfRecognition;
        this.issuedBy = issuedBy;
    }

    public String getStreetName() { return streetName; }
    public String getWeekOfRecognition() { return weekOfRecognition; }
    public String getIssuedBy() { return issuedBy; }

    @Override
    public String toString() { return streetName + " (" + weekOfRecognition + ")"; }
}

