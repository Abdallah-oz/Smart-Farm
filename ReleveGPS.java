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
        double ecartLatitude = 0.0;
        double ecartLongitude = 0.0;
        boolean latitudeHorsBornes = false;
        boolean longitudeHorsBornes = false;

        if (this.latitude < getCapteur().getSeuilMin()) {
            latitudeHorsBornes = true;
            ecartLatitude = getCapteur().getSeuilMin() - this.latitude;
        } else if (this.latitude > getCapteur().getSeuilMax()) {
            latitudeHorsBornes = true;
            ecartLatitude = this.latitude - getCapteur().getSeuilMax();
        }

        if (this.longitude < getCapteur().getSeuilMin()) {
            longitudeHorsBornes = true;
            ecartLongitude = getCapteur().getSeuilMin() - this.longitude;
        } else if (this.longitude > getCapteur().getSeuilMax()) {
            longitudeHorsBornes = true;
            ecartLongitude = this.longitude - getCapteur().getSeuilMax();
        }

        if (latitudeHorsBornes || longitudeHorsBornes) {
            double ecartMax = Math.max(ecartLatitude, ecartLongitude);
            Gravite gravite = ecartMax > 1.0 ? Gravite.CRITIQUE : Gravite.AVERTISSEMENT;
            throw new AlerteException("Seuil dépassé: GPS position = (" + this.latitude + ", " + this.longitude + ")", gravite);
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

