package com.demo;

import java.util.List;

public class EWasteCenter {
    private String name;
    private String location;
    private boolean govtApproved;
    private boolean acceptsHazardousWaste;
    private List<String> acceptedMaterials;

    public EWasteCenter(String name, String location, boolean govtApproved, boolean acceptsHazardousWaste, List<String> acceptedMaterials) {
        this.name = name;
        this.location = location;
        this.govtApproved = govtApproved;
        this.acceptsHazardousWaste = acceptsHazardousWaste;
        this.acceptedMaterials = acceptedMaterials;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public boolean isGovtApproved() { return govtApproved; }
    public boolean acceptsHazardousWaste() { return acceptsHazardousWaste; }
    public List<String> getAcceptedMaterials() { return acceptedMaterials; }

    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
}

