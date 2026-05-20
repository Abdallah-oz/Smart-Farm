package TP;

public class ReleveMesure extends Releve {
    private double valeur;

    public ReleveMesure(double valeur, Capteur capteur) {
        super(capteur);
        this.valeur = valeur;
    }

    @Override
    public void verifierSeuil() throws AlerteException {
        double ecart = 0.0;
        if (this.valeur < getCapteur().getSeuilMin()) {
            ecart = getCapteur().getSeuilMin() - this.valeur;
        } else if (this.valeur > getCapteur().getSeuilMax()) {
            ecart = this.valeur - getCapteur().getSeuilMax();
        }

        if (ecart > 0.0) {
            Gravite gravite = ecart > 1.0 ? Gravite.CRITIQUE : Gravite.AVERTISSEMENT;
            throw new AlerteException("Seuil dépassé: valeur = " + this.valeur, gravite);
        }
    }

    @Override
    public void afficher() {
        System.out.println("=== Relevé ===");
        System.out.println("Valeur: " + this.valeur);
        System.out.println("Date/Heure: " + getDateHeure());
        System.out.println("Capteur: " + getCapteur().getCode());
        System.out.println("Zone: " + getCapteur().getZone().getNom());
    }

    @Override
    public double getValeur() {
        return valeur;
    }

    @Override
    public double getLongitude() {
        return 0.0;
    }

    @Override
    public boolean isGPS() {
        return false;
    }
}
