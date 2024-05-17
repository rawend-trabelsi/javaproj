package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController {

    @FXML
    private TextField username;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    private static final String URL = "jdbc:mysql://localhost:3307/agence";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    @FXML
    private void register() {
        String newUsername = username.getText();
        String userEmail = email.getText();
        String newPassword = password.getText();

        if (registerUser(newUsername, userEmail, newPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Inscription Réussie", "Utilisateur inscrit avec succès.");
            redirectToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur d'Inscription", "Erreur lors de l'inscription de l'utilisateur.");
        }
    }

    private boolean registerUser(String newUsername, String email, String newPassword) {
        String insertUserQuery = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, 'USER')";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement insertStatement = connection.prepareStatement(insertUserQuery)) {

            insertStatement.setString(1, newUsername);
            insertStatement.setString(2, email);
            insertStatement.setString(3, newPassword);
            int rowsAffected = insertStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void redirectToLogin() {
        try {
            Stage stage = (Stage) username.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/login.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
