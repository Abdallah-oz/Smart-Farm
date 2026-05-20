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
       


        zone1.afficherVueEnsemble();

    zone1.setStatut(Statut.SUSPENDUE);
        

       zone1.afficherAnimaux(zone1.getAnimaux());
    
}
}
