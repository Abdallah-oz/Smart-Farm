package TP;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EvenementSanitaire {
    private LocalDateTime dateHeure;
    private String description;
    private EtatSante ancienEtat;
    private EtatSante nouvelEtat;
    private double ancienPoids;
    private double nouveauPoids;

    public EvenementSanitaire(String description, EtatSante ancienEtat, EtatSante nouvelEtat, double ancienPoids, double nouveauPoids) {
        this.dateHeure = LocalDateTime.now();
        this.description = description;
        this.ancienEtat = ancienEtat;
        this.nouvelEtat = nouvelEtat;
        this.ancienPoids = ancienPoids;
        this.nouveauPoids = nouveauPoids;
    }

    public void afficher() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("=== Evenement Sanitaire ===");
        System.out.println("Date/Heure: " + dateHeure.format(fmt));
        System.out.println("Description: " + description);
        System.out.println("Ancien état: " + ancienEtat);
        System.out.println("Nouvel état: " + nouvelEtat);
        System.out.println("Ancien poids: " + ancienPoids);
        System.out.println("Nouveau poids: " + nouveauPoids);
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public String getDescription() {
        return description;
    }

    public EtatSante getAncienEtat() {
        return ancienEtat;
    }

    public EtatSante getNouvelEtat() {
        return nouvelEtat;
    }

    public double getAncienPoids() {
        return ancienPoids;
    }

    public double getNouveauPoids() {
        return nouveauPoids;
    }
}
