package com.demo;

public class PanchayatOfficial extends User {

    public PanchayatOfficial(String username, String password) {
        super(username, password);
    }

    public void viewRealTimeDumpReports() {
        System.out.println("Viewing real-time dump reports...");
    }

    public void updateReportStatus(String reportId, String status) {
        System.out.println("Report " + reportId + " updated to status: " + status);
    }

    public void viewHeatmap() {
        System.out.println("Viewing heatmap as Panchayat Official.");
    }

    public void viewCitizenEngagementStats() {
        System.out.println("Viewing citizen engagement stats.");
    }

    public void reviewComplaints() {
        System.out.println("Reviewing complaints...");
    }
    @Override
    public String getDataFileName() {
        return "panchayatuserdata.csv";
    }
}
