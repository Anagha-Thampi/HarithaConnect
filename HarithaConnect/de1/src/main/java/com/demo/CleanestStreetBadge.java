package com.demo;

import javafx.beans.property.*;
import java.time.LocalDate;

public class CleanestStreetBadge {
    private final StringProperty streetName;
    private final ObjectProperty<LocalDate> weekOfRecognition;

    public CleanestStreetBadge(String streetName, LocalDate weekOfRecognition) {
        this.streetName = new SimpleStringProperty(streetName);
        this.weekOfRecognition = new SimpleObjectProperty<>(weekOfRecognition);
    }

    public StringProperty streetNameProperty() { return streetName; }
    public ObjectProperty<LocalDate> weekOfRecognitionProperty() { return weekOfRecognition; }

    public String getStreetName() { return streetName.get(); }
    public LocalDate getWeekOfRecognition() { return weekOfRecognition.get(); }

    @Override
    public String toString() {
        return getStreetName() + " (Week of: " + getWeekOfRecognition() + ")";
    }
}
