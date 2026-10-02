package org.example.controller;

import org.example.dao.UserDAO;
import org.example.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.utils.PasswordUtil;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String loginSaisi = loginField.getText();
        String passwordSaisi = passwordField.getText();

        if (loginSaisi.isEmpty() || passwordSaisi.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByEmail(loginSaisi);

        // On crypte le mot de passe saisi avant de le comparer
        String hashedPasswordSaisi = PasswordUtil.hashPassword(passwordSaisi);

        if (user != null && user.getPassword().equals(hashedPasswordSaisi))  {

            try {
                // 1. Fermer la fenêtre de connexion
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();

                Parent root;
                Stage dashboardStage = new Stage();

                // 2. Rediriger selon le rôle
                if (user.getRole().equals("ADMIN")) {
                    // Si c'est un admin, on ouvre le tableau de bord Admin
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/adminDashboard.fxml"));
                    root = loader.load();
                    dashboardStage.setTitle("UAM e-Vote - Tableau de bord Admin");
                } else {
                    // Sinon, c'est un électeur, on ouvre le tableau de bord Électeur
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/voterDashboard.fxml"));
                    root = loader.load();

                    // On transmet l'utilisateur connecté au contrôleur de l'électeur
                    VoterDashboardController controller = loader.getController();
                    controller.initData(user);

                    dashboardStage.setTitle("UAM e-Vote - Espace Électeur");
                }

                dashboardStage.setScene(new Scene(root, 900, 600));
                dashboardStage.show();

            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Erreur de chargement de l'interface.");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setVisible(true);
            }

        } else {
            errorLabel.setText("Email ou mot de passe incorrect.");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setVisible(true);
        }
    }
}