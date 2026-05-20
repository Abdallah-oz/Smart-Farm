package TP;
import java.time.LocalDateTime;


public class Principal {
    public static void main(String[] args){

  //Ici on cree nos zones

    ZoneCulture zoneC = new ZoneCulture("ZC001", "Zone de culture 1");
    ZoneElevage zoneR = new ZoneElevage("ZE001", "Zone d'élevage 1", TypeElevage.RUMINANT);
    ZoneElevage zoneV = new ZoneElevage("ZE002", "Zone d'élevage 2", TypeElevage.VOLAILLE);
    ZoneAquacole zoneA = new ZoneAquacole("ZA001", "Zone aquacole 1", "Sepia");
    

    //on ajoute des cultures à la zone de culture
    Culture culture1 = new Culture(FamilleCulture.CEREALE, LocalDateTime.of(2025, 3, 15, 10, 0), LocalDateTime.of(2025, 8, 30, 10, 0), 7.0);
    zoneC.ajouterCulture(culture1);
    Culture culture2 = new Culture(FamilleCulture.LEGUME, LocalDateTime.of(2025, 4, 1, 10, 0), LocalDateTime.of(2025, 7, 15, 10, 0), 6.5);
    zoneC.ajouterCulture(culture2);

    //on teste la zone culture completement
    culture1.mettreAJourStade(StadeCroissance.RECOLTE);
    culture2.mettreAJourStade(StadeCroissance.RECOLTE);

    culture1.enregistrerRendement(950.0);
    culture2.enregistrerRendement(500.0);

    zoneC.calculerRendement();
    zoneC.rapportCulture();
    
    
}

}
