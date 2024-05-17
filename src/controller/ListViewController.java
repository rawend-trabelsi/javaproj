package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import models.Hotel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ResourceBundle;

public class ListViewController implements Initializable {

    @FXML
    private TableView<Hotel> tableView;

    private ObservableList<Hotel> hotels = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadHotelData();
        setupTable();
    }

    private void loadHotelData() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/agence", "root", "")) {
            String query = "SELECT * FROM hotel";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id_hotel");
                String name = resultSet.getString("nom");
                String address = resultSet.getString("adresse");
                String city = resultSet.getString("ville");
                String country = resultSet.getString("pays");
                int stars = resultSet.getInt("etoiles");
                String description = resultSet.getString("description");
                String imageUrl = resultSet.getString("image_url");

                Hotel hotel = new Hotel(id, name, address, city, country, stars, description, imageUrl);
                hotels.add(hotel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTable() {
        TableColumn<Hotel, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Hotel, String> addressColumn = new TableColumn<>("Adresse");
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());

        TableColumn<Hotel, String> cityColumn = new TableColumn<>("Ville");
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());

        TableColumn<Hotel, String> countryColumn = new TableColumn<>("Pays");
        countryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());

        TableColumn<Hotel, Integer> starsColumn = new TableColumn<>("Étoiles");
        starsColumn.setCellValueFactory(cellData -> cellData.getValue().starsProperty().asObject());

        TableColumn<Hotel, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        TableColumn<Hotel, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            try {
                String imageUrl = cellData.getValue().getImageUrl();
                Image image = new Image(new FileInputStream("images/" + imageUrl)); // Chemin relatif vers le dossier "images"
                imageView.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return new SimpleObjectProperty<>(imageView);
        });

        // Colonne pour le bouton "Modifier"
        TableColumn<Hotel, Void> editColumn = new TableColumn<>("Modifier");
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");

            {
                editButton.setOnAction(event -> {
                    Hotel hotel = getTableView().getItems().get(getIndex());
                    editHotel(hotel);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

       
        TableColumn<Hotel, Void> deleteColumn = new TableColumn<>("Supprimer");
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Hotel hotel = getTableView().getItems().get(getIndex());
                    deleteHotel(hotel);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        tableView.getColumns().addAll(nameColumn, addressColumn, cityColumn, countryColumn,
                starsColumn, descriptionColumn, imageColumn, editColumn, deleteColumn);

        tableView.setItems(hotels);
    }

    private void editHotel(Hotel hotel) {
        Dialog<Hotel> dialog = new Dialog<>();
        dialog.setTitle("Modifier les détails de l'hôtel");
        dialog.setHeaderText("Modifier les détails de l'hôtel : " + hotel.getName());

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField(hotel.getName());
        TextField addressField = new TextField(hotel.getAddress());
        TextField cityField = new TextField(hotel.getCity());
        TextField countryField = new TextField(hotel.getCountry());
        TextField starsField = new TextField(String.valueOf(hotel.getStars()));
        TextArea descriptionArea = new TextArea(hotel.getDescription());
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        try {
            Image image = new Image(new FileInputStream("images/" + hotel.getImageUrl()));
            imageView.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Bouton Parcourir pour changer l'image
        Button browseButton = new Button("Parcourir");
        browseButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une nouvelle image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (selectedFile != null) {
                try {
                    // Mettre à jour l'URL de l'image avec le chemin complet de la nouvelle image
                    String newImageUrl = selectedFile.getName();
                    hotel.setImageUrl(newImageUrl);

                    // Mettre à jour l'ImageView avec la nouvelle image sélectionnée
                    Image newImage = new Image(new FileInputStream(selectedFile));
                    imageView.setImage(newImage);
                    
                    // Appel de la méthode saveChanges avec le fichier de la nouvelle image
                    saveChanges(hotel, selectedFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        // Ajoutez les champs de saisie, l'ImageView et le bouton Parcourir à un GridPane ou un VBox
        dialog.getDialogPane().setContent(new VBox(10, nameField, addressField, cityField, countryField, starsField, descriptionArea, imageView, browseButton));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                hotel.setName(nameField.getText());
                hotel.setAddress(addressField.getText());
                hotel.setCity(cityField.getText());
                hotel.setCountry(countryField.getText());
                hotel.setStars(Integer.parseInt(starsField.getText()));
                hotel.setDescription(descriptionArea.getText());
                // Les modifications de l'image sont déjà sauvegardées dans l'objet Hotel
                // saveChanges(hotel); // Supprimer cette ligne, car nous avons déjà appelé saveChanges avec le fichier de la nouvelle image
                return hotel;
            }
            return null;
        });

        dialog.showAndWait();
    }





    private void deleteHotel(Hotel hotel) {
        hotels.remove(hotel);
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/agence", "root", "")) {
            String query = "DELETE FROM hotel WHERE id_hotel=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, hotel.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveChanges(Hotel hotel, File newImageFile) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/agence", "root", "")) {
            String query = "UPDATE hotel SET nom=?, adresse=?, ville=?, pays=?, etoiles=?, description=?, image_url=? WHERE id_hotel=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, hotel.getName());
            statement.setString(2, hotel.getAddress());
            statement.setString(3, hotel.getCity());
            statement.setString(4, hotel.getCountry());
            statement.setInt(5, hotel.getStars());
            statement.setString(6, hotel.getDescription());
            
            // Vérifie si un nouveau fichier image a été sélectionné
            if (newImageFile != null) {
                // Copie le fichier sélectionné vers le répertoire d'images
                File destinationFile = new File("images/" + newImageFile.getName());
                Files.copy(newImageFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // Met à jour l'URL de l'image dans l'objet Hotel avec le nouveau chemin
                hotel.setImageUrl(newImageFile.getName());
            }
            
            // Utilise le chemin d'accès de l'image mis à jour
            statement.setString(7, hotel.getImageUrl());
            statement.setInt(8, hotel.getId());
            statement.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }



      
    
}
