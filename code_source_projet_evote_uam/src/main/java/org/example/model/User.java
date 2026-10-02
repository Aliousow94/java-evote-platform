package org.example.model;

public class User {
    private int id;
    private String login;
    private String password;
    private String role; // ADMIN, ETUDIANT, ENSEIGNANT
    private String matricule;
    private String ufr;

    public String getUfr() { return ufr; }
    public void setUfr(String ufr) { this.ufr = ufr; }
    private String nom;
    private String prenom;
    private String email;
    private String classe;
    private String departement;
    private boolean estHabilite;

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public String getDepartement() { return departement; }
    public void setDepartement(String departement) { this.departement = departement; }

    public boolean isEstHabilite() { return estHabilite; }
    public void setEstHabilite(boolean estHabilite) { this.estHabilite = estHabilite; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Getters et Setters
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    // Constructeur vide
    public User() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
