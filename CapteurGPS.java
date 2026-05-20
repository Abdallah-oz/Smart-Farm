package TP;

public class CapteurGPS extends Capteur {
    private double latitude;
    private double longitude;

    public CapteurGPS(String code, Zone zone, double seuilMin, double seuilMax) {
        super(code, zone, seuilMin, seuilMax);
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public Alerte envoyerPosition() {
        ReleveGPS releve = new ReleveGPS(latitude, longitude, this);
        enregistrerReleve(releve);
        try {
            releve.verifierSeuil();
        } catch (AlerteException e) {
            return new Alerte(releve, e.getGravite());
        }
        return null;
    }

    @Override
    public Alerte envoyerReleve() {
        ReleveGPS releve = new ReleveGPS(latitude, longitude, this);
        enregistrerReleve(releve);
        try {
            releve.verifierSeuil();
        } catch (AlerteException e) {
            return new Alerte(releve, e.getGravite());
        }
        return null;
    }

    public void afficher() {
        System.out.println("=== Capteur GPS ===");
        System.out.println("Code: " + this.getCode());
        System.out.println("Zone: " + this.getZone().getNom());
        System.out.println("Statut: " + this.getStatut());
        System.out.println("Latitude: " + this.latitude);
        System.out.println("Longitude: " + this.longitude);
    }

    // Getters and Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
