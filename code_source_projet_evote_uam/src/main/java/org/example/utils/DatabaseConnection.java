package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // URL de connexion à XAMPP (localhost:3306, base uam_evote)
    private static final String URL = "jdbc:mysql://localhost:3306/uam_evote?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Sur XAMPP, le mot de passe est vide par défaut

    // Méthode pour obtenir la connexion
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Chargement du driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établissement de la connexion
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base de données réussie !");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC introuvable. Vérifie ton fichier pom.xml.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à MySQL. Vérifie que XAMPP (MySQL) est lancé.");
            e.printStackTrace();
        }
        return connection;
    }

    // Méthode de test principale
    public static void main(String[] args) {
        getConnection();
    }
}