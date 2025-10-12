package com.demo;

import java.util.*;
import java.util.stream.Collectors;

public class PanchayatOfficial extends User {
    public PanchayatOfficial(String username, String password) {
        super(username, password);
    }
    @Override
    public String getDataFileName() {
        return "panchayatuserdata.csv";
    }
    public List<DumpReport> viewRealTimeDumpReports() {
        return DumpReport.readAllReports();
    }

    public boolean updateReportStatus(String reportId, String newStatus) {
        List<String[]> rows = DataManager.readCsv("dump_reports.csv");
        boolean found = false;
        for (int i=0;i<rows.size();i++) {
            String[] r = rows.get(i);
            if (r.length>0 && r[0].equals(reportId)) {
                r[3] = newStatus; // status is column index 3
                rows.set(i, r);
                found = true;
                break;
            }
        }
        if (found) DataManager.overwriteCsv("dump_reports.csv", rows);
        return found;
    }

    public Map<String,Integer> viewCitizenEngagementStats() {
        Map<String,Integer> stats = new LinkedHashMap<>();
        stats.put("complaints", DataManager.readCsv("complaints.csv").size());
        stats.put("reports", DataManager.readCsv("dump_reports.csv").size());
        stats.put("pickups", DataManager.readCsv("pickups.csv").size());
        return stats;
    }

    public List<Complaint> reviewComplaints() {
        List<Complaint> out = new ArrayList<>();
        for (String[] row : DataManager.readCsv("complaints.csv")) {
            if (row.length < 5) continue;
            out.add(new Complaint(row[0], row[2], row[3], row[4], row[1]));
        }
        return out;
    }

    public void issueBadge(CleanestStreetBadge badge) {
        // badges.csv: badgeId,streetName,weekOfRecognition,issuedBy
        String id = UUID.randomUUID().toString();
        DataManager.appendCsv("badges.csv", id, badge.getStreetName(), badge.getWeekOfRecognition(), badge.getIssuedBy());
    }
}
