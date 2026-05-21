package TP;

import java.time.LocalDateTime;
import java.util.Map;

public class Scenario {
    public static void main(String[] args) {

        // ===== CHARGEMENT DES DONNEES =====
        System.out.println("========== CHARGEMENT DES DONNEES ==========");
        Map<String, Zone> zones = Chargeur.chargerZones("TP/csv/zones.csv");
        Chargeur.chargerCultures("TP/csv/cultures.csv", zones);
        Chargeur.chargerAnimaux("TP/csv/animaux.csv", zones);
        Chargeur.chargerCapteurs("TP/csv/capteurs.csv", zones);

        ZoneCulture zc = (ZoneCulture) zones.get("ZC01");
        ZoneElevage ze = (ZoneElevage) zones.get("ZE01");
        ZoneAquacole za = (ZoneAquacole) zones.get("ZA01");
        System.out.println("Donnees chargees avec succes");

        // ===== 1. GERER LES ZONES =====
        System.out.println("\n========== 1. GERER LES ZONES ==========");
        zc.afficherVueEnsemble();
        ze.afficherVueEnsemble();
        za.afficherVueEnsemble();

        System.out.println("\n--- Suspendre ZoneCulture ---");
        zc.changerStatut(Statut.SUSPENDUE);
        System.out.println("Statut ZC01: " + zc.getStatut());
        zc.changerStatut(Statut.ACTIVE);
        System.out.println("Statut ZC01 apres reactivation: " + zc.getStatut());

        // ===== 2. GERER LES CULTURES =====
        System.out.println("\n========== 2. GERER LES CULTURES ==========");
        zc.rapportCulture();

        System.out.println("\n--- Mettre a jour stade et rendement ---");
        Culture culture = zc.getCultures().get(0);
        culture.mettreAJourStade(StadeCroissance.RECOLTE);
        culture.enregistrerRendement(1200.0);
        zc.calculerRendement();
        System.out.println("Rendement total zone: " + zc.getRendement());

        System.out.println("\n--- Afficher chaque culture ---");
        for (Culture c : zc.getCultures()) {
            c.afficher();
        }

        // ===== 3. GERER LES ANIMAUX =====
        System.out.println("\n========== 3. GERER LES ANIMAUX ==========");
        ze.afficherVueEnsemble();

        System.out.println("\n--- Changer etat sante ---");
        Animal animal1 = ze.getAnimaux().get(0);
        Animal animal2 = ze.getAnimaux().get(1);
        animal1.changerEtatSante(EtatSante.MALADE, 590.0);
        animal2.changerEtatSante(EtatSante.QUARANTAINE, 440.0);

        System.out.println("\n--- Historique sanitaire animal1 ---");
        animal1.afficherHistorique();
        System.out.println("\n--- Historique sanitaire animal2 ---");
        animal2.afficherHistorique();

        System.out.println("\n--- Programme alimentation ---");
        ze.afficherProgramme();

        // ===== 4. GERER LES CAPTEURS =====
        System.out.println("\n========== 4. GERER LES CAPTEURS ==========");

        System.out.println("\n--- Capteurs par zone ---");
        zc.afficherCapteurs();
        ze.afficherCapteurs();

        // récupérer capteurs depuis la liste
        CapteurMesure cm = null;
        CapteurMesure cm02 = null;
        CapteurGPS gps1 = null;
        CapteurGPS gps2 = null;

        for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
            if (c.getCode().equals("CM01")) cm = (CapteurMesure) c;
            if (c.getCode().equals("CM02")) cm02 = (CapteurMesure) c;
            if (c.getCode().equals("GPS01")) gps1 = (CapteurGPS) c;
            if (c.getCode().equals("GPS02")) gps2 = (CapteurGPS) c;
        }

        System.out.println("\n--- Changer statut CM01 ---");
        cm.desactiver();
        System.out.println("Statut CM01: " + cm.getStatut());
        cm.activer();
        System.out.println("Statut CM01 apres reactivation: " + cm.getStatut());

        System.out.println("\n--- Envoyer mesures ---");
        Alerte a1 = cm.envoyerMesure(7.0);
        if (a1 == null) System.out.println("CM01 (7.0): NORMAL");
        else a1.afficher();

        Alerte a2 = cm.envoyerMesure(9.0);
        if (a2 == null) System.out.println("CM01 (9.0): NORMAL");
        else a2.afficher();

        Alerte a3 = cm02.envoyerMesure(45.0);
        if (a3 == null) System.out.println("CM02 (45.0): NORMAL");
        else a3.afficher();

        System.out.println("\n--- Envoyer positions GPS ---");
        Alerte a4 = gps1.envoyerPosition();
        if (a4 == null) System.out.println("GPS01: NORMAL");
        else a4.afficher();

        Alerte a5 = gps2.envoyerPosition();
        if (a5 == null) System.out.println("GPS02: NORMAL");
        else a5.afficher();

        System.out.println("\n--- Tableau de bord ---");
        TableauDeBord.afficherTableauDeBord(GestionnaireCapteurs.getCapteurs());

        System.out.println("\n--- Historique CM01 complet ---");
        GestionnaireHistorique.afficherHistorique(cm);

        System.out.println("\n--- Historique CM01 filtre ---");
        GestionnaireHistorique.afficherHistorique(cm,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1));
    }
}