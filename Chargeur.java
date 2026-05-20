package TP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Chargeur {

    public static List<Zone> chargerZones(String fichier) {
        List<Zone> zones = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fichier));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parties = ligne.split(",");
                String type = parties[0];
                String code = parties[1];
                String nom = parties[2];

                if (type.equals("CULTURE")) {
                    zones.add(new ZoneCulture(code, nom));
                } else if (type.equals("ELEVAGE")) {
                    TypeElevage typeElevage = TypeElevage.valueOf(parties[3]);
                    zones.add(new ZoneElevage(code, nom, typeElevage));
                } else if (type.equals("AQUACOLE")) {
                    zones.add(new ZoneAquacole(code, nom, parties[3]));
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Erreur lecture fichier: " + e.getMessage());
        }
        return zones;
    }
}