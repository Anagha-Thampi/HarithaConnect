package com.demo;

import com.demo.controllers.SchedulePickupController;

public class MapBridge {
    private final SchedulePickupController controller;

    public MapBridge(SchedulePickupController controller) {
        this.controller = controller;
    }

    // This is what JavaScript calls
    public void updateCoords(String coords) {
        System.out.println("JS -> Java: received " + coords);
        controller.updateCoordinates(coords);
    }
}

