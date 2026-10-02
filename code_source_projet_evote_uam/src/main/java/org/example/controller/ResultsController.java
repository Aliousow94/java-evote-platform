package org.example.controller;

import org.example.dao.VoteDAO;
import org.example.model.Election;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;
import org.example.dao.StatsDAO;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class ResultsController {

    @FXML private Text electionTitle;
    @FXML private PieChart resultsPieChart;
    @FXML private Label winnerLabel;
    @FXML private BarChart<String, Number> participationBarChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private Election currentElection;
    private Map<String, Integer> currentResults;
    private int totalVotes;

    public void initData(Election election) {
        this.currentElection = election;
        electionTitle.setText("Résultats : " + election.getTitre());

        VoteDAO voteDAO = new VoteDAO();
        StatsDAO statsDAO = new StatsDAO();
        currentResults = voteDAO.getResultsByCandidatName(election.getId());

        if (currentResults.isEmpty()) {
            winnerLabel.setText("Aucun vote n'a été enregistré pour cette élection.");
            return;
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        totalVotes = 0;
        String winnerName = "";
        int maxVotes = -1;
        int countMaxVotes = 0;

        for (Map.Entry<String, Integer> entry : currentResults.entrySet()) {
            totalVotes += entry.getValue();
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winnerName = entry.getKey();
                countMaxVotes = 1;
            } else if (entry.getValue() == maxVotes) {
                countMaxVotes++;
            }
        }

        for (Map.Entry<String, Integer> entry : currentResults.entrySet()) {
            String candidat = entry.getKey();
            double percentage = (double) entry.getValue() / totalVotes * 100;
            pieData.add(new PieChart.Data(candidat + " (" + String.format("%.1f", percentage) + "%)", entry.getValue()));
        }

        resultsPieChart.setData(pieData);
        resultsPieChart.setLabelsVisible(true);
        resultsPieChart.setLegendVisible(true);

        // --- NOUVEAU : REMPLIR LE BARCHART DE PARTICIPATION ---
        int eligibleVoters = statsDAO.countEligibleVoters(election);
        int notVoted = Math.max(0, eligibleVoters - totalVotes);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Électeurs");
        series.getData().add(new XYChart.Data<>("A voté", totalVotes));
        series.getData().add(new XYChart.Data<>("N'a pas voté", notVoted));

        participationBarChart.getData().add(series);
        participationBarChart.setLegendVisible(false);

        // Afficher le vainqueur OU l'égalité
        if (countMaxVotes > 1) {
            winnerLabel.setText("⚠️ Égalité des voix à " + maxVotes + " ! Pas de vainqueur pour l'instant.");
            winnerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;");
        } else {
            winnerLabel.setText("🏆 Vainqueur : " + winnerName + " avec " + maxVotes + " voix !");
            winnerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #E67E22;");
        }
    }
    @FXML
    private void handleExportPDF() {
        if (currentResults == null || currentResults.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        fileChooser.setInitialFileName(currentElection.getTitre().replace(" ", "_") + "_resultats.pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                document.add(new Paragraph("Résultats de l'élection : " + currentElection.getTitre()));
                document.add(new Paragraph("Date du rapport : " + java.time.LocalDate.now()));
                document.add(new Paragraph("Total des votes : " + totalVotes));
                document.add(new Paragraph("--------------------------------------------------------"));

                for (Map.Entry<String, Integer> entry : currentResults.entrySet()) {
                    double percentage = (double) entry.getValue() / totalVotes * 100;
                    document.add(new Paragraph(entry.getKey() + " : " + entry.getValue() + " voix (" + String.format("%.1f", percentage) + "%)"));
                }

                document.add(new Paragraph("--------------------------------------------------------"));
                document.add(new Paragraph(winnerLabel.getText()));

                document.close();

                showAlert("Succès", "Le PDF a été exporté avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la création du PDF.");
            }
        }
    }

    @FXML
    private void handleExportCSV() {
        if (currentResults == null || currentResults.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier CSV", "*.csv"));
        fileChooser.setInitialFileName(currentElection.getTitre().replace(" ", "_") + "_resultats.csv");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Candidat,Nombre de votes,Pourcentage");

                for (Map.Entry<String, Integer> entry : currentResults.entrySet()) {
                    double percentage = (double) entry.getValue() / totalVotes * 100;
                    writer.println(entry.getKey() + "," + entry.getValue() + "," + String.format("%.1f", percentage) + "%");
                }

                showAlert("Succès", "Le CSV a été exporté avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la création du CSV.");
            }
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) winnerLabel.getScene().getWindow();
        stage.close();
    }
}