module com.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.base;
    requires javafx.graphics;
    requires jdk.jsobject;
    requires java.desktop;

    opens com.demo.controllers to javafx.fxml;


    // Export your main package so JavaFX can reflectively construct it
    exports com.demo;
}