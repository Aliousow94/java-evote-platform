package org.example.controller;

import org.example.dao.ElectionDAO;
import org.example.dao.StatsDAO;
import org.example.dao.VoteDAO;
import org.example.model.Election;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminDashboardController {

    @FXML private Label contentLabel;
    @FXML private BarChart<String, Number> resultsBarChart;
    @FXML private PieChart participationPieChart;
    @FXML private ListView<String> upcomingElectionsList;
    @FXML private ListView<String> eventsList;

    @FXML private BorderPane mainBorderPane;

    @FXML private Text barChartTitle; // <-- LIGNE CORRIGÉE ET BIEN PLACÉE ICI
    @FXML private Text activeElectionsText;
    @FXML private Text votersText;
    @FXML private Text totalVotesText;

    @FXML
    public void initialize() {
        StatsDAO statsDAO = new StatsDAO();
        ElectionDAO electionDAO = new ElectionDAO();
        VoteDAO voteDAO = new VoteDAO();

        // --- 1. STATISTIQUES GLOBALES (Les cartes en haut) ---
        activeElectionsText.setText(String.valueOf(statsDAO.countActiveElections()));
        votersText.setText(String.valueOf(statsDAO.countUsers()));
        totalVotesText.setText(String.valueOf(statsDAO.countTotalVotes()));

        // --- 2. GRAPHIQUES SPÉCIFIQUES À UNE ÉLECTION ---
        resultsBarChart.getData().clear();
        List<Election> allElections = electionDAO.findAll();
        Election electionToShow = null;

        for (Election e : allElections) {
            if ("Ouverte".equals(e.getStatut()) || "Fermée".equals(e.getStatut())) {
                electionToShow = e;
                break;
            }
        }

        if (electionToShow != null) {
            barChartTitle.setText("Résultats : " + electionToShow.getTitre());

            // Récupérer les résultats de cette élection
            Map<String, Integer> results = voteDAO.getResultsByCandidatName(electionToShow.getId());

            // Remplir le BarChart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Votes");
            int votesCandidats = 0;
            for (Map.Entry<String, Integer> entry : results.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                votesCandidats += entry.getValue(); // On compte le total des votes pour cette élection
            }
            resultsBarChart.getData().add(series);

            // Calculer le VRAI nombre d'électeurs autorisés pour cette élection
            int eligibleVoters = statsDAO.countEligibleVoters(electionToShow);
            int notVoted = Math.max(0, eligibleVoters - votesCandidats);

            // Remplir le PieChart avec les vraies données de participation
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("A voté (" + votesCandidats + ")", votesCandidats),
                    new PieChart.Data("N'a pas voté (" + notVoted + ")", notVoted)
            );
            participationPieChart.setData(pieData);
            participationPieChart.setLabelsVisible(true);
            participationPieChart.setTitle("Participation : " + votesCandidats + "/" + eligibleVoters);

        } else {
            barChartTitle.setText("Aucune élection à afficher");
            participationPieChart.setTitle("Aucune élection à afficher");
        }
        resultsBarChart.setLegendVisible(false);

        // --- 3. LISTES RÉELLES ---
        List<Election> upcoming = electionDAO.findByStatut("En préparation");
        ObservableList<String> upcomingNames = FXCollections.observableArrayList();
        for (Election e : upcoming) {
            upcomingNames.add(e.getTitre() + " (Début: " + e.getDateDebut() + ")");
        }
        upcomingElectionsList.setItems(upcomingNames);

        ObservableList<String> events = FXCollections.observableArrayList(
                "Total des votes (toutes élections) : " + statsDAO.countTotalVotes(),
                "Élections actives : " + statsDAO.countActiveElections(),
                "Utilisateurs inscrits : " + statsDAO.countUsers()
        );
        eventsList.setItems(events);
    }

    @FXML
    private void showStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/adminDashboard.fxml"));
            Parent root = loader.load();
            mainBorderPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showElections() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/electionsView.fxml"));
            Parent electionsView = loader.load();
            mainBorderPane.setCenter(electionsView);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void showCandidats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/candidatsView.fxml"));
            Parent candidatsView = loader.load();
            mainBorderPane.setCenter(candidatsView);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void showUtilisateurs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/usersView.fxml"));
            Parent usersView = loader.load();
            mainBorderPane.setCenter(usersView);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) mainBorderPane.getScene().getWindow();
            stage.close();

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
}