package com.demo;

import java.util.List;

public class EWasteCenter {
    private final String name;
    private final String location;
    private final boolean isGovtApproved;
    private final boolean isOpenNow;
    private final List<String> acceptedWasteTypes;

    public EWasteCenter(String name, String location, boolean isGovtApproved, boolean isOpenNow, List<String> acceptedWasteTypes) {
        this.name = name;
        this.location = location;
        this.isGovtApproved = isGovtApproved;
        this.isOpenNow = isOpenNow;
        this.acceptedWasteTypes = acceptedWasteTypes;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public boolean isGovtApproved() { return isGovtApproved; }
    public boolean isOpenNow() { return isOpenNow; }
    public List<String> getAcceptedWasteTypes() { return acceptedWasteTypes; }

    @Override
    public String toString() { return name + " (" + location + ")"; }
}
