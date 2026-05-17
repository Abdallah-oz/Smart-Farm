package TP;

public abstract class Animal {
    private String numero;
    private String nom;
    private String espece;
    private int age;
    private double poids;
    private EtatSante etatSante;

    public Animal(String numero, String nom, String espece, int age, double poids, EtatSante etatSante) {
        this.numero = numero;
        this.nom = nom;
        this.espece = espece;
        this.age = age;
        this.poids = poids;
        this.etatSante = etatSante;
    }

    public void changerEtatSante(EtatSante etatSante) {
        this.etatSante = etatSante;
    }

    public void afficher() {
        System.out.println("=== Animal ===");
        System.out.println("Numéro: " + this.numero);
        System.out.println("Nom: " + this.nom);
        System.out.println("Espèce: " + this.espece);
        System.out.println("Âge: " + this.age);
        System.out.println("Poids: " + this.poids);
        System.out.println("État de santé: " + this.etatSante);
    }

    // Getters and Setters
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEspece() {
        return espece;
    }

    public void setEspece(String espece) {
        this.espece = espece;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getPoids() {
        return poids;
    }

    public void setPoids(double poids) {
        this.poids = poids;
    }

    public EtatSante getEtatSante() {
        return etatSante;
    }

    public void setEtatSante(EtatSante etatSante) {
        this.etatSante = etatSante;
    }
}
