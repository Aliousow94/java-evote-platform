package org.example.model;

public class Election {
    private int id;
    private String titre;
    private String typeElection;
    private String dateDebut;
    private String dateFin;
    private String statut;
    private String electeurRole;
    private String electeurCible;
    private String cibleUfr;
    private String cibleDepartement;
    private String cibleClasse;

    public String getCibleUfr() { return cibleUfr; }
    public void setCibleUfr(String cibleUfr) { this.cibleUfr = cibleUfr; }

    public String getCibleDepartement() { return cibleDepartement; }
    public void setCibleDepartement(String cibleDepartement) { this.cibleDepartement = cibleDepartement; }

    public String getCibleClasse() { return cibleClasse; }
    public void setCibleClasse(String cibleClasse) { this.cibleClasse = cibleClasse; }

    public Election() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getTypeElection() { return typeElection; }
    public void setTypeElection(String typeElection) { this.typeElection = typeElection; }

    public String getDateDebut() { return dateDebut; }
    public void setDateDebut(String dateDebut) { this.dateDebut = dateDebut; }

    public String getDateFin() { return dateFin; }
    public void setDateFin(String dateFin) { this.dateFin = dateFin; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getElecteurRole() { return electeurRole; }
    public void setElecteurRole(String electeurRole) { this.electeurRole = electeurRole; }

    public String getElecteurCible() { return electeurCible; }
    public void setElecteurCible(String electeurCible) { this.electeurCible = electeurCible; }
    @Override
    public String toString() {
        return id + " - " + titre;
    }
}
