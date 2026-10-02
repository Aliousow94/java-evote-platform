package org.example.controller;

import org.example.dao.CandidatDAO;
import org.example.dao.ElectionDAO;
import org.example.model.Candidat;
import org.example.model.Election;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.io.File;
import java.util.List;

public class AddCandidatController {

    @FXML private ComboBox<Election> electionCombo;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField professionField;
    @FXML private TextArea programmeArea;
    @FXML private Label photoLabel;
    @FXML private Label errorLabel;
    @FXML private Text titleLabel;

    private String cheminPhoto;
    private Candidat candidatToEdit = null; // null si ajout, rempli si modification

    @FXML
    public void initialize() {
        ElectionDAO electionDAO = new ElectionDAO();
        List<Election> elections = electionDAO.findAll();
        ObservableList<Election> observableList = FXCollections.observableArrayList(elections);
        electionCombo.setItems(observableList);

        if (!observableList.isEmpty()) {
            electionCombo.setValue(observableList.get(0));
        }
    }

    // Méthode appelée si on veut modifier
    public void setCandidatForEdit(Candidat candidat) {
        this.candidatToEdit = candidat;
        titleLabel.setText("Modifier Candidat");

        nomField.setText(candidat.getNom());
        prenomField.setText(candidat.getPrenom());
        professionField.setText(candidat.getProfession());
        programmeArea.setText(candidat.getProgramme());

        // Pour la photo, on garde l'ancien chemin en mémoire
        this.cheminPhoto = candidat.getPhoto();
        if (cheminPhoto != null && !cheminPhoto.isEmpty()) {
            photoLabel.setText("Photo actuelle conservée");
        }

        // Sélectionner la bonne élection dans la liste déroulante
        for (Election e : electionCombo.getItems()) {
            if (e.getId() == candidat.getElectionId()) {
                electionCombo.setValue(e);
                break;
            }
        }
    }

    @FXML
    private void handleChoisirPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une photo de profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            cheminPhoto = selectedFile.getAbsolutePath();
            photoLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleSave() {
        Election selectedElection = electionCombo.getValue();

        if (selectedElection == null || nomField.getText().isEmpty() || prenomField.getText().isEmpty()) {
            errorLabel.setText("Veuillez choisir une élection et remplir le nom/prénom.");
            errorLabel.setVisible(true);
            return;
        }

        Candidat candidat = new Candidat();
        candidat.setElectionId(selectedElection.getId());
        candidat.setNom(nomField.getText());
        candidat.setPrenom(prenomField.getText());
        candidat.setProfession(professionField.getText());
        candidat.setProgramme(programmeArea.getText());
        candidat.setPhoto(cheminPhoto);

        CandidatDAO candidatDAO = new CandidatDAO();
        boolean succes;

        if (candidatToEdit == null) {
            succes = candidatDAO.save(candidat); // Ajout
        } else {
            candidat.setId(candidatToEdit.getId());
            succes = candidatDAO.update(candidat); // Modification
        }

        if (succes) {
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        } else {
            errorLabel.setText("Erreur lors de l'enregistrement.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}