package TP;

public abstract class Zone {
    private String code;
    private String nom;
    private Statut statutZone;

    public Zone(String code, String nom) {
        this.code = code;
        this.nom = nom;
        this.statutZone = Statut.ACTIVE;
    }

    public void changerStatut(Statut statut) {
        this.statutZone = statut;
    }

    public abstract void afficherVueEnsemble();

    // Getters
    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    public Statut getStatut() {
        return statutZone;
    }

    public void setStatut(Statut statut) {
        this.statutZone = statut;
    }
}
