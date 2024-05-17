package controller;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.User;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        
        User user = authenticate(username, password);

        if (user != null) {
            try {
                redirectToDashboard(user);
            } catch (IOException e) {
                showLoginErrorAlert("Erreur lors du chargement de la page : " + e.getMessage());
            }
        } else {
            showLoginErrorAlert("Nom d'utilisateur ou mot de passe incorrect !");
        }
    }
    @FXML
    private void handleSignUpLinkClick() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("signin.fxml"));



            AnchorPane signupPane = loader.load();

            Scene scene = new Scene(signupPane);
            stage.setScene(scene);
        } catch (IOException e) {
            showLoginErrorAlert("Erreur lors du chargement de la page d'inscription : " + e.getMessage());
        }
    }



    private void redirectToDashboard(User user) throws IOException {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader;
        Parent root;

        if ("ADMIN".equals(user.getRole())) {
            loader = new FXMLLoader(getClass().getResource("tableau.fxml"));
            root = loader.load();
        } else {
            loader = new FXMLLoader(getClass().getResource("home.fxml"));
            root = loader.load();
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }



    private void showLoginErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de Connexion");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

  

    private User authenticate(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/agence","root","");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                if (password.equals(storedPassword)) {
                    String role = resultSet.getString("role");
                    return new User(username, role);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
