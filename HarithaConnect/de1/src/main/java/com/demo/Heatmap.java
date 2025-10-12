package com.demo;

import java.util.*;
import java.util.stream.Collectors;

/** Simple heatmap data generator (counts reports per location) */
public class HeatMap {
    private final Map<String,Integer> density = new HashMap<>();

    public void generateHeatmap(List<DumpReport> reports) {
        density.clear();
        for (DumpReport r : reports) {
            String coords = r.getCoordinates();
            density.put(coords, density.getOrDefault(coords, 0) + 1);
        }
    }

    public List<Map.Entry<String,Integer>> getTopHotspots(int topN) {
        return density.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    public Map<String,Integer> getDensity() { return density; }
}
