package org.example.controller;

import org.example.dao.ElectionDAO;
import org.example.model.Election;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ElectionController implements Initializable {

    // Remplace l'ancien cibleColumn par ceux-ci :
    @FXML private TableView<Election> electionsTable;
    @FXML private TableColumn<Election, Integer> idColumn;
    @FXML private TableColumn<Election, String> titreColumn;
    @FXML private TableColumn<Election, String> typeColumn;
    @FXML private TableColumn<Election, String> dateDebutColumn;
    @FXML private TableColumn<Election, String> statutColumn;
    @FXML private TableColumn<Election, String> roleColumn;
    @FXML private TableColumn<Election, String> ufrColumn;
    @FXML private TableColumn<Election, String> departementColumn;
    @FXML private TableColumn<Election, String> classeColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeElection"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("electeurRole"));
        ufrColumn.setCellValueFactory(new PropertyValueFactory<>("cibleUfr"));
        departementColumn.setCellValueFactory(new PropertyValueFactory<>("cibleDepartement"));
        classeColumn.setCellValueFactory(new PropertyValueFactory<>("cibleClasse"));

        chargerElections();
    }

    public void chargerElections() {
        ElectionDAO electionDAO = new ElectionDAO();
        List<Election> electionsList = electionDAO.findAll();
        ObservableList<Election> observableList = FXCollections.observableArrayList(electionsList);
        electionsTable.setItems(observableList);
    }


    @FXML
    private void handleAddElection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addElection.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Créer une élection");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Rafraîchir le tableau après fermeture de la fenêtre
            chargerElections();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleOuvrirElection() {
        Election selected = electionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une élection dans le tableau.");
            return;
        }
        new ElectionDAO().updateStatut(selected.getId(), "Ouverte");
        chargerElections(); // Rafraîchir
    }

    @FXML
    private void handleFermerElection() {
        Election selected = electionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une élection dans le tableau.");
            return;
        }
        new ElectionDAO().updateStatut(selected.getId(), "Fermée");
        chargerElections(); // Rafraîchir
    }
    @FXML
    private void handleEditElection() {
        Election selected = electionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une élection dans le tableau.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addElection.fxml"));
            Parent root = loader.load();

            AddElectionController controller = loader.getController();
            controller.setElectionForEdit(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier une élection");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            chargerElections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteElection() {
        Election selected = electionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une élection à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer l'élection : " + selected.getTitre() + " ?");
        confirm.setContentText("Attention : Tous les candidats et votes liés à cette élection seront aussi supprimés.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            ElectionDAO electionDAO = new ElectionDAO();
            electionDAO.delete(selected.getId());
            chargerElections();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleShowResults() {
        Election selected = electionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une élection dans le tableau.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/resultsView.fxml"));
            Parent root = loader.load();

            ResultsController controller = loader.getController();
            controller.initData(selected);

            Stage stage = new Stage();
            stage.setTitle("Résultats de l'élection");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
