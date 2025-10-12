package com.demo;

import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Opens a modal WebView with Leaflet map (map_selector.html).
 * Returns [lat,lon] chosen by user (or [0,0] if none).
 *
 * Implementation uses WebEngine alert() to pass coordinates to Java.
 */
public class MapSelector {
    private static double selectedLat = 0.0;
    private static double selectedLon = 0.0;

    public static double[] openMapAndSelect() {
        selectedLat = 0.0; selectedLon = 0.0;
        Stage stage = new Stage();
        stage.setTitle("Select Location on Map");

        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        engine.setOnAlert(event -> {
            try {
                String data = event.getData(); // "lat,lon"
                String[] parts = data.split(",");
                selectedLat = Double.parseDouble(parts[0]);
                selectedLon = Double.parseDouble(parts[1]);
                // close window once selection is made
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        engine.load(MapSelector.class.getResource("/com/demo/map_selector.html").toExternalForm());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(webView, 800, 600));
        stage.showAndWait();

        return new double[]{selectedLat, selectedLon};
    }
}
