package TP;

import java.util.ArrayList;
import java.util.List;

public class ZoneElevage extends Zone {
    private TypeElevage type;
    private List<Animal> animaux;
    private double longueur;
    private double largeur;
    private ProgrammeAlimentation programme;

    public ZoneElevage(String code, String nom, TypeElevage type) {
        super(code, nom);
        this.type = type;
        this.animaux = new ArrayList<>();
        this.programme = null;
    }

    public void ajouterAnimal(Animal animal) {
        this.animaux.add(animal);
    }

    public void definirProgramme(ProgrammeAlimentation programme) {
        this.programme = programme;
    }

    public void afficherProgramme() {
        if (this.programme != null) {
            this.programme.afficher();
        } else {
            System.out.println("Aucun programme d'alimentation défini pour cette zone.");
        }
    }

    @Override
    public void afficherVueEnsemble() {
        System.out.println("=== Vue Ensemble Zone Elevage ===");
        System.out.println("Code: " + this.getCode());
        System.out.println("Nom: " + this.getNom());
        System.out.println("Statut: " + this.getStatut());
        System.out.println("Type d'élevage: " + this.type);
        System.out.println("Nombre d'animaux: " + this.animaux.size());
        if (this.programme != null) {
            System.out.println("Programme alimentation: " + this.programme.getAliment());
        }
    }

    // Getters and Setters
    public TypeElevage getType() {
        return type;
    }

    public void setType(TypeElevage type) {
        this.type = type;
    }

    public double getLongueur() {
        return longueur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public double getLargeur() {
        return largeur;
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
    }

    public List<Animal> getAnimaux() {
        return animaux;
    }
    //afficher lensemble des animaux de la zone
    public void afficherAnimaux(List<Animal> animaux) {
        System.out.println("=== Animaux dans la Zone Elevage ===");
        for (Animal animal : this.animaux) {
            animal.afficher();
            System.out.println();
        }
    }
    public void setAnimaux(List<Animal> animaux) {
        this.animaux = animaux;
    }

    public ProgrammeAlimentation getProgramme() {
        return programme;
    }

    public void setProgramme(ProgrammeAlimentation programme) {
        this.programme = programme;
    }
}
