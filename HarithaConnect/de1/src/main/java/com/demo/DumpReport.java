package com.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DumpReport {
    private final String reportId;
    private final String locationText;   // optional textual description
    private final String coordinates;    // "lat,lon"
    private String status;               // Pending / Resolved
    private final String reportedDate;   // ISO string
    private final String reportedBy;
    private final String details;        // optional details

    public DumpReport(String reportId, String locationText, String coordinates,
                      String status, String reportedDate, String reportedBy, String details) {
        this.reportId = reportId;
        this.locationText = locationText;
        this.coordinates = coordinates;
        this.status = status;
        this.reportedDate = reportedDate;
        this.reportedBy = reportedBy;
        this.details = details;
    }

    // convenience constructor for new reports (id & date auto)
    public DumpReport(String locationText, String coordinates, String reportedBy, String details) {
        this(UUID.randomUUID().toString(), locationText, coordinates, "Pending",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                reportedBy, details);
    }

    public String getReportId() { return reportId; }
    public String getLocationText() { return locationText; }
    public String getCoordinates() { return coordinates; }
    public String getStatus() { return status; }
    public String getReportedDate() { return reportedDate; }
    public String getReportedBy() { return reportedBy; }
    public String getDetails() { return details; }

    public void setStatus(String status) { this.status = status; }

    /** Save this report to dump_reports.csv */
    public void saveToCSV() {
        // columns: reportId,locationText,coordinates,status,reportedDate,reportedBy,details
        DataManager.appendCsv("dump_reports.csv", reportId, locationText, coordinates, status, reportedDate, reportedBy, details);
    }

    /** Opens selected location on the map (via MapView with show marker) */
    public void showOnMap() {
        try {
            String[] parts = coordinates.split(",");
            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());
            MapView.showLocation(lat, lon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Read all reports from CSV into objects */
    public static List<DumpReport> readAllReports() {
        List<DumpReport> out = new ArrayList<>();
        for (String[] r : DataManager.readCsv("dump_reports.csv")) {
            // expect 7 columns
            if (r.length < 7) continue;
            out.add(new DumpReport(r[0], r[1], r[2], r[3], r[4], r[5], r[6]));
        }
        return out;
    }

    @Override
    public String toString() {
        return "DumpReport{" + reportId + "," + locationText + "," + coordinates + "," + status + "}";
    }
}
