package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import Config.Connexion;

public class CreateHotelController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtCity;

    @FXML
    private TextField txtCountry;

    @FXML
    private TextField txtStars;

    @FXML
    private TextField txtDescription;

    @FXML
    private ImageView imageView;

    private File selectedImageFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (root != null) {
            root.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
        } else {
            System.err.println("L'élément root est null. Vérifiez votre fichier FXML.");
        }
    }

    @FXML
    private void onBtnChooseImageAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif")
        );
        selectedImageFile = fileChooser.showOpenDialog(root.getScene().getWindow());

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imageView.setImage(image);
        }
    }
    @FXML
    private void onBtnSaveAction(ActionEvent event) {
        String name = txtName.getText();
        String address = txtAddress.getText();
        String city = txtCity.getText();
        String country = txtCountry.getText();
        String stars = txtStars.getText();
        String description = txtDescription.getText();

        if (name.isEmpty() || address.isEmpty() || city.isEmpty() || country.isEmpty() || stars.isEmpty() || description.isEmpty() || selectedImageFile == null) {
            System.out.println("Veuillez remplir tous les champs et choisir une image.");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = Connexion.getConnection();
            String query = "INSERT INTO hotel (id_hotel, nom, adresse, ville, pays, etoiles, description, image_url) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, city);
            stmt.setString(4, country);
            stmt.setString(5, stars);
            stmt.setString(6, description);
            stmt.setString(7, "../images/" + selectedImageFile.getName());

            stmt.executeUpdate();
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int hotelId = generatedKeys.getInt(1);
                System.out.println("Hôtel ajouté avec succès. ID : " + hotelId);

                // Enregistrer l'image dans le répertoire images
                saveImage(new FileInputStream(selectedImageFile), selectedImageFile.getName());

                clearFields();
            } else {
                System.out.println("Échec de l'ajout de l'hôtel.");
            }
        } catch (SQLException | FileNotFoundException e) {
            System.out.println("Erreur lors de l'ajout de l'hôtel : " + e.getMessage());
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }}
    

    private void saveImage(FileInputStream fileInputStream, String name) {
        // Chemin relatif du répertoire "images"
        String imagesDirectory = "images";

        // Obtenir le chemin absolu du répertoire "images"
        Path absolutePath = Paths.get(imagesDirectory).toAbsolutePath();

        // Créer le répertoire s'il n'existe pas
        File directory = new File(absolutePath.toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Enregistrer l'image
        try (FileOutputStream outputStream = new FileOutputStream(absolutePath.resolve(name).toString())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Image enregistrée avec succès : " + absolutePath.resolve(name));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


	@FXML
    private void onBtnCancelAction(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        txtName.clear();
        txtAddress.clear();
        txtCity.clear();
        txtCountry.clear();
        txtStars.clear();
        txtDescription.clear();
        imageView.setImage(null);
        selectedImageFile = null;
    }
}
