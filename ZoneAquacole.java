package TP;

public class ZoneAquacole extends Zone {
    private String espece;
    private double poidRecolte;

    public ZoneAquacole(String code, String nom, String espece) {
        super(code, nom);
        this.espece = espece;
        this.poidRecolte = 0.0;
    }

    public void ajouterEspece(String espece) {
        this.espece = espece;
    }

    public void affecterPoidRecolte(double poidRecolte) {
        this.poidRecolte = poidRecolte;
    }

    @Override
    public void afficherVueEnsemble() {
        System.out.println("=== Vue Ensemble Zone Aquacole ===");
        System.out.println("Code: " + this.getCode());
        System.out.println("Nom: " + this.getNom());
        System.out.println("Statut: " + this.getStatut());
        System.out.println("Espèce: " + this.espece);
        System.out.println("Poids à la récolte: " + this.poidRecolte);
    }

    // Getters and Setters
    public String getEspece() {
        return espece;
    }

    public void setEspece(String espece) {
        this.espece = espece;
    }

    public double getPoidRecolte() {
        return poidRecolte;
    }

    public void setPoidRecolte(double poidRecolte) {
        this.poidRecolte = poidRecolte;
    }
}
