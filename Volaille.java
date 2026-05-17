package TP;

public class Volaille extends Animal {
    private int productionOeufs;

    public Volaille(String numero, String nom, String espece, int age, double poids, EtatSante etatSante) {
        super(numero, nom, espece, age, poids, etatSante);
        this.productionOeufs = 0;
    }

    public void enregistrerProdOeufs(int productionOeufs) {
        this.productionOeufs = productionOeufs;
    }

    @Override
    public void afficher() {
        super.afficher();
        System.out.println("Production d'oeufs: " + this.productionOeufs);
    }

    // Getter
    public int getProductionOeufs() {
        return productionOeufs;
    }

    public void setProductionOeufs(int productionOeufs) {
        this.productionOeufs = productionOeufs;
    }
}
