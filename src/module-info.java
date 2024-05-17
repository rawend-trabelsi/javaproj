module AGENCE {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires java.desktop;
	requires java.xml.crypto;
	requires java.sql;
	requires javafx.base;

    // Exporter le package controller pour qu'il soit accessible à l'extérieur du module AGENCE
    exports controller;

    // Ouvrir le package controller pour permettre l'accès réflexif depuis javafx.fxml
    opens controller to javafx.fxml;

    // Ouvrir le package application pour permettre l'accès réflexif depuis javafx.fxml et javafx.graphics
    opens application to javafx.graphics, javafx.fxml;
}
