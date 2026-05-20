package TP;

public class ReleveGPS extends Releve {
    private double latitude;
    private double longitude;

    public ReleveGPS(double latitude, double longitude, Capteur capteur) {
        super(capteur);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public void verifierSeuil() throws AlerteException {
        ZoneElevage zone = (ZoneElevage) getCapteur().getZone();
        double x0 = zone.getLongueur();
        double y0 = zone.getLargeur();
        double distance = Math.sqrt(Math.pow(latitude - x0, 2) + Math.pow(longitude - y0, 2));

        if (distance > getCapteur().getSeuilMax()) {
            Gravite gravite = distance > getCapteur().getSeuilMax() * 2 ? Gravite.CRITIQUE : Gravite.AVERTISSEMENT;
            throw new AlerteException("Animal hors zone, distance = " + distance, gravite);
        }
    }

    @Override
    public void afficher() {
        System.out.println("=== Relevé ===");
        System.out.println("Latitude: " + this.latitude);
        System.out.println("Longitude: " + this.longitude);
        System.out.println("Date/Heure: " + getDateHeure());
        System.out.println("Capteur: " + getCapteur().getCode());
        System.out.println("Zone: " + getCapteur().getZone().getNom());
    }

    @Override
    public double getValeur() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public boolean isGPS() {
        return true;
    }
}

