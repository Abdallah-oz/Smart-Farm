package TP;

import java.util.List;

public class TableauDeBord {
    public static void afficherTableauDeBord(List<Capteur> capteurs) {
        System.out.println("=== TABLEAU DE BORD DES RELEVES ===");
        String zoneCourante = null;

        for (Capteur capteur : capteurs) {
            String zoneNom = capteur.getZone().getNom();
            if (zoneCourante == null || !zoneCourante.equals(zoneNom)) {
                zoneCourante = zoneNom;
                System.out.println("\nZone: " + zoneNom);
            }

            Alerte alerte = capteur.envoyerReleve();
            System.out.print("Capteur: " + capteur.getCode() + " - ");
            if (alerte == null) {
                System.out.println("NORMAL");
            } else if (alerte.getGravite() == Gravite.AVERTISSEMENT) {
                System.out.println("AVERTISSEMENT");
            } else if (alerte.getGravite() == Gravite.CRITIQUE) {
                System.out.println("CRITIQUE");
            } else {
                System.out.println("UNKNOWN");
            }
        }
    }
}
