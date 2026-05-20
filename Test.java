package TP;

import java.time.LocalDateTime;
import java.util.List;

public class Test {
    public static void main(String[] args) {

     List<Zone> zones = Chargeur.chargerZones("TP/zones.csv");

ZoneCulture zoneCulture = null;
ZoneElevage zoneElevage = null;
ZoneAquacole zoneAquacole = null;

for (Zone z : zones) {
    if (z instanceof ZoneCulture) zoneCulture = (ZoneCulture) z;
    else if (z instanceof ZoneElevage) zoneElevage = (ZoneElevage) z;
    else if (z instanceof ZoneAquacole) zoneAquacole = (ZoneAquacole) z;
}

zoneCulture.afficherVueEnsemble();
zoneElevage.afficherVueEnsemble();
zoneAquacole.afficherVueEnsemble();
    }
}
