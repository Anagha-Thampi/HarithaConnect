package com.demo;

import java.time.LocalDateTime;

public class Complaint {
    private String description;
    private String status;
    private LocalDateTime dateFiled;
    private String filedBy;

    public Complaint(String description, String status, LocalDateTime dateFiled, String filedBy) {
        this.description = description;
        this.status = status;
        this.dateFiled = dateFiled;
        this.filedBy = filedBy;
    }

    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getDateFiled() { return dateFiled; }
    public String getFiledBy() { return filedBy; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Complaint: " + description + " (" + status + ")";
    }
}
