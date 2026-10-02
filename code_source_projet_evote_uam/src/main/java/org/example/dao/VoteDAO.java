package org.example.dao;

import org.example.model.Vote;
import org.example.utils.DatabaseConnection;
import java.util.HashMap;
import java.util.Map;

import java.sql.*;

public class VoteDAO {

    // Enregistrer un vote
    public boolean saveVote(Vote vote) {
        String sql = "INSERT INTO votes (election_id, candidat_id, utilisateur_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, vote.getElectionId());
            pstmt.setInt(2, vote.getCandidatId());
            pstmt.setInt(3, vote.getUtilisateurId());

            int lignes = pstmt.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            // Si erreur, c'est sûrement que l'utilisateur a déjà voté (contrainte UNIQUE)
            System.err.println("Erreur lors du vote (déjà voté ?) : " + e.getMessage());
            return false;
        }
    }

    // Vérifier si l'utilisateur a déjà voté pour cette élection
    public boolean aDejaVote(int electionId, int utilisateurId) {
        String sql = "SELECT id FROM votes WHERE election_id = ? AND utilisateur_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            pstmt.setInt(2, utilisateurId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // S'il y a un résultat, il a déjà voté

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Compter les votes pour chaque candidat d'une élection
    public Map<String, Integer> getResultsByCandidatName(int electionId) {
        Map<String, Integer> results = new HashMap<>();
        // On joint la table candidats et votes pour compter
        String sql = "SELECT c.nom, c.prenom, COUNT(v.id) as nb_votes " +
                "FROM candidats c LEFT JOIN votes v ON c.id = v.candidat_id " +
                "WHERE c.election_id = ? " +
                "GROUP BY c.id, c.nom, c.prenom";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nomComplet = rs.getString("nom") + " " + rs.getString("prenom");
                int nbVotes = rs.getInt("nb_votes");
                results.put(nomComplet, nbVotes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}