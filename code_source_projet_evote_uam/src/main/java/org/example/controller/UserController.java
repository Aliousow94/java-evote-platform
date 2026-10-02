package org.example.controller;

import org.example.dao.UserDAO;
import org.example.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML private TableView<User> usersTable; // <-- LA LIGNE MANQUANTE EST ICI
    @FXML private TableColumn<User, String> matriculeColumn;
    @FXML private TableColumn<User, String> ufrColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> departementColumn;
    @FXML private TableColumn<User, String> classeColumn;
    @FXML private TableColumn<User, String> roleColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        matriculeColumn.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        ufrColumn.setCellValueFactory(new PropertyValueFactory<>("ufr"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        departementColumn.setCellValueFactory(new PropertyValueFactory<>("departement"));
        classeColumn.setCellValueFactory(new PropertyValueFactory<>("classe"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        chargerUtilisateurs();
    }

    public void chargerUtilisateurs() {
        UserDAO userDAO = new UserDAO();
        List<User> usersList = userDAO.findAll();
        ObservableList<User> observableList = FXCollections.observableArrayList(usersList);
        usersTable.setItems(observableList);
    }

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addUser.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            chargerUtilisateurs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur dans le tableau.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addUser.fxml"));
            Parent root = loader.load();

            AddUserController controller = loader.getController();
            controller.setUserForEdit(selectedUser);

            Stage stage = new Stage();
            stage.setTitle("Modifier un utilisateur");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            chargerUtilisateurs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer " + selectedUser.getNom() + " " + selectedUser.getPrenom() + " ?");
        confirm.setContentText("Cette action est irréversible.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            UserDAO userDAO = new UserDAO();
            userDAO.delete(selectedUser.getId());
            chargerUtilisateurs();
        }
    }
    @FXML
    private void handleImportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner le fichier CSV des électeurs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return; // L'utilisateur a annulé

        UserDAO userDAO = new UserDAO();
        int succesCount = 0;
        int errorCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // Ignorer la ligne d'en-tête (nom des colonnes)
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Séparer la ligne par des points-virgules (format Excel français)
                String[] data = line.split(";");

                if (data.length >= 5) {
                    String matricule = data[0].trim();
                    String nom = data[1].trim();
                    String prenom = data[2].trim();
                    String email = data[3].trim();
                    String role = data[4].trim().toUpperCase();

                    String ufr = data.length > 5 ? data[5].trim() : "";
                    String departement = data.length > 6 ? data[6].trim() : "";
                    String classe = data.length > 7 ? data[7].trim() : "";

                    boolean success = userDAO.saveFromCSV(matricule, nom, prenom, email, role, ufr, departement, classe);
                    if (success) succesCount++;
                    else errorCount++;
                }
            }

            chargerUtilisateurs(); // Rafraîchir le tableau
            showAlert("Importation terminée", "Ajoutés : " + succesCount + "\nIgnorés (doublons) : " + errorCount);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la lecture du fichier CSV.");
        }
    }
    @FXML
    private void handleGenerateCard() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur dans le tableau.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        fileChooser.setInitialFileName("Carte_Electeur_" + selectedUser.getNom() + "_" + selectedUser.getPrenom() + ".pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // En-tête de la carte
                document.add(new Paragraph("UAM e-Vote"));
                document.add(new Paragraph("CARTE D'ÉLECTEUR"));
                document.add(new Paragraph("--------------------------------------------------------"));

                // Informations de l'électeur
                document.add(new Paragraph("Nom : " + selectedUser.getNom()));
                document.add(new Paragraph("Prénom : " + selectedUser.getPrenom()));
                document.add(new Paragraph("Matricule : " + selectedUser.getMatricule()));
                document.add(new Paragraph("Email : " + selectedUser.getEmail()));
                document.add(new Paragraph("Rôle : " + selectedUser.getRole()));
                document.add(new Paragraph("UFR : " + (selectedUser.getUfr() != null ? selectedUser.getUfr() : "N/A")));
                document.add(new Paragraph("Département : " + (selectedUser.getDepartement() != null ? selectedUser.getDepartement() : "N/A")));
                document.add(new Paragraph("Classe : " + (selectedUser.getClasse() != null ? selectedUser.getClasse() : "N/A")));

                document.add(new Paragraph("--------------------------------------------------------"));
                document.add(new Paragraph("Ce document atteste que le titulaire est inscrit sur la liste électorale de l'UAM et est habilité à participer aux scrutins correspondant à son profil."));

                document.close();

                showAlert("Succès", "La carte d'électeur a été générée avec succès en PDF !");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la création de la carte.");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}