package TP;

import java.time.LocalDateTime;

public class Releve {
    private double valeur;
    private double longitude;
    private boolean isGPS;
    private LocalDateTime dateHeure;
    private Capteur capteur;

    public Releve(double valeur, Capteur capteur) {
        this.valeur = valeur;
        this.longitude = 0.0;
        this.isGPS = false;
        this.capteur = capteur;
        this.dateHeure = LocalDateTime.now();
    }

    public Releve(double latitude, double longitude, Capteur capteur) {
        this.valeur = latitude;
        this.longitude = longitude;
        this.isGPS = true;
        this.capteur = capteur;
        this.dateHeure = LocalDateTime.now();
    }

    public void verifierSeuil() throws AlerteException {
        if (this.isGPS) {
            double ecartLatitude = 0.0;
            double ecartLongitude = 0.0;
            boolean latitudeHorsBornes = false;
            boolean longitudeHorsBornes = false;

            if (this.valeur < this.capteur.getSeuilMin()) {
                latitudeHorsBornes = true;
                ecartLatitude = this.capteur.getSeuilMin() - this.valeur;
            } else if (this.valeur > this.capteur.getSeuilMax()) {
                latitudeHorsBornes = true;
                ecartLatitude = this.valeur - this.capteur.getSeuilMax();
            }

            if (this.longitude < this.capteur.getSeuilMin()) {
                longitudeHorsBornes = true;
                ecartLongitude = this.capteur.getSeuilMin() - this.longitude;
            } else if (this.longitude > this.capteur.getSeuilMax()) {
                longitudeHorsBornes = true;
                ecartLongitude = this.longitude - this.capteur.getSeuilMax();
            }

            if (latitudeHorsBornes || longitudeHorsBornes) {
                double ecartMax = Math.max(ecartLatitude, ecartLongitude);
               
            Gravite gravite;
            if (ecartMax > 1.0) {
            gravite = Gravite.CRITIQUE;
            } else {
            gravite = Gravite.AVERTISSEMENT;
            }
                throw new AlerteException("Seuil dépassé: GPS position = (" + this.valeur + ", " + this.longitude + ")", gravite);
            }
        } else {
            double ecart = 0.0;
            if (this.valeur < this.capteur.getSeuilMin()) {
                ecart = this.capteur.getSeuilMin() - this.valeur;
            } else if (this.valeur > this.capteur.getSeuilMax()) {
                ecart = this.valeur - this.capteur.getSeuilMax();
            }

            if (ecart > 0.0) {
                Gravite gravite = ecart > 1.0 ? Gravite.CRITIQUE : Gravite.AVERTISSEMENT;
                throw new AlerteException("Seuil dépassé: valeur = " + this.valeur, gravite);
            }
        }
    }

    public void afficher() {
        System.out.println("=== Relevé ===");
        if (this.isGPS) {
            System.out.println("Latitude: " + this.valeur);
            System.out.println("Longitude: " + this.longitude);
        } else {
            System.out.println("Valeur: " + this.valeur);
        }
        System.out.println("Date/Heure: " + this.dateHeure);
        System.out.println("Capteur: " + this.capteur.getCode());
        System.out.println("Zone: " + this.capteur.getZone().getNom());
    }

    // Getters
    public double getValeur() {
        return valeur;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isGPS() {
        return isGPS;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public Capteur getCapteur() {
        return capteur;
    }
}
