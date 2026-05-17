package TP;

import java.time.LocalDateTime;

public class Culture {
    private FamilleCulture type;
    private LocalDateTime datePlantation;
    private LocalDateTime dateRecolte;
    private StadeCroissance stade;
    private double exigencesPH;
    private double rendement;

    public Culture(FamilleCulture type, LocalDateTime datePlantation, LocalDateTime dateRecolte, double exigencesPH) {
        this.type = type;
        this.datePlantation = datePlantation;
        this.dateRecolte = dateRecolte;
        this.exigencesPH = exigencesPH;
        this.stade = StadeCroissance.SEMIS;
        this.rendement = 0.0;
    }

    public void mettreAJourStade(StadeCroissance stade) {
        this.stade = stade;
    }

    public void enregistrerRendement(double rendement) {
        if (this.stade == StadeCroissance.RECOLTE) {
            this.rendement = rendement;
        }
    }

    public void afficher() {
        System.out.println("=== Culture ===");
        System.out.println("Type: " + this.type);
        System.out.println("Date plantation: " + this.datePlantation);
        System.out.println("Date récolte: " + this.dateRecolte);
        System.out.println("Stade de croissance: " + this.stade);
        System.out.println("Exigences pH: " + this.exigencesPH);
        System.out.println("Rendement: " + this.rendement);
    }

    // Getters and Setters
    public FamilleCulture getType() {
        return type;
    }

    public void setType(FamilleCulture type) {
        this.type = type;
    }

    public LocalDateTime getDatePlantation() {
        return datePlantation;
    }

    public void setDatePlantation(LocalDateTime datePlantation) {
        this.datePlantation = datePlantation;
    }

    public LocalDateTime getDateRecolte() {
        return dateRecolte;
    }

    public void setDateRecolte(LocalDateTime dateRecolte) {
        this.dateRecolte = dateRecolte;
    }

    public StadeCroissance getStade() {
        return stade;
    }

    public void setStade(StadeCroissance stade) {
        this.stade = stade;
    }

    public double getExigencesPH() {
        return exigencesPH;
    }

    public void setExigencesPH(double exigencesPH) {
        this.exigencesPH = exigencesPH;
    }

    public double getRendement() {
        return rendement;
    }

    public void setRendement(double rendement) {
        this.rendement = rendement;
    }
}
