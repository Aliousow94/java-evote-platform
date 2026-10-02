package org.example.dao;

import org.example.model.User;
import org.example.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import org.example.utils.PasswordUtil;

public class UserDAO {

    // Méthode pour chercher un utilisateur par son email
    public User findByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setMatricule(rs.getString("matricule"));
                user.setUfr(rs.getString("ufr"));
                user.setNom(rs.getString("nom"));           // <-- LIGNE AJOUTÉE
                user.setPrenom(rs.getString("prenom"));     // <-- LIGNE AJOUTÉE
                user.setEmail(rs.getString("email"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setClasse(rs.getString("classe"));
                user.setDepartement(rs.getString("departement"));
                user.setEstHabilite(rs.getBoolean("est_habilite"));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur : " + e.getMessage());
        }
        return user;
    }
    // Méthode pour récupérer tous les utilisateurs

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // 1. D'abord, on crée l'objet
                User user = new User();

                // 2. Ensuite, on remplit ses informations
                user.setId(rs.getInt("id"));
                user.setMatricule(rs.getString("matricule"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setClasse(rs.getString("classe"));
                user.setDepartement(rs.getString("departement"));
                user.setUfr(rs.getString("ufr"));

                // 3. Enfin, on l'ajoute à la liste
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return users;
    }
    // Méthode pour ajouter un utilisateur dans la base de données
    public boolean save(User user) {
        String sql = "INSERT INTO users (matricule, ufr, nom, prenom, email, login, password, role, classe, departement, est_habilite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getMatricule());
            pstmt.setString(2, user.getUfr()); // <-- NOUVEAU
            pstmt.setString(3, user.getNom());
            pstmt.setString(4, user.getPrenom());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getPassword());
            pstmt.setString(8, user.getRole());
            pstmt.setString(9, user.getClasse());
            pstmt.setString(10, user.getDepartement());
            pstmt.setBoolean(11, user.isEstHabilite());

            int lignes = pstmt.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            return false;
        }
    }
    // Insérer un utilisateur via l'import CSV
    public boolean saveFromCSV(String matricule, String nom, String prenom, String email, String role, String ufr, String departement, String classe) {
        // On génère un mot de passe par défaut (le matricule) qu'on crypte
        String defaultPassword = PasswordUtil.hashPassword(matricule);
        String sql = "INSERT INTO users (matricule, ufr, nom, prenom, email, login, password, role, classe, departement) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matricule);
            pstmt.setString(2, ufr);
            pstmt.setString(3, nom);
            pstmt.setString(4, prenom);
            pstmt.setString(5, email);
            pstmt.setString(6, email); // login = email
            pstmt.setString(7, defaultPassword);
            pstmt.setString(8, role);
            pstmt.setString(9, classe);
            pstmt.setString(10, departement);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // Si l'email existe déjà, on l'ignore et on continue
            return false;
        }
    }

    // Méthode pour mettre à jour un utilisateur
    public boolean update(User user) {
        String sql = "UPDATE users SET matricule=?, ufr=?, nom=?, prenom=?, email=?, login=?, password=?, role=?, classe=?, departement=?, est_habilite=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getMatricule());
            pstmt.setString(2, user.getUfr());
            pstmt.setString(3, user.getNom());
            pstmt.setString(4, user.getPrenom());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getEmail()); // login = email
            pstmt.setString(7, user.getPassword());
            pstmt.setString(8, user.getRole());
            pstmt.setString(9, user.getClasse());
            pstmt.setString(10, user.getDepartement());
            pstmt.setBoolean(11, user.isEstHabilite());
            pstmt.setInt(12, user.getId());

            int lignes = pstmt.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour supprimer un utilisateur
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int lignes = pstmt.executeUpdate();
            return lignes > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
            return false;
        }
    }
    // Récupérer toutes les valeurs distinctes (classes, ufr, départements...)
    public List<String> getDistinctValues(String colonne) {
        List<String> values = new ArrayList<>();
        // Requête pour éviter les doublons (DISTINCT) et ignorer les champs vides
        String sql = "SELECT DISTINCT " + colonne + " FROM users WHERE " + colonne + " IS NOT NULL AND " + colonne + " != ''";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                values.add(rs.getString(colonne));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return values;
    }
    // Récupérer les valeurs distinctes avec un filtre
    public List<String> getFilteredValues(String targetColumn, String filterColumn, String filterValue) {
        List<String> values = new ArrayList<>();
        String sql = "SELECT DISTINCT " + targetColumn + " FROM users WHERE " + targetColumn + " IS NOT NULL AND " + targetColumn + " != ''";

        // Si on a choisi un filtre (ex: UFR = STA), on l'ajoute à la requête
        if (filterValue != null && !filterValue.equals("Toutes")) {
            sql += " AND " + filterColumn + " = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (filterValue != null && !filterValue.equals("Toutes")) {
                pstmt.setString(1, filterValue);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                values.add(rs.getString(targetColumn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return values;
    }
}