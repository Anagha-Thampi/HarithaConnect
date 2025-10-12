package com.demo;

public class Complaint {
    private final String complaintId;
    private final String description;
    private String status;
    private final String submittedDate;
    private final String submittedBy;

    public Complaint(String complaintId, String description, String status, String submittedDate, String submittedBy) {
        this.complaintId = complaintId;
        this.description = description;
        this.status = status;
        this.submittedDate = submittedDate;
        this.submittedBy = submittedBy;
    }

    public String getComplaintId() { return complaintId; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSubmittedDate() { return submittedDate; }
    public String getSubmittedBy() { return submittedBy; }
}
