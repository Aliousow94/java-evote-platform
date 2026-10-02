package org.example.controller;

import org.example.dao.CandidatDAO;
import org.example.dao.VoteDAO;
import org.example.model.Candidat;
import org.example.model.Election;
import org.example.model.User;
import org.example.model.Vote;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VoteController implements Initializable {

    @FXML private Text electionTitle;
    @FXML private TableView<Candidat> candidatsTable;
    @FXML private TableColumn<Candidat, String> nomColumn;
    @FXML private TableColumn<Candidat, String> prenomColumn;
    @FXML private TableColumn<Candidat, String> professionColumn;
    @FXML private TableColumn<Candidat, String> programmeColumn;
    @FXML private Label errorLabel;
    @FXML private TableColumn<Candidat, String> photoColumn;

    private User currentUser;
    private Election currentElection;

    public void initData(Election election, User user) {
        this.currentElection = election;
        this.currentUser = user;

        electionTitle.setText("Vote : " + election.getTitre());
        chargerCandidats();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Agrandir la hauteur des lignes du tableau pour les photos
        candidatsTable.setFixedCellSize(80);

        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        professionColumn.setCellValueFactory(new PropertyValueFactory<>("profession"));
        programmeColumn.setCellValueFactory(new PropertyValueFactory<>("programme"));

        // --- MAGIE POUR L'IMAGE ---
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        photoColumn.setCellFactory(col -> {
            final ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true); // Garde les proportions

            // L'image s'adaptera à la largeur de la colonne (moins 10px de marge)
            imageView.fitWidthProperty().bind(photoColumn.widthProperty().subtract(10));
            // L'image s'adaptera à la hauteur de la ligne (fixée à 80px)
            imageView.setFitHeight(75);

            TableCell<Candidat, String> cell = new TableCell<Candidat, String>() {
                @Override
                public void updateItem(String path, boolean empty) {
                    super.updateItem(path, empty);
                    if (empty || path == null || path.isEmpty()) {
                        setGraphic(null);
                    } else {
                        File file = new File(path);
                        if (file.exists()) {
                            imageView.setImage(new Image(file.toURI().toString()));
                            setGraphic(imageView);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        // --- MAGIE POUR LE TEXTE (RETOUR À LA LIGNE) ---
        programmeColumn.setCellFactory(tc -> {
            TableCell<Candidat, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);

            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(programmeColumn.widthProperty().subtract(10));
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        // Permet aux colonnes de bien remplir l'espace
        candidatsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void chargerCandidats() {
        CandidatDAO candidatDAO = new CandidatDAO();
        List<Candidat> candidats = candidatDAO.findByElectionId(currentElection.getId());
        ObservableList<Candidat> observableList = FXCollections.observableArrayList(candidats);
        candidatsTable.setItems(observableList);
    }

    @FXML
    private void handleConfirmerVote() {
        Candidat selected = candidatsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            errorLabel.setText("Veuillez sélectionner un candidat dans le tableau.");
            errorLabel.setVisible(true);
            return;
        }

        // Vérifier s'il a déjà voté
        VoteDAO voteDAO = new VoteDAO();
        if (voteDAO.aDejaVote(currentElection.getId(), currentUser.getId())) {
            errorLabel.setText("Vous avez déjà voté pour cette élection !");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
            return;
        }

        // Enregistrer le vote
        Vote vote = new Vote();
        vote.setElectionId(currentElection.getId());
        vote.setCandidatId(selected.getId());
        vote.setUtilisateurId(currentUser.getId());

        boolean succes = voteDAO.saveVote(vote);

        if (succes) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Vote enregistré");
            alert.setHeaderText(null);
            alert.setContentText("Votre vote anonyme a bien été enregistré. Merci !");
            alert.showAndWait();

            Stage stage = (Stage) candidatsTable.getScene().getWindow();
            stage.close();
        } else {
            errorLabel.setText("Erreur lors de l'enregistrement du vote.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) candidatsTable.getScene().getWindow();
        stage.close();
    }
}