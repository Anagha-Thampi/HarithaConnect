package com.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KudumbasreeWorker extends User {

    public KudumbasreeWorker(String username, String password) {
        super(username, password);
    }
    @Override
    public String getDataFileName() {
        return "kudumbasreeuserdata.csv";
    }
    public List<DumpReport> viewRealTimeDumpReports() {
        return DumpReport.readAllReports();
    }

    /** Add timeslot(s); timeslots.csv columns: slotId,workerUsername,timeSlot,capacity,isOpen */
    public void addAvailableTimeSlots(List<TimeSlot> slots) {
        for (TimeSlot s : slots) {
            DataManager.appendCsv("timeslots.csv", s.getSlotId(), username, s.getTimeSlot(), String.valueOf(s.getCapacity()), String.valueOf(s.isOpen()));
        }
    }

    public List<WastePickup> viewBookedPickups() {
        List<WastePickup> out = new ArrayList<>();
        for (String[] row : DataManager.readCsv("pickups.csv")) {
            // pickupId,username,wasteType,timeSlotId,location,status,assignedWorker,createdAt
            if (row.length < 8) continue;
            if (row[6].equals(username)) {
                WastePickup wp = new WastePickup(row[2], row[4]);
                wp.setPickupId(row[0]);
                wp.setTimeSlot(row[3]);
                wp.setAssignedWorker(row[6]);
                wp.setStatus(row[5]);
                out.add(wp);
            }
        }
        return out;
    }

    public boolean collectWaste(String pickupId) {
        List<String[]> rows = DataManager.readCsv("pickups.csv");
        boolean found = false;
        for (int i=0;i<rows.size();i++) {
            String[] r = rows.get(i);
            if (r.length>0 && r[0].equals(pickupId)) {
                r[5] = "Collected";
                rows.set(i, r);
                found = true; break;
            }
        }
        if (found) DataManager.overwriteCsv("pickups.csv", rows);
        return found;
    }

    public boolean updatePickupStatus(String pickupId, String status) {
        List<String[]> rows = DataManager.readCsv("pickups.csv");
        boolean changed = false;
        for (int i=0;i<rows.size();i++) {
            String[] r = rows.get(i);
            if (r.length>0 && r[0].equals(pickupId)) {
                r[5] = status; rows.set(i, r); changed = true; break;
            }
        }
        if (changed) DataManager.overwriteCsv("pickups.csv", rows);
        return changed;
    }
}
