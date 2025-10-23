package com.demo;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class LocalUser extends User {
    private String name, mobile, ward, address;

    public LocalUser(String username, String name, String mobile, String ward, String address) {
        super(username, null); // ✅ initialize parent field
        this.name = name;
        this.mobile = mobile;
        this.ward = ward;
        this.address = address;
    }

    public LocalUser(String username, String password) {
        super(username, password);
    }
    public String getName() { return name; }
    public String getMobile() { return mobile; }
    public String getWard() { return ward; }
    public String getAddress() { return address; }


    @Override
    public String getDataFileName() {
        return "localuserdata.csv";
    }
    public String getUsername() {
        return username;
    }

    public void selectWasteType(String type) {
        // optional: store preference (not persisted here)
        System.out.println(username + " selected waste type: " + type);
    }

    /**
     * Schedule pickup by choosing a timeslot id (from timeslots.csv).
     * - timeslots.csv columns: slotId,workerUsername,timeSlot,capacity,isOpen
     * - pickups.csv columns: pickupId,username,wasteType,timeSlotId,location,status,assignedWorker,createdAt
     */
    public boolean scheduleDoorstepPickup(String timeSlotId, WastePickup pickup) {
        List<String[]> slots = DataManager.readCsv("timeslots.csv");
        Optional<String[]> slotOpt = slots.stream().filter(s -> s.length>0 && s[0].equals(timeSlotId)).findFirst();
        if (!slotOpt.isPresent()) return false;
        String[] slot = slotOpt.get();
        boolean isOpen = Boolean.parseBoolean(slot[4]);
        if (!isOpen) return false;
        int capacity = Integer.parseInt(slot[3]);
        long bookings = DataManager.readCsv("pickups.csv").stream().filter(r -> r.length>3 && r[3].equals(timeSlotId)).count();
        if (bookings >= capacity) {
            // close slot
            slot[4] = "false";
            DataManager.overwriteCsv("timeslots.csv", slots);
            return false;
        }
        String pid = UUID.randomUUID().toString();
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        DataManager.appendCsv("pickups.csv", pid, username, pickup.getWasteType(), timeSlotId, pickup.getLocation(), "Scheduled", "", now);
        bookings++;
        if (bookings >= capacity) { slot[4] = "false"; DataManager.overwriteCsv("timeslots.csv", slots); }
        return true;
    }

    public List<EWasteCenter> findNearbyEWasteCenters(boolean govtApproved, boolean openNow) {
        List<EWasteCenter> out = new ArrayList<>();
        for (String[] row : DataManager.readCsv("ewaste.csv")) {
            // centerId,name,location,isGovtApproved,isOpenNow,acceptedTypes
            if (row.length < 6) continue;
            boolean isGovt = Boolean.parseBoolean(row[3]);
            boolean isOpen = Boolean.parseBoolean(row[4]);
            if ((govtApproved && !isGovt) || (openNow && !isOpen)) continue;
            List<String> accepted = Arrays.asList(row[5].split("\\|")).stream().map(String::trim).collect(Collectors.toList());
            out.add(new EWasteCenter(row[1], row[2], isGovt, isOpen, accepted));
        }
        return out;
    }

    public DumpReport reportPublicGarbageDump(String locationText, double lat, double lon, String details) {
        String coords = lat + "," + lon;
        DumpReport r = new DumpReport(locationText, coords, username, details);
        r.saveToCSV();
        return r;
    }

    public void viewHeatmap() {
        List<DumpReport> reports = DumpReport.readAllReports();
        MapView.showManyMarkers(reports);
    }

    public CleanestStreetBadge viewCleanestStreetBadge() {
        List<String[]> rows = DataManager.readCsv("badges.csv");
        if (rows.isEmpty()) return null;
        String[] last = rows.get(rows.size()-1);
        // badgeId,streetName,weekOfRecognition,issuedBy
        return new CleanestStreetBadge(last[1], last[2], last.length>3?last[3]:"");
    }

    public Complaint submitComplaint(String description) {
        String id = UUID.randomUUID().toString();
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        DataManager.appendCsv("complaints.csv", id, username, description, "Pending", now);
        return new Complaint(id, description, "Pending", now, username);
    }
}
