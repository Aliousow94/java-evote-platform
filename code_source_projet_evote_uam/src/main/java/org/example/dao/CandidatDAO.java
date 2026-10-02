package org.example.dao;

import org.example.model.Candidat;
import org.example.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatDAO {

    // Récupérer tous les candidats
    public List<Candidat> findAll() {
        List<Candidat> candidats = new ArrayList<>();
        String sql = "SELECT * FROM candidats";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Candidat candidat = new Candidat();
                candidat.setId(rs.getInt("id"));
                candidat.setElectionId(rs.getInt("election_id"));
                candidat.setNom(rs.getString("nom"));
                candidat.setPrenom(rs.getString("prenom"));
                candidat.setProfession(rs.getString("profession"));
                candidat.setProgramme(rs.getString("programme"));
                candidat.setPhoto(rs.getString("photo"));

                candidats.add(candidat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidats;
    }

    // Ajouter un candidat
    public boolean save(Candidat candidat) {
        String sql = "INSERT INTO candidats (election_id, nom, prenom, profession, programme, photo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, candidat.getElectionId());
            pstmt.setString(2, candidat.getNom());
            pstmt.setString(3, candidat.getPrenom());
            pstmt.setString(4, candidat.getProfession());
            pstmt.setString(5, candidat.getProgramme());
            pstmt.setString(6, candidat.getPhoto());

            int lignes = pstmt.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Mettre à jour un candidat
    public boolean update(Candidat candidat) {
        String sql = "UPDATE candidats SET election_id=?, nom=?, prenom=?, profession=?, programme=?, photo=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, candidat.getElectionId());
            pstmt.setString(2, candidat.getNom());
            pstmt.setString(3, candidat.getPrenom());
            pstmt.setString(4, candidat.getProfession());
            pstmt.setString(5, candidat.getProgramme());
            pstmt.setString(6, candidat.getPhoto());
            pstmt.setInt(7, candidat.getId());

            int lignes = pstmt.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un candidat
    public boolean delete(int id) {
        String sql = "DELETE FROM candidats WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int lignes = pstmt.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Récupérer les candidats d'une élection spécifique
    public List<Candidat> findByElectionId(int electionId) {
        List<Candidat> candidats = new ArrayList<>();
        String sql = "SELECT * FROM candidats WHERE election_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, electionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Candidat candidat = new Candidat();
                candidat.setId(rs.getInt("id"));
                candidat.setElectionId(rs.getInt("election_id"));
                candidat.setNom(rs.getString("nom"));
                candidat.setPrenom(rs.getString("prenom"));
                candidat.setProfession(rs.getString("profession"));
                candidat.setProgramme(rs.getString("programme"));
                candidat.setPhoto(rs.getString("photo"));
                candidats.add(candidat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidats;
    }
}