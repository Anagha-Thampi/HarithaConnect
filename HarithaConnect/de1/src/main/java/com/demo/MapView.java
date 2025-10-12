package com.demo;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.List;

/**
 * Lightweight map display: show a single location or multiple markers (heatmap).
 */
public class MapView {

    public static void showLocation(double lat, double lon) {
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                WebView webView = new WebView();
                WebEngine engine = webView.getEngine();
                engine.load(MapView.class.getResource("/com/demo/map.html").toExternalForm());
                engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
                    if (newDoc != null) {
                        engine.executeScript("showLocation(" + lat + "," + lon + ")");
                    }
                });
                stage.setTitle("Location View");
                stage.setScene(new Scene(webView, 800, 600));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /** Show many dump points on heatmap page (heatmap.html expects addMarker(lat,lon) JS function) */
    public static void showManyMarkers(List<DumpReport> reports) {
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                WebView webView = new WebView();
                WebEngine engine = webView.getEngine();
                engine.load(MapView.class.getResource("/com/demo/heatmap.html").toExternalForm());
                engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
                    if (newDoc != null) {
                        for (DumpReport r : reports) {
                            try {
                                String[] parts = r.getCoordinates().split(",");
                                double lat = Double.parseDouble(parts[0].trim());
                                double lon = Double.parseDouble(parts[1].trim());
                                engine.executeScript("addMarker(" + lat + "," + lon + ")");
                            } catch (Exception ignored) {}
                        }
                    }
                });
                stage.setTitle("Dump Hotspots");
                stage.setScene(new Scene(webView, 900, 700));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
