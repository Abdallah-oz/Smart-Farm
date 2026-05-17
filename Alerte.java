package TP;

public class Alerte {
    private Releve releve;
    private Gravite gravite;
    private boolean acquittee;

    public Alerte(Releve releve, Gravite gravite) {
        this.releve = releve;
        this.gravite = gravite;
        this.acquittee = false;
    }

    public void acquitter() {
        this.acquittee = true;
    }

    public void supprimer() {
        System.out.println("Alerte supprimée avec succès.");
    }

    public void afficher() {
        System.out.println("=== Alerte ===");
        System.out.println("Gravité: " + this.gravite);
        System.out.println("Acquittée: " + (this.acquittee ? "Oui" : "Non"));
        System.out.println("--- Informations du relevé ---");
        this.releve.afficher();
    }

    // Getters and Setters
    public Releve getReleve() {
        return releve;
    }

    public void setReleve(Releve releve) {
        this.releve = releve;
    }

    public Gravite getGravite() {
        return gravite;
    }

    public void setGravite(Gravite gravite) {
        this.gravite = gravite;
    }

    public boolean isAcquittee() {
        return acquittee;
    }

    public void setAcquittee(boolean acquittee) {
        this.acquittee = acquittee;
    }
}
