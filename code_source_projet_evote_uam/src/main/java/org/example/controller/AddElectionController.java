package org.example.controller;

import org.example.dao.ElectionDAO;
import org.example.dao.UserDAO;
import org.example.model.Election;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class AddElectionController {

    @FXML private TextField titreField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ComboBox<String> statutCombo;

    @FXML private ComboBox<String> cibleCombo; // <-- REMPLACE roleCombo

    @FXML private ComboBox<String> ufrCombo;
    @FXML private ComboBox<String> departementCombo;
    @FXML private ComboBox<String> classeCombo;

    @FXML private Label errorLabel;
    @FXML private Text titleLabel;

    private Election electionToEdit = null;

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList("Délégués étudiants", "Chef de département", "Directeur UFR", "Directeur adjoint UFR"));
        typeCombo.setValue("Délégués étudiants");

        statutCombo.setItems(FXCollections.observableArrayList("En préparation", "Ouverte", "Fermée"));
        statutCombo.setValue("En préparation");

        // NOUVELLE LISTE CIBLE
        cibleCombo.setItems(FXCollections.observableArrayList("Étudiants", "Enseignants", "Enseignants Habilités"));
        cibleCombo.setValue("Étudiants");

        UserDAO userDAO = new UserDAO();

        ufrCombo.getItems().add("Toutes");
        ufrCombo.getItems().addAll(userDAO.getDistinctValues("ufr"));
        ufrCombo.setValue("Toutes");

        ufrCombo.valueProperty().addListener((obs, oldVal, newVal) -> majDepartements());
        departementCombo.valueProperty().addListener((obs, oldVal, newVal) -> majClasses());

        majDepartements();
        majClasses();

        // MAGIE : Si on choisit "Enseignants Habilités", on désactive les autres listes
        cibleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isHabilite = "Enseignants Habilités".equals(newVal);
            ufrCombo.setDisable(isHabilite);
            departementCombo.setDisable(isHabilite);
            classeCombo.setDisable(isHabilite);
        });
    }

    private void majDepartements() {
        String ufrChoisie = ufrCombo.getValue();
        departementCombo.getItems().clear();
        departementCombo.getItems().add("Toutes");
        List<String> depts = new UserDAO().getFilteredValues("departement", "ufr", ufrChoisie);
        departementCombo.getItems().addAll(depts);
        departementCombo.setValue("Toutes");
    }

    private void majClasses() {
        String deptChoisi = departementCombo.getValue();
        classeCombo.getItems().clear();
        classeCombo.getItems().add("Toutes");
        List<String> classes = new UserDAO().getFilteredValues("classe", "departement", deptChoisi);
        classeCombo.getItems().addAll(classes);
        classeCombo.setValue("Toutes");
    }

    public void setElectionForEdit(Election election) {
        this.electionToEdit = election;
        titleLabel.setText("Modifier Élection");

        titreField.setText(election.getTitre());
        typeCombo.setValue(election.getTypeElection());
        statutCombo.setValue(election.getStatut());

        // Retrouver la bonne valeur de Cible à partir des données de la BDD
        if ("HABILITES".equals(election.getCibleDepartement())) {
            cibleCombo.setValue("Enseignants Habilités");
        } else if ("ENSEIGNANT".equals(election.getElecteurRole())) {
            cibleCombo.setValue("Enseignants");
        } else {
            cibleCombo.setValue("Étudiants");
        }

        ufrCombo.setValue(election.getCibleUfr() == null ? "Toutes" : election.getCibleUfr());
        departementCombo.setValue(election.getCibleDepartement() == null ? "Toutes" : election.getCibleDepartement());
        classeCombo.setValue(election.getCibleClasse() == null ? "Toutes" : election.getCibleClasse());
    }

    @FXML
    private void handleSave() {
        if (titreField.getText().isEmpty() ||
                (electionToEdit == null && (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null))) {
            errorLabel.setText("Veuillez remplir le titre et les dates.");
            errorLabel.setVisible(true);
            return;
        }

        Election election = new Election();
        election.setTitre(titreField.getText());
        election.setTypeElection(typeCombo.getValue());

        if (dateDebutPicker.getValue() != null) election.setDateDebut(dateDebutPicker.getValue().toString() + " 00:00:00");
        if (dateFinPicker.getValue() != null) election.setDateFin(dateFinPicker.getValue().toString() + " 23:59:59");

        election.setStatut(statutCombo.getValue());

        // TRADUCTION DE LA CIBLE VERS LA BDD
        String cibleChoisie = cibleCombo.getValue();
        if ("Enseignants Habilités".equals(cibleChoisie)) {
            election.setElecteurRole("ENSEIGNANT");
            election.setCibleUfr("Toutes");
            election.setCibleDepartement("HABILITES"); // Mot-clé pour la BDD
            election.setCibleClasse("Toutes");
        } else if ("Enseignants".equals(cibleChoisie)) {
            election.setElecteurRole("ENSEIGNANT");
            election.setCibleUfr(ufrCombo.getValue());
            election.setCibleDepartement(departementCombo.getValue());
            election.setCibleClasse(classeCombo.getValue());
        } else { // Étudiants
            election.setElecteurRole("ETUDIANT");
            election.setCibleUfr(ufrCombo.getValue());
            election.setCibleDepartement(departementCombo.getValue());
            election.setCibleClasse(classeCombo.getValue());
        }

        ElectionDAO electionDAO = new ElectionDAO();
        boolean succes;

        if (electionToEdit == null) {
            succes = electionDAO.save(election);
        } else {
            election.setId(electionToEdit.getId());
            if (election.getDateDebut() == null) election.setDateDebut(electionToEdit.getDateDebut());
            if (election.getDateFin() == null) election.setDateFin(electionToEdit.getDateFin());
            succes = electionDAO.update(election);
        }

        if (succes) {
            Stage stage = (Stage) titreField.getScene().getWindow();
            stage.close();
        } else {
            errorLabel.setText("Erreur lors de l'enregistrement.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }
}