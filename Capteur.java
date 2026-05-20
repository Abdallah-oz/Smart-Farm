package TP;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public abstract class Capteur {
    private String code;
    private Zone zone;
    private StatutCapteur statut;
    private double seuilMin;
    private double seuilMax;
    private List<Releve> historique = new ArrayList<>();

    public Capteur(String code, Zone zone, double seuilMin, double seuilMax) {
        this.code = code;
        this.zone = zone;
        this.seuilMin = seuilMin;
        this.seuilMax = seuilMax;
        this.statut = StatutCapteur.ACTIF;
        GestionnaireCapteurs.ajouterCapteur(this);
    }

    public void desactiver() {
        this.statut = StatutCapteur.SUSPENDU;
    }

    public void activer() {
        if (this.statut != StatutCapteur.DEFAILLANT) {
            this.statut = StatutCapteur.ACTIF;
        }
    }

    public List<Releve> getHistorique(LocalDateTime debut, LocalDateTime fin) {
        List<Releve> resultat = new ArrayList<>();
        for (Releve releve : historique) {
            if (releve.getDateHeure().isAfter(debut) && releve.getDateHeure().isBefore(fin)) {
                resultat.add(releve);
            }
        }
        return resultat;
    }

    public List<Releve> getHistorique() {
        return historique;
    }

    protected void enregistrerReleve(Releve releve) {
        this.historique.add(releve);
    }

    public abstract Alerte envoyerReleve();

    public abstract void afficher();

    // Getters
    public String getCode() {
        return code;
    }

    public Zone getZone() {
        return zone;
    }

    public StatutCapteur getStatut() {
        return statut;
    }

    public double getSeuilMin() {
        return seuilMin;
    }

    public double getSeuilMax() {
        return seuilMax;
    }

    public void setStatut(StatutCapteur statut) {
        this.statut = statut;
    }
}
