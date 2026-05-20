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
        
        // Si la zone devient SUSPENDUE, désactiver tous les capteurs associés
        if (statut == Statut.SUSPENDUE) {
            for (Capteur capteur : GestionnaireCapteurs.getCapteurs()) {
                if (capteur.getZone().getCode().equals(this.code)) {
                    capteur.desactiver();
                }
            }
        }
        // Si la zone devient ACTIVE, réactiver tous les capteurs associés
        else if (statut == Statut.ACTIVE) {
            for (Capteur capteur : GestionnaireCapteurs.getCapteurs()) {
                if (capteur.getZone().getCode().equals(this.code)) {
                    capteur.activer();
                }
            }
        }
    }

    public abstract void afficherVueEnsemble();

    public void afficherCapteurs() {
        java.util.List<Capteur> capteurs = GestionnaireCapteurs.getCapteursByZone(this);
        System.out.println("Zone: " + this.nom);
        if (capteurs.isEmpty()) {
            System.out.println("Aucun capteur dans cette zone");
            return;
        }
        for (Capteur c : capteurs) {
            c.afficher();
        }
    }

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
        this.changerStatut(statut);
    }
}
