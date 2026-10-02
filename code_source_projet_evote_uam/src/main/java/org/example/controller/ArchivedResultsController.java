package org.example.controller;

import org.example.dao.ElectionDAO;
import org.example.model.Election;
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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ArchivedResultsController implements Initializable {

    @FXML private TableView<Election> archivedTable;
    @FXML private TableColumn<Election, String> titreColumn;
    @FXML private TableColumn<Election, String> typeColumn;
    @FXML private TableColumn<Election, String> dateFinColumn;

    private User currentUser;

    public void initData(User user) {
        this.currentUser = user;
        chargerElectionsFermées();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeElection"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        archivedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void chargerElectionsFermées() {
        if (currentUser == null) return;
        ElectionDAO electionDAO = new ElectionDAO();
        List<Election> elections = electionDAO.findClosedElectionsForUser(currentUser);
        ObservableList<Election> observableList = FXCollections.observableArrayList(elections);
        archivedTable.setItems(observableList);
    }

    @FXML
    private void handleShowResults() {
        Election selected = archivedTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setContentText("Veuillez sélectionner une élection dans le tableau.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/resultsView.fxml"));
            Parent root = loader.load();

            ResultsController controller = loader.getController();
            controller.initData(selected);

            Stage stage = new Stage();
            stage.setTitle("Résultats : " + selected.getTitre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}