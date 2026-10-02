package org.example.controller;

import org.example.dao.ElectionDAO;
import org.example.model.Election;
import org.example.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VoterDashboardController implements Initializable {

    @FXML private Text userInfoLabel;
    @FXML private VBox contentArea;
    @FXML private TableView<Election> electionsTable;
    @FXML private TableColumn<Election, String> titreColumn;
    @FXML private TableColumn<Election, String> typeColumn;
    @FXML private TableColumn<Election, String> dateFinColumn;


    private User currentUser;

    // Méthode pour recevoir l'utilisateur connecté depuis la page de Login
    public void initData(User user) {
        this.currentUser = user;
        userInfoLabel.setText("Bonjour " + user.getPrenom() + " " + user.getNom());
        chargerElectionsOuvertes();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeElection"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        // Permet aux colonnes de s'étirer pour prendre toute la largeur (évite la colonne vide)
        electionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void chargerElectionsOuvertes() {
        if (currentUser == null) return;

        ElectionDAO electionDAO = new ElectionDAO();
        List<Election> elections = electionDAO.findOpenElectionsForUser(currentUser);
        ObservableList<Election> observableList = FXCollections.observableArrayList(elections);
        electionsTable.setItems(observableList);
    }

    @FXML
    private void showOpenElections() {
        // Déjà affiché
    }

    @FXML
    private void showArchivedResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/archivedResults.fxml"));
            Parent root = loader.load();

            ArchivedResultsController controller = loader.getController();
            controller.initData(currentUser);

            // On remplace le contenu du centre par cette vue
            BorderPane parent = (BorderPane) contentArea.getParent();
            parent.setCenter(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Fermer l'espace électeur
            Stage stage = (Stage) userInfoLabel.getScene().getWindow();
            stage.close();

            // Rouvrir la fenêtre de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("UAM e-Vote - Connexion");
            loginStage.setScene(new Scene(root, 600, 400));
            loginStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleVoter() {
        Election selected = electionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setContentText("Veuillez sélectionner une élection dans le tableau.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/voteView.fxml"));
            Parent root = loader.load();

            VoteController controller = loader.getController();
            controller.initData(selected, currentUser);

            Stage stage = new Stage();
            stage.setTitle("Isoloir - UAM e-Vote");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}