package TP;
import java.time.LocalDateTime;

public class Test {
    public static void main(String[]args){
        //cree une zone

        ZoneElevage zone1 = new ZoneElevage("13DR3", "Zone name", TypeElevage.RUMINANT);
        Volaille v1 = new Volaille("V001", "Poulet1", "Poulet", 2, 1.5, EtatSante.SAIN);
        zone1.ajouterAnimal(v1);
        Volaille v2 = new Volaille("V002", "Poulet2", "Poulet", 3, 1.8, EtatSante.SAIN);
        zone1.ajouterAnimal(v2);
        //on gere les capteurs
      
        CapteurMesure capteur1 = new CapteurMesure("C001", zone1, 0, 10, "kg", TypeMesure.AZOTE);
        capteur1.setValeur(100);
        Releve releve1 = capteur1.envoyerReleve();
        System.out.println("Relevé du capteur de mesure: " + releve1.getValeur() + " " + capteur1.getUnite());
}
}
