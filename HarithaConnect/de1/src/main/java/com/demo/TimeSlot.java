package com.demo;

import java.io.*;
import java.util.*;

public class TimeSlot {
    private final String slotId;
    private final String workerUsername;
    private final String date;       // NEW: yyyy-MM-dd
    private final String timeSlot;   // e.g. "09:00-10:00"
    private int capacity;
    private boolean isOpen;

    // Constructor used when creating new programmatically (keeps old compatibility)
    public TimeSlot(String timeSlot, int capacity) {
        this.slotId = UUID.randomUUID().toString();
        this.workerUsername = "unknown";
        this.date = ""; // unspecified
        this.timeSlot = timeSlot;
        this.capacity = capacity;
        this.isOpen = capacity > 0;
    }

    // Full constructor (used when loading from CSV)
    public TimeSlot(String slotId, String workerUsername, String date, String timeSlot, int capacity, boolean isOpen) {
        this.slotId = slotId;
        this.workerUsername = workerUsername;
        this.date = date;
        this.timeSlot = timeSlot;
        this.capacity = capacity;
        this.isOpen = isOpen;
    }

    // getters / setters
    public String getSlotId() { return slotId; }
    public String getWorkerUsername() { return workerUsername; }
    public String getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public int getCapacity() { return capacity; }
    public boolean isOpen() { return isOpen; }

    public void setCapacity(int capacity) { this.capacity = capacity; this.isOpen = capacity > 0; }
    public void setOpen(boolean open) { this.isOpen = open; }

    // CSV path - adjust if your project uses a different folder
    private static final String TIMESLOTS_CSV = "src/main/resources/com/demo/data/timeslots.csv";

    /**
     * Load slots from CSV for a particular date.
     * returns ALL slots for that date (open/closed both) — controller will filter .isOpen()
     */
    public static List<TimeSlot> loadSlotsForDate(String date) {
        List<TimeSlot> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(TIMESLOTS_CSV))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                // expected: slotId,workerUsername,date,timeSlot,capacity,isOpen
                if (parts.length < 6) continue;
                String slotId = parts[0].trim();
                String worker = parts[1].trim();
                String csvDate = parts[2].trim();
                String tslot = parts[3].trim();
                int cap = Integer.parseInt(parts[4].trim());
                boolean open = Boolean.parseBoolean(parts[5].trim());
                if (csvDate.equals(date)) {
                    out.add(new TimeSlot(slotId, worker, csvDate, tslot, cap, open));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Update a slot row in timeslots.csv (matching by slotId).
     * Preserves header.
     */
    public static void updateSlotInCSV(TimeSlot updated) {
        List<String> outLines = new ArrayList<>();
        String header = null;
        try (BufferedReader br = new BufferedReader(new FileReader(TIMESLOTS_CSV))) {
            header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 6) {
                    outLines.add(line);
                    continue;
                }
                String slotId = parts[0].trim();
                if (slotId.equals(updated.getSlotId())) {
                    // write updated row: slotId,workerUsername,date,timeSlot,capacity,isOpen
                    String newRow = String.join(",",
                            updated.getSlotId(),
                            updated.getWorkerUsername(),
                            updated.getDate(),
                            updated.getTimeSlot(),
                            String.valueOf(updated.getCapacity()),
                            String.valueOf(updated.isOpen()));
                    outLines.add(newRow);
                } else {
                    outLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // write back
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TIMESLOTS_CSV, false))) {
            if (header != null) bw.write(header + "\n");
            for (String l : outLines) {
                bw.write(l + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
