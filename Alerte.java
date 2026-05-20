package TP;
import java.time.LocalDateTime;
import java.util.UUID;

public class Alerte {
    private Releve releve;
    private Gravite gravite;
    private boolean acquittee;
    private String idAlerte;                    
    private LocalDateTime dateCreation;         
    private LocalDateTime dateAcquittement;    


    public Alerte(Releve releve, Gravite gravite) {
    this.idAlerte = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    this.releve = releve;
    this.gravite = gravite;
    this.acquittee = false;
    this.dateCreation = LocalDateTime.now();
    this.dateAcquittement = null;
}
    

    public void acquitter() {
        this.acquittee = true;
        this.dateAcquittement = LocalDateTime.now();
        //System.out.println("✓ Alerte " + idAlerte + " acquittée");
    }

    public void supprimer() {
        System.out.println("Alerte supprimée avec succès.");
    }
    
    public boolean estActive() {
    return !this.acquittee;
}
public String obtenirResume() {
    String statut = acquittee ? "Acquittée" : "Active";
    return "Alerte #" + idAlerte + " (" + statut + ") - Gravité: " + gravite + 
           " - Capteur " + releve.getCapteur().getCode() + 
           " en zone " + releve.getCapteur().getZone().getNom();
}

   /*  public void afficher() {
        System.out.println("=== Alerte ===");
        System.out.println("Gravité: " + this.gravite);
        System.out.println("Acquittée: " + (this.acquittee ? "Oui" : "Non"));
        System.out.println("--- Informations du relevé ---");
        this.releve.afficher();
    }*/
    public void afficher() {
    System.out.println("\n========== ALERTE #" + idAlerte + " ==========");
    System.out.println("Statut: " + (acquittee ? "ACQUITTÉE" : "ACTIVE"));
    System.out.println("Gravité: " + gravite);
    System.out.println("Créée le: " + dateCreation);
    if (acquittee) {
        System.out.println("Acquittée le: " + dateAcquittement);
    }
    System.out.println("\n--- Détails du relevé ---");
    this.releve.afficher();
    System.out.println("=====================================\n");
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

public String getIdAlerte() {
    return idAlerte;
}

public LocalDateTime getDateCreation() {
    return dateCreation;
}

public LocalDateTime getDateAcquittement() {
    return dateAcquittement;
}
}