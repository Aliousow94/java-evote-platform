package org.example.dao;

import org.example.model.Election;
import org.example.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.model.User;

public class ElectionDAO {

    // Récupérer toutes les élections
    public List<Election> findAll() {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Election election = new Election();
                election.setId(rs.getInt("id"));
                election.setTitre(rs.getString("titre"));
                election.setTypeElection(rs.getString("type_election"));

                Timestamp tsDebut = rs.getTimestamp("date_debut");
                if (tsDebut != null) election.setDateDebut(tsDebut.toString());

                Timestamp tsFin = rs.getTimestamp("date_fin");
                if (tsFin != null) election.setDateFin(tsFin.toString());

                election.setStatut(rs.getString("statut"));
                election.setElecteurRole(rs.getString("electeur_role"));

                // Les nouvelles colonnes
                election.setCibleUfr(rs.getString("cible_ufr"));
                election.setCibleDepartement(rs.getString("cible_departement"));
                election.setCibleClasse(rs.getString("cible_classe"));

                elections.add(election);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return elections;
    }

    // Ajouter une élection
    public boolean save(Election election) {
        String sql = "INSERT INTO elections (titre, type_election, date_debut, date_fin, statut, electeur_role, cible_ufr, cible_departement, cible_classe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, election.getTitre());
            pstmt.setString(2, election.getTypeElection());
            pstmt.setString(3, election.getDateDebut());
            pstmt.setString(4, election.getDateFin());
            pstmt.setString(5, election.getStatut());
            pstmt.setString(6, election.getElecteurRole());

            // Si la valeur est "Toutes", on enregistre NULL dans la base
            pstmt.setString(7, election.getCibleUfr() != null && election.getCibleUfr().equals("Toutes") ? null : election.getCibleUfr());
            pstmt.setString(8, election.getCibleDepartement() != null && election.getCibleDepartement().equals("Toutes") ? null : election.getCibleDepartement());
            pstmt.setString(9, election.getCibleClasse() != null && election.getCibleClasse().equals("Toutes") ? null : election.getCibleClasse());

            int lignes = pstmt.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Mettre à jour le statut d'une élection
    public boolean updateStatut(int id, String statut) {
        String sql = "UPDATE elections SET statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, statut);
            pstmt.setInt(2, id);

            int lignes = pstmt.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Mettre à jour une élection
    public boolean update(Election election) {
        String sql = "UPDATE elections SET titre=?, type_election=?, date_debut=?, date_fin=?, statut=?, electeur_role=?, cible_ufr=?, cible_departement=?, cible_classe=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, election.getTitre());
            pstmt.setString(2, election.getTypeElection());
            pstmt.setString(3, election.getDateDebut());
            pstmt.setString(4, election.getDateFin());
            pstmt.setString(5, election.getStatut());
            pstmt.setString(6, election.getElecteurRole());
            pstmt.setString(7, election.getCibleUfr() != null && election.getCibleUfr().equals("Toutes") ? null : election.getCibleUfr());
            pstmt.setString(8, election.getCibleDepartement() != null && election.getCibleDepartement().equals("Toutes") ? null : election.getCibleDepartement());
            pstmt.setString(9, election.getCibleClasse() != null && election.getCibleClasse().equals("Toutes") ? null : election.getCibleClasse());
            pstmt.setInt(10, election.getId());

            int lignes = pstmt.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer une élection
    public boolean delete(int id) {
        String sql = "DELETE FROM elections WHERE id=?";
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

    // Récupérer les élections ouvertes pour un utilisateur spécifique
    public List<Election> findOpenElectionsForUser(User user) {
        List<Election> elections = new ArrayList<>();

        // Requête magique mise à jour :
        // Si cible_departement = 'HABILITES', on vérifie que l'utilisateur est habilité (? = 1)
        String sql = "SELECT * FROM elections WHERE statut = 'Ouverte' AND electeur_role = ? " +
                "AND (cible_ufr = ? OR cible_ufr IS NULL OR cible_ufr = 'Toutes') " +
                "AND (cible_departement = ? OR cible_departement IS NULL OR cible_departement = 'Toutes' " +
                "     OR (cible_departement = 'HABILITES' AND ? = 1)) " + // <-- LA MAGIE EST ICI
                "AND (cible_classe = ? OR cible_classe IS NULL OR cible_classe = 'Toutes')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getRole());
            pstmt.setString(2, user.getUfr() != null ? user.getUfr() : "");
            pstmt.setString(3, user.getDepartement() != null ? user.getDepartement() : "");

            // On passe 1 si l'utilisateur est habilité, 0 sinon
            pstmt.setInt(4, user.isEstHabilite() ? 1 : 0); // <-- NOUVEAU PARAMÈTRE

            pstmt.setString(5, user.getClasse() != null ? user.getClasse() : "");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Election election = new Election();
                election.setId(rs.getInt("id"));
                election.setTitre(rs.getString("titre"));
                election.setTypeElection(rs.getString("type_election"));
                election.setStatut(rs.getString("statut"));
                election.setElecteurRole(rs.getString("electeur_role"));
                election.setCibleUfr(rs.getString("cible_ufr"));
                election.setCibleDepartement(rs.getString("cible_departement"));
                election.setCibleClasse(rs.getString("cible_classe"));

                Timestamp tsDebut = rs.getTimestamp("date_debut");
                if (tsDebut != null) election.setDateDebut(tsDebut.toString());
                Timestamp tsFin = rs.getTimestamp("date_fin");
                if (tsFin != null) election.setDateFin(tsFin.toString());

                elections.add(election);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return elections;
    }
    // Récupérer les élections fermées (archivées) pour un utilisateur
    public List<Election> findClosedElectionsForUser(User user) {
        List<Election> elections = new ArrayList<>();
        // Même logique que pour les élections ouvertes, mais statut = 'Fermée'
        String sql = "SELECT * FROM elections WHERE statut = 'Fermée' AND electeur_role = ? " +
                "AND (cible_ufr = ? OR cible_ufr IS NULL OR cible_ufr = 'Toutes') " +
                "AND (cible_departement = ? OR cible_departement IS NULL OR cible_departement = 'Toutes' " +
                "     OR (cible_departement = 'HABILITES' AND ? = 1)) " +
                "AND (cible_classe = ? OR cible_classe IS NULL OR cible_classe = 'Toutes')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getRole());
            pstmt.setString(2, user.getUfr() != null ? user.getUfr() : "");
            pstmt.setString(3, user.getDepartement() != null ? user.getDepartement() : "");
            pstmt.setInt(4, user.isEstHabilite() ? 1 : 0);
            pstmt.setString(5, user.getClasse() != null ? user.getClasse() : "");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Election election = new Election();
                election.setId(rs.getInt("id"));
                election.setTitre(rs.getString("titre"));
                election.setTypeElection(rs.getString("type_election"));
                election.setStatut(rs.getString("statut"));
                election.setElecteurRole(rs.getString("electeur_role"));
                election.setCibleUfr(rs.getString("cible_ufr"));
                election.setCibleDepartement(rs.getString("cible_departement"));
                election.setCibleClasse(rs.getString("cible_classe"));

                Timestamp tsFin = rs.getTimestamp("date_fin");
                if (tsFin != null) election.setDateFin(tsFin.toString());

                elections.add(election);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return elections;
    }
    // Récupérer les élections par statut
    public List<Election> findByStatut(String statut) {
        List<Election> elections = new ArrayList<>();
        String sql = "SELECT * FROM elections WHERE statut = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, statut);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Election election = new Election();
                election.setId(rs.getInt("id"));
                election.setTitre(rs.getString("titre"));
                election.setTypeElection(rs.getString("type_election"));
                election.setStatut(rs.getString("statut"));
                election.setElecteurRole(rs.getString("electeur_role"));
                election.setCibleUfr(rs.getString("cible_ufr"));
                election.setCibleDepartement(rs.getString("cible_departement"));
                election.setCibleClasse(rs.getString("cible_classe"));

                Timestamp tsDebut = rs.getTimestamp("date_debut");
                if (tsDebut != null) election.setDateDebut(tsDebut.toString());
                Timestamp tsFin = rs.getTimestamp("date_fin");
                if (tsFin != null) election.setDateFin(tsFin.toString());

                elections.add(election);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return elections;
    }
}