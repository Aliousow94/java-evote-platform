package org.example.dao;

import org.example.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.example.model.Election;
import java.sql.PreparedStatement;

public class StatsDAO {

    // Compter le nombre total d'utilisateurs (électeurs potentiels)
    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Compter le nombre d'élections ouvertes
    public int countActiveElections() {
        String sql = "SELECT COUNT(*) FROM elections WHERE statut = 'Ouverte'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Compter le nombre total de votes enregistrés
    public int countTotalVotes() {
        String sql = "SELECT COUNT(*) FROM votes";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    // Compter les électeurs potentiels pour une élection spécifique
    public int countEligibleVoters(Election election) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE role = ?");

        // Si l'élection cible les enseignants habilités
        if ("HABILITES".equals(election.getCibleDepartement())) {
            sql.append(" AND est_habilite = 1");
        } else {
            // Sinon, on filtre selon l'UFR, Département, Classe (s'ils ne sont pas NULL)
            if (election.getCibleUfr() != null) {
                sql.append(" AND ufr = '").append(election.getCibleUfr()).append("'");
            }
            if (election.getCibleDepartement() != null) {
                sql.append(" AND departement = '").append(election.getCibleDepartement()).append("'");
            }
            if (election.getCibleClasse() != null) {
                sql.append(" AND classe = '").append(election.getCibleClasse()).append("'");
            }
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            pstmt.setString(1, election.getElecteurRole());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}