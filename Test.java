package TP;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        System.out.println("--- Test GestionnaireCapteurs ---");

        GestionnaireCapteurs.clearCapteurs();

        ZoneCulture zoneCulture = new ZoneCulture("ZC01", "Zone Culture");
        ZoneElevage zoneElevage = new ZoneElevage("ZE01", "Zone Elevage", TypeElevage.RUMINANT, 20.0, 15.0);

        CapteurMesure capteurCulture = new CapteurMesure("CM01", zoneCulture, 6.0, 7.5, "pH", TypeMesure.PH_SOL);
        CapteurMesure capteurElevage = new CapteurMesure("CM02", zoneElevage, 15.0, 35.0, "°C", TypeMesure.TEMPERATURE);
        CapteurGPS gpsElevage1 = new CapteurGPS("GPS01", zoneElevage, 0.0, 15.0);
        gpsElevage1.setLatitude(5.0);
        gpsElevage1.setLongitude(5.0);

        System.out.println("\nTotal de capteurs enregistrés : " + GestionnaireCapteurs.getCapteurs().size());
        for (Capteur capteur : GestionnaireCapteurs.getCapteurs()) {
            System.out.println("- " + capteur.getCode() + " (zone=" + capteur.getZone().getNom() + ")");
        }

        System.out.println("\nCapteurs dans la zone Culture :");
        List<Capteur> capteursZoneCulture = GestionnaireCapteurs.getCapteursByZone(zoneCulture);
        for (Capteur capteur : capteursZoneCulture) {
            System.out.println("- " + capteur.getCode());
        }

        System.out.println("\nCapteurs dans la zone Elevage :");
        List<Capteur> capteursZoneElevage = GestionnaireCapteurs.getCapteursByZone(zoneElevage);
        for (Capteur capteur : capteursZoneElevage) {
            System.out.println("- " + capteur.getCode());
        }

        System.out.println("\nVérifier que getCapteursByZone renvoie le bon nombre de capteurs :");
        System.out.println("Zone Culture attend 1, obtenu " + capteursZoneCulture.size());
        System.out.println("Zone Elevage attend 2, obtenu " + capteursZoneElevage.size());

        System.out.println("\nAffichage direct des capteurs de la zone Elevage via la zone :");
        zoneElevage.afficherCapteurs();

        System.out.println("\nEffacement des capteurs et vérification :");
        GestionnaireCapteurs.clearCapteurs();
        System.out.println("Total après clear : " + GestionnaireCapteurs.getCapteurs().size());
    }
}
