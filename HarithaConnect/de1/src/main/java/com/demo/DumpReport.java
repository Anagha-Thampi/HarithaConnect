package com.demo;

import java.time.LocalDateTime;

public class DumpReport {
    private String location;
    private String status;
    private LocalDateTime reportDate;
    private String reportedBy;

    public DumpReport(String location, String status, LocalDateTime reportDate, String reportedBy) {
        this.location = location;
        this.status = status;
        this.reportDate = reportDate;
        this.reportedBy = reportedBy;
    }

    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public LocalDateTime getReportDate() { return reportDate; }
    public String getReportedBy() { return reportedBy; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Dump reported at " + location + " by " + reportedBy + " (" + status + ")";
    }
}
