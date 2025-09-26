package com.demo;

import java.util.List;

public class Heatmap {

    public void generateHeatmap(List<DumpReport> reports) {
        System.out.println("Generating heatmap with " + reports.size() + " reports...");
    }

    public void showDumpHotspots() {
        System.out.println("Showing dump hotspots on heatmap...");
    }
}