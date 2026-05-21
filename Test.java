package TP;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        System.out.println("========================================================================");

        // 1) Charger toutes les données
        Ferme ferme = new Ferme("Tablat");
        Map<String, Zone> zones = Chargeur.chargerZones("TP/csv/zones.csv");

        ZoneElevage ze = (ZoneElevage) zones.get("ZE01");
        ZoneCulture zc = (ZoneCulture) zones.get("ZC01");
        ZoneAquacole za = (ZoneAquacole) zones.get("ZA01");

        Chargeur.chargerCultures("TP/csv/cultures.csv", zones);
        Chargeur.chargerAnimaux("TP/csv/animaux.csv", zones);
        Chargeur.chargerCapteurs("TP/csv/capteurs.csv", zones);

        ferme.ajouterZone(ze);
        ferme.ajouterZone(zc);
        ferme.ajouterZone(za);



        //creation des trois zones + capteurs cultures et animaux

System.out.println("========================================================================");
System.out.println("========================================================================");
System.out.println("========================================================================");


        // 2) Scénario zone
        System.out.println("========== ZONES ==========");
        zc.afficherVueEnsemble();
        ze.afficherVueEnsemble();
        za.afficherVueEnsemble();

        System.out.println("-- Suspension de la zone ZC01 via Ferme --");
        System.out.println("Statut avant: " + zc.getStatut());

       


        ferme.deactiverZone("ZC01");
        System.out.println("Statut après suspension: " + zc.getStatut());

        //on afficher les capteur de la zones pour voir qu'ils sont aussi suspendus
        System.out.println("Capteurs de ZC01 après suspension:");
        zc.afficherCapteurs();



        System.out.println("-- Réactivation de la zone ZC01 via Ferme --");
        ferme.activerZone("ZC01");
        System.out.println("Statut après réactivation: " + zc.getStatut());

zc.afficherCapteurs();


System.out.println("========================================================================");
System.out.println("========================================================================");
System.out.println("========================================================================");
/* */
        // 3) Scénario cultures
        System.out.println("========== CULTURES ==========");
        zc.rapportCulture();

        if (!zc.getCultures().isEmpty()) {
            Culture first = zc.getCultures().get(0);
            System.out.println("Mise à jour du stade de la première culture vers RECOLTE");

            first.mettreAJourStade(StadeCroissance.RECOLTE);
            System.out.println("Enregistrement du rendement pour cette culture");

            first.enregistrerRendement(1200.0);
            zc.calculerRendement();
        }

        System.out.println("Rapport mis à jour :");
        zc.rapportCulture();

        System.out.println("Affichage individuel des cultures :");
        zc.afficherCultures(zc.getCultures());



System.out.println("========================================================================");
System.out.println("========================================================================");
System.out.println("========================================================================");


        // 4) Scénario animaux
        System.out.println("========== ANIMAUX ==========");
        ze.afficherAnimaux(ze.getAnimaux());

        List<Animal> animaux = ze.getAnimaux();
        if (animaux.size() >= 2) {
            Animal a1 = animaux.get(0);
            Animal a2 = animaux.get(1);
            System.out.println("Changement état santé du 1er animal vers MALADE");
            a1.changerEtatSante(EtatSante.MALADE, 3000.0);
            System.out.println("Changement état santé du 2nd animal vers QUARANTAINE");
            a2.changerEtatSante(EtatSante.QUARANTAINE, a2.getPoids());
        }

        System.out.println("Historique sanitaire des animaux :");
        for (Animal a : ze.getAnimaux()) {
            System.out.println("--- Animal: " + a.getNom() + " ---");
            a.afficherHistorique();
        }

        System.out.println("Programme d'alimentation de la zone d'élevage :");
        ze.afficherProgramme();

        //on ajouter un programme d'alimentation à la zone d'élevage
        ProgrammeAlimentation pa = new ProgrammeAlimentation(ze, "Aliment complet",500.0);
        // on ajouter un programme d alimentation a la zone d elevage
        ze.definirProgramme(pa);
        System.out.println("Programme d'alimentation après définition :");
        ze.afficherProgramme();


System.out.println("========================================================================");
System.out.println("========================================================================");
System.out.println("========================================================================");

        // 5) Scénario capteurs
        System.out.println("========== CAPTEURS ==========");
        zc.afficherCapteurs();
        ze.afficherCapteurs();
        za.afficherCapteurs();

        // Utilisation des méthodes de Ferme pour les capteurs
        System.out.println("-- Gestion des capteurs via Ferme --");

        ferme.deactiverCapteur("CM01");
        zc.afficherCapteurs();

        ferme.activerCapteur("CM01");
        zc.afficherCapteurs();


        //Alerte de mesure critique
        System.out.println("Enregistrement mesure normale CM01 (7.0)");
        Alerte aNorm = ferme.enregistrerMesure("CM01", 7.0);
        if (aNorm == null) System.out.println("NORMAL"); else aNorm.afficher();


        System.out.println("Enregistrement mesure critique CM01 (9.0)");
        Alerte aCrit = ferme.enregistrerMesure("CM01", 4.0);
        if (aCrit != null) aCrit.afficher();

        System.out.println("Enregistrement mesure critique CM02 (40.0)");
        Alerte a2 = ferme.enregistrerMesure("CM02", 40.0);
        if (a2 != null) a2.afficher();

        System.out.println("Enregistrement position GPS01 (5.0,5.0)");
        Alerte ag1 = ferme.enregistrerPosition("GPS01", 5.0, 5.0);
        if (ag1 == null) System.out.println("NORMAL"); else ag1.afficher();

        System.out.println("Enregistrement position GPS02 (50.0,50.0)");
        Alerte ag2 = ferme.enregistrerPosition("GPS02", 50.0, 50.0);
        if (ag2 != null) ag2.afficher();

        // Actualiser la ferme (relevés globaux)
        ferme.actualiserFerme();

//fin des gestion des alerte


       //Afficher le capteur filtre par plage de date
       //afficherHistorique(Capteur capteur, LocalDateTime debut, LocalDateTime fin)


        GestionnaireHistorique.afficherHistorique(GestionnaireCapteurs.getCapteurs().get(0), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        // Afficher tableau de bord

        System.out.println("Affichage du tableau de bord :");
        TableauDeBord.afficherTableauDeBord(GestionnaireCapteurs.getCapteurs());


System.out.println("========================================================================");
System.out.println("========================================================================");
System.out.println("========================================================================");

        // 6) Historique
        System.out.println("========== HISTORIQUE ==========");
        Capteur cm01 = null;
        for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
            if (c.getCode().equals("CM01")) {
                cm01 = c;
                break;
            }
        }

        if (cm01 != null) {
            System.out.println("Historique complet de CM01 :");
            GestionnaireHistorique.afficherHistorique(cm01);

            System.out.println("Historique filtré (±1 jour) de CM01 :");
            GestionnaireHistorique.afficherHistorique(cm01, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        } else {
            System.out.println("Capteur CM01 introuvable pour l'historique.");
        }

    



    //Statistique de Gestionnaire d'alerte

System.out.println("========================================================================");
System.out.println("========================================================================");
System.out.println("========================================================================");
//7) Statistiques et affichages avancés du gestionnaire d'alertes

        System.out.println("--- Statistiques du gestionnaire d'alertes ---");
        
        System.out.println("Total alertes : " + ferme.getGestionnaireAlerte().compterTotalAlertes());
        System.out.println("Alertes actives : " + ferme.getGestionnaireAlerte().compterAlertesActives());
        System.out.println("Alertes acquittées : " + ferme.getGestionnaireAlerte().compterAlertesAcquittees());
        System.out.println("Alertes critiques : " + ferme.getGestionnaireAlerte().compterAlertesCritiques());
        System.out.println("Avertissements : " + ferme.getGestionnaireAlerte().compterAvertissements());
        System.out.println();

        System.out.println("--- Affichage des alertes de la zone ZC1 ---");
        ferme.getGestionnaireAlerte().afficherAlerteParZone("zc1");
        System.out.println();

        System.out.println("--- Affichage des alertes critiques ---");
        ferme.getGestionnaireAlerte().afficherAlertesCritiques();
        System.out.println();

        System.out.println("--- Affichage des alertes triées par gravité ---");
        ferme.getGestionnaireAlerte().afficherAlertesTrieesParGravite();
        System.out.println();

        System.out.println("--- Historique complet des alertes ---");
        ferme.getGestionnaireAlerte().afficherHistoriqueAlertes();
        System.out.println();

        System.out.println("--- Nettoyage des alertes ---");
        ferme.getGestionnaireAlerte().supprimerToutesAlertes();
        System.out.println("Total après suppression : " + ferme.getGestionnaireAlerte().compterTotalAlertes());

        System.out.println("--- REAFICHAGE Historique complet des alertes ---");
        ferme.getGestionnaireAlerte().afficherHistoriqueAlertes();
        System.out.println();


}
}
