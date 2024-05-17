package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label; 
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Button addButton; 
    @FXML
    private Label dashboardLabel;
 
    public void initialize() {
    	 dashboardLabel.setText("Bienvenue sur le Tableau de Bord !");
    	
    }

  
    @FXML
    private void handleManageTrips() {

        System.out.println("Gestion des voyages");
    }

    // Méthode pour gérer l'événement de gestion des réservations
    @FXML
    private void handleManageBookings() {
        // Code pour gérer la gestion des réservations
        System.out.println("Gestion des réservations");
    }

    // Méthode pour gérer l'événement d'ajout d'hôtel
    @FXML
    private void handleAddHotel() {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/createhotel.fxml"));
            Parent root = loader.load();
            
            // Créer une nouvelle scène
            Scene scene = new Scene(root);
            
            // Obtenir la scène actuelle à partir du bouton "ajout hotel"
            Stage stage = (Stage) dashboardLabel.getScene().getWindow();
            
            // Changer la scène
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleListHotels() {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/listehotel.fxml"));
            Parent root = loader.load();
            
            // Créer une nouvelle scène
            Scene scene = new Scene(root);
            
            // Obtenir la scène actuelle à partir du bouton "liste des hôtels"
            Stage stage = (Stage) dashboardLabel.getScene().getWindow();
            
            // Changer la scène
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
