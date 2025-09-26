package com.demo.controllers;

import javafx.beans.property.*;

public class StreetScore {
    private final IntegerProperty rank;
    private final StringProperty street;
    private final IntegerProperty points;

    public StreetScore(int rank, String street, int points) {
        this.rank = new SimpleIntegerProperty(rank);
        this.street = new SimpleStringProperty(street);
        this.points = new SimpleIntegerProperty(points);
    }

    public IntegerProperty rankProperty() { return rank; }
    public StringProperty streetProperty() { return street; }
    public IntegerProperty pointsProperty() { return points; }
}
