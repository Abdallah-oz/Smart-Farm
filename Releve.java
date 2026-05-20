package TP;

import java.time.LocalDateTime;

public abstract class Releve {
    private LocalDateTime dateHeure;
    private Capteur capteur;

    public Releve(Capteur capteur) {
        this.dateHeure = LocalDateTime.now();
        this.capteur = capteur;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public Capteur getCapteur() {
        return capteur;
    }

    public abstract void verifierSeuil() throws AlerteException;

    public abstract void afficher();

    public abstract double getValeur();

    public abstract double getLongitude();

    public abstract boolean isGPS();
}
