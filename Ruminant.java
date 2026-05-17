package TP;

public class Ruminant extends Animal {
    private double productionLait;

    public Ruminant(String numero, String nom, String espece, int age, double poids, EtatSante etatSante) {
        super(numero, nom, espece, age, poids, etatSante);
        this.productionLait = 0.0;
    }

    public void enregistrerProdLait(double productionLait) {
        this.productionLait = productionLait;
    }

    @Override
    public void afficher() {
        super.afficher();
        System.out.println("Production de lait: " + this.productionLait + " L");
    }

    // Getter
    public double getProductionLait() {
        return productionLait;
    }

    public void setProductionLait(double productionLait) {
        this.productionLait = productionLait;
    }
}
