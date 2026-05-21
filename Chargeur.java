package TP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Chargeur {

    public static Map<String, Zone> chargerZones(String fichier) {
        Map<String, Zone> zones = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fichier));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parties = ligne.split(",");
                String type = parties[0];
                String code = parties[1];
                String nom = parties[2];
                if (type.equals("CULTURE")) {
                    zones.put(code, new ZoneCulture(code, nom));
                } else if (type.equals("ELEVAGE")) {
                    TypeElevage typeElevage = TypeElevage.valueOf(parties[3]);
                    double longueur = Double.parseDouble(parties[4]);
                    double largeur = Double.parseDouble(parties[5]);
                    zones.put(code, new ZoneElevage(code, nom, typeElevage, longueur, largeur));
                } else if (type.equals("AQUACOLE")) {
                    zones.put(code, new ZoneAquacole(code, nom, parties[3]));
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Erreur chargerZones: " + e.getMessage());
        }
        return zones;
    }

    public static void chargerCultures(String fichier, Map<String, Zone> zones) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fichier));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parties = ligne.split(",");
                String codeZone = parties[0];
                FamilleCulture famille = FamilleCulture.valueOf(parties[1]);
                LocalDateTime datePlantation = LocalDateTime.parse(parties[2]);
                LocalDateTime dateRecolte = LocalDateTime.parse(parties[3]);
                double exigencesPH = Double.parseDouble(parties[4]);

                Zone z = zones.get(codeZone);
                if (z instanceof ZoneCulture) {
                    Culture culture = new Culture(famille, datePlantation, dateRecolte, exigencesPH);
                    ((ZoneCulture) z).ajouterCulture(culture);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Erreur chargerCultures: " + e.getMessage());
        }
    }

    public static void chargerAnimaux(String fichier, Map<String, Zone> zones) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fichier));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parties = ligne.split(",");
                String type = parties[0];
                String codeZone = parties[1];
                String numero = parties[2];
                String nom = parties[3];
                String espece = parties[4];
                int age = Integer.parseInt(parties[5]);
                double poids = Double.parseDouble(parties[6]);
                EtatSante etat = EtatSante.valueOf(parties[7]);

                Zone z = zones.get(codeZone);
                if (z instanceof ZoneElevage) {
                    Animal animal;
                    if (type.equals("RUMINANT")) {
                        animal = new Ruminant(numero, nom, espece, age, poids, etat);
                    } else {
                        animal = new Volaille(numero, nom, espece, age, poids, etat);
                    }
                    ((ZoneElevage) z).ajouterAnimal(animal);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Erreur chargerAnimaux: " + e.getMessage());
        }
    }

    public static void chargerCapteurs(String fichier, Map<String, Zone> zones) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fichier));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parties = ligne.split(",");
                String type = parties[0];
                String code = parties[1];
                String codeZone = parties[2];
                double seuilMin = Double.parseDouble(parties[3]);
                double seuilMax = Double.parseDouble(parties[4]);

                Zone z = zones.get(codeZone);
                if (z == null) {
                    continue;
                }
                if (type.equals("MESURE")) {
                    String unite = parties[5];
                    TypeMesure typeMesure = TypeMesure.valueOf(parties[6]);
                    new CapteurMesure(code, z, seuilMin, seuilMax, unite, typeMesure);
                } else if (type.equals("GPS")) {
                    double latitude = Double.parseDouble(parties[5]);
                    double longitude = Double.parseDouble(parties[6]);
                    CapteurGPS gps = new CapteurGPS(code, z, seuilMin, seuilMax);
                    gps.setLatitude(latitude);
                    gps.setLongitude(longitude);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Erreur chargerCapteurs: " + e.getMessage());
        }
    }
}