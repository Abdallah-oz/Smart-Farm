package TP;

import java.time.LocalDateTime;

public class Test {
    public static void main(String[] args) {

     // cree une zone de culture
        ZoneCulture zone1 = new ZoneCulture("Zone1", "CODE");
        // cree des cultures 
        //on respecte le constructeur de la classe Culture qui prend en parametre le type, 
        // le stade et le rendement
         
        Culture culture1 = new Culture(FamilleCulture.LEGUME, LocalDateTime.now(), LocalDateTime.now().plusDays(30), 6.5);
        Culture culture2 = new Culture(FamilleCulture.CEREALE, LocalDateTime.now(), LocalDateTime.now().plusDays(30), 7.0);
        zone1.ajouterCulture(culture1);
        zone1.ajouterCulture(culture2);
        zone1.afficherCultures(zone1.getCultures());

        //on ajoute des capteur
        Capteur capteur1 = new CapteurMesure("CAP1", zone1, 5.0, 8.0, "pH", TypeMesure.PH_SOL);
        Capteur capteur2 = new CapteurMesure("CAP2", zone1, 10.0, 30.0, "°C", TypeMesure.TEMPERATURE);
        zone1.afficherCapteurs();

        zone1.changerStatut(Statut.SUSPENDUE);
        zone1.afficherCapteurs();
    }
}
