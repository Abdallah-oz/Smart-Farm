package TP;

import java.util.List;
import java.time.LocalDateTime;

public class GestionnaireHistorique {
    public static void afficherHistorique(Capteur capteur) {
        List<Releve> historique = capteur.getHistorique();
        System.out.println("=== Historique du capteur ===");
        System.out.println("Capteur: " + capteur.getCode());
        System.out.println("Zone: " + capteur.getZone().getNom());
        if (historique == null || historique.isEmpty()) {
            System.out.println("Aucun relevé enregistré");
            return;
        }
        for (Releve releve : historique) {
            releve.afficher();
        }
    }

    public static void afficherHistorique(Capteur capteur, LocalDateTime debut, LocalDateTime fin) {
        List<Releve> historique = capteur.getHistorique(debut, fin);
        System.out.println("=== Historique du capteur ===");
        System.out.println("Capteur: " + capteur.getCode());
        System.out.println("Zone: " + capteur.getZone().getNom());
        System.out.println("Période: " + debut + " à " + fin);
        if (historique == null || historique.isEmpty()) {
            System.out.println("Aucun relevé dans cette période");
            return;
        }
        for (Releve releve : historique) {
            releve.afficher();
        }
    }
}
