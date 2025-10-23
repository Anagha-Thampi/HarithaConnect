package com.demo;

public class MapBridge {
    private final Object controller; // <-- accept any controller

    public MapBridge(Object controller) {
        this.controller = controller;
    }

    // Called by JavaScript when a map click happens
    public void updateCoords(String coords) {
        System.out.println("JS -> Java: received " + coords);

        try {
            controller.getClass().getMethod("updateCoordinates", String.class)
                    .invoke(controller, coords);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


