package TP;

public class CapteurMesure extends Capteur {
    private double valeur;
    private String unite;
    private TypeMesure type;

    public CapteurMesure(String code, Zone zone, double seuilMin, double seuilMax, String unite, TypeMesure type) {
        super(code, zone, seuilMin, seuilMax);
        this.valeur = 0.0;
        this.unite = unite;
        this.type = type;
    }

    public Alerte envoyerMesure(double valeur) {
        this.valeur = valeur;
        ReleveMesure releve = new ReleveMesure(valeur, this);
        try {
            releve.verifierSeuil();
        } catch (AlerteException e) {
            return new Alerte(releve, e.getGravite());
        }
        return null;
    }

    @Override
    public Alerte envoyerReleve() {
        ReleveMesure releve = new ReleveMesure(this.valeur, this);
        try {
            releve.verifierSeuil();
        } catch (AlerteException e) {
            return new Alerte(releve, e.getGravite());
        }
        return null;
    }

    public void afficher() {
        System.out.println("=== Capteur de Mesure ===");
        System.out.println("Code: " + this.getCode());
        System.out.println("Zone: " + this.getZone().getNom());
        System.out.println("Statut: " + this.getStatut());
        System.out.println("Type de mesure: " + this.type);
        System.out.println("Valeur: " + this.valeur + " " + this.unite);
        System.out.println("Seuil min: " + this.getSeuilMin());
        System.out.println("Seuil max: " + this.getSeuilMax());
    }

    // Getters and Setters
    public double getValeur() {
        return valeur;
    }

    public void setValeur(double valeur) {
        this.valeur = valeur;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public TypeMesure getType() {
        return type;
    }

    public void setType(TypeMesure type) {
        this.type = type;
    }
}
