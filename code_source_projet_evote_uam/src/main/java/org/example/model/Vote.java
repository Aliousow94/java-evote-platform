package org.example.model;

public class Vote {
    private int id;
    private int electionId;
    private int candidatId;
    private int utilisateurId;
    private String dateVote;

    public Vote() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getElectionId() { return electionId; }
    public void setElectionId(int electionId) { this.electionId = electionId; }
    public int getCandidatId() { return candidatId; }
    public void setCandidatId(int candidatId) { this.candidatId = candidatId; }
    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getDateVote() { return dateVote; }
    public void setDateVote(String dateVote) { this.dateVote = dateVote; }
}
