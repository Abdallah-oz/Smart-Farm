package TP;

public class ProgrammeAlimentation {
    private ZoneElevage zone;
    private String aliment;
    private double quantite;

    public ProgrammeAlimentation(ZoneElevage zone, String aliment, double quantite) {
        this.zone = zone;
        this.aliment = aliment;
        this.quantite = quantite;
    }

    public void afficher() {
        System.out.println("=== Programme d'Alimentation ===");
        System.out.println("Zone: " + this.zone.getNom());
        System.out.println("Aliment: " + this.aliment);
        System.out.println("Quantité: " + this.quantite + " kg");
    }

    // Getters and Setters
    public ZoneElevage getZone() {
        return zone;
    }

    public void setZone(ZoneElevage zone) {
        this.zone = zone;
    }

    public String getAliment() {
        return aliment;
    }

    public void setAliment(String aliment) {
        this.aliment = aliment;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }
}
