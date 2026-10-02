package org.example.controller;

import org.example.dao.UserDAO;
import org.example.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import org.example.utils.PasswordUtil;

public class AddUserController {

    @FXML private TextField matriculeField;
    @FXML private TextField ufrField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField classeField;
    @FXML private TextField departementField;
    @FXML private CheckBox habiliteCheck;
    @FXML private Label errorLabel;
    @FXML private Text titleLabel; // On va l'ajouter dans le FXML

    private User userToEdit = null; // null si ajout, rempli si modification

    @FXML
    public void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList("ADMIN", "ETUDIANT", "ENSEIGNANT"));
        roleCombo.setValue("ETUDIANT");
    }

    // Méthode appelée si on veut modifier
    public void setUserForEdit(User user) {
        this.userToEdit = user;
        titleLabel.setText("Modifier Utilisateur");

        matriculeField.setText(user.getMatricule());
        ufrField.setText(user.getUfr());
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        passwordField.setText(user.getPassword());
        roleCombo.setValue(user.getRole());
        classeField.setText(user.getClasse());
        departementField.setText(user.getDepartement());
        habiliteCheck.setSelected(user.isEstHabilite());
    }

    @FXML
    private void handleSave() {
        if (matriculeField.getText().isEmpty() || nomField.getText().isEmpty() ||
                prenomField.getText().isEmpty() || emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs obligatoires.");
            errorLabel.setVisible(true);
            return;
        }

        User user = new User();
        user.setMatricule(matriculeField.getText());
        user.setUfr(ufrField.getText());
        user.setNom(nomField.getText());
        user.setPrenom(prenomField.getText());
        user.setEmail(emailField.getText());
        // Si c'est une modification et qu'on n'a pas touché au mot de passe, on garde l'ancien
        if (userToEdit != null && passwordField.getText().isEmpty()) {
            user.setPassword(userToEdit.getPassword());
        } else {
            // Sinon, on crypte le nouveau mot de passe
            user.setPassword(PasswordUtil.hashPassword(passwordField.getText()));
        }
        user.setRole(roleCombo.getValue());
        user.setClasse(classeField.getText());
        user.setDepartement(departementField.getText());
        user.setEstHabilite(habiliteCheck.isSelected());

        UserDAO userDAO = new UserDAO();
        boolean succes;

        if (userToEdit == null) {
            succes = userDAO.save(user); // Ajout
        } else {
            user.setId(userToEdit.getId());
            succes = userDAO.update(user); // Modification
        }

        if (succes) {
            Stage stage = (Stage) matriculeField.getScene().getWindow();
            stage.close();
        } else {
            errorLabel.setText("Erreur : Cet email ou matricule existe déjà.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) matriculeField.getScene().getWindow();
        stage.close();
    }
}