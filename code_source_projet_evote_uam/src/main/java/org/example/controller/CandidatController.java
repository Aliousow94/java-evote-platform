package org.example.controller;

import org.example.dao.CandidatDAO;
import org.example.model.Candidat;
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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CandidatController implements Initializable {

    @FXML private TableView<Candidat> candidatsTable;
    @FXML private TableColumn<Candidat, Integer> electionIdColumn;
    @FXML private TableColumn<Candidat, String> nomColumn;
    @FXML private TableColumn<Candidat, String> prenomColumn;
    @FXML private TableColumn<Candidat, String> professionColumn;
    @FXML private TableColumn<Candidat, String> programmeColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        electionIdColumn.setCellValueFactory(new PropertyValueFactory<>("electionId"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        professionColumn.setCellValueFactory(new PropertyValueFactory<>("profession"));
        programmeColumn.setCellValueFactory(new PropertyValueFactory<>("programme"));

        chargerCandidats();
    }

    public void chargerCandidats() {
        CandidatDAO candidatDAO = new CandidatDAO();
        List<Candidat> candidatsList = candidatDAO.findAll();
        ObservableList<Candidat> observableList = FXCollections.observableArrayList(candidatsList);
        candidatsTable.setItems(observableList);
    }

    @FXML
    private void handleAddCandidat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addCandidat.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un candidat");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            chargerCandidats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditCandidat() {
        Candidat selected = candidatsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un candidat dans le tableau.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addCandidat.fxml"));
            Parent root = loader.load();

            AddCandidatController controller = loader.getController();
            controller.setCandidatForEdit(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier un candidat");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            chargerCandidats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCandidat() {
        Candidat selected = candidatsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un candidat à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer " + selected.getNom() + " " + selected.getPrenom() + " ?");
        confirm.setContentText("Cette action est irréversible.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            CandidatDAO candidatDAO = new CandidatDAO();
            candidatDAO.delete(selected.getId());
            chargerCandidats();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}