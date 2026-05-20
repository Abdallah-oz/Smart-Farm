package TP;

import java.util.ArrayList;
import java.util.List;

public class ZoneCulture extends Zone {
    private double rendement;
    private List<Culture> cultures;

    public ZoneCulture(String code, String nom) {
        super(code, nom);
        this.rendement = 0.0;
        this.cultures = new ArrayList<>();
    }

    public void ajouterCulture(Culture culture) {
        this.cultures.add(culture);
    }

    public void calculerRendement() {
        if (this.cultures.isEmpty()) {
            this.rendement = 0.0;
            return;
        }
        double totalRendement = 0.0;
        for (Culture culture : this.cultures) {
            totalRendement += culture.getRendement();
        }
        this.rendement = totalRendement;
    }

    public void rapportCulture() {
        System.out.println("=== Rapport des Cultures - Zone: " + this.getNom() + " ===");
        for (Culture culture : this.cultures) {
            afficherCulture(culture);
        }
        System.out.println("Rendement moyen: " + this.rendement);
    }

    public void afficherCulture(Culture culture) {
        System.out.println("- Type: " + culture.getType() + ", Stade: " + culture.getStade() + 
                           ", Rendement: " + culture.getRendement());
    }

    @Override
    public void afficherVueEnsemble() {
        System.out.println("=== Vue Ensemble Zone Culture ===");
        System.out.println("Code: " + this.getCode());
        System.out.println("Nom: " + this.getNom());
        System.out.println("Statut: " + this.getStatut());
        System.out.println("Nombre de cultures: " + this.cultures.size());
        System.out.println("Rendement: " + this.rendement);
    }

    // Getters and Setters
    public double getRendement() {
        return rendement;
    }

    public void setRendement(double rendement) {
        this.rendement = rendement;
    }

    public List<Culture> getCultures() {
        return cultures;
    }
    //on affiche lensemble des cultures de la zone
    public void afficherCultures(List<Culture> cultures) {
        System.out.println("=== Cultures dans la Zone Culture ===");
        for (Culture culture : this.cultures) {
            culture.afficher();
            System.out.println();
        }
    }

    public void setCultures(List<Culture> cultures) {
        this.cultures = cultures;
    }
}
