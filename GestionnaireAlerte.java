package TP;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;



public class GestionnaireAlerte {
    
    private List<Alerte> alertes;
    // Compteur cumulatif du nombre total alertes
    private int compteurAlertes;

    
     //Constructeur
     
    public GestionnaireAlerte() {
        this.alertes = new ArrayList<>();
        this.compteurAlertes = 0;
    }

    /**/
     // Enregistre une nouvelle alerte dans le gestionnaire et incrémente le compteur.
    
    public void ajouterAlerte(Alerte alerte) {
        this.alertes.add(alerte);
        this.compteurAlertes++;
    }

    /**/
     // Supprime définitivement toutes les alertes enregistrées
     
    public void supprimerToutesAlertes() {
        alertes.clear();
        System.out.println("Toutes les alertes ont été supprimées.");
    }

   
     
    public Alerte obtenirAlerteParId(String idAlerte) {
        for (Alerte a : alertes) {
            if (a.getIdAlerte().equals(idAlerte)) {
                return a;
            }
        }
        return null;
    }

    
     // Recupere la liste de toutes les alertes liées a une zone.
     
     
    public List<Alerte> obtenirAlerteParZone(String codeZone) {
        List<Alerte> result = new ArrayList<>();
        for (Alerte a : alertes) {
            if (a.getReleve().getCapteur().getZone().getCode().equals(codeZone)) {
                result.add(a);
            }
        }
        return result;
    }

    // ==========================================
    //          FILTRAGE PAR GRAVITÉ
    // ==========================================

    /**/
     // Recupere toutes les alertes de niveau critique.

     
    public List<Alerte> obtenirAlertesCritiques() {
        List<Alerte> result = new ArrayList<>();
        for (Alerte a : alertes) {
            if (a.getGravite() == Gravite.CRITIQUE) {
                result.add(a);
            }
        }
        return result;
    }

    /**/
     // Récupère uniquement les alertes critiques qui sont encore actives.
     
     
    public List<Alerte> obtenirAlertesCritiquesActives() {
        List<Alerte> result = new ArrayList<>();
        for (Alerte a : alertes) {
            if (a.getGravite() == Gravite.CRITIQUE && a.estActive()) {
                result.add(a);
            }
        }
        return result;
    }

    /**/
     // Récupère toutes les alertes de niveau avertissement.
    
     
    public List<Alerte> obtenirAvertissements() {
        List<Alerte> result = new ArrayList<>();
        for (Alerte a : alertes) {
            if (a.getGravite() == Gravite.AVERTISSEMENT) {
                result.add(a);
            }
        }
        return result;
    }

    /**/
     // Récupère uniquement les avertissements qui sont encore actifs (non acquittés).
     
     
    public List<Alerte> obtenirAvertissementsActifs() {
        List<Alerte> result = new ArrayList<>();
        for (Alerte a : alertes) {
            if (a.getGravite() == Gravite.AVERTISSEMENT && a.estActive()) {
                result.add(a);
            }
        }
        return result;
    }

    // ==========================================
    //           FILTRAGE PAR STATUT
    // ==========================================

    
     // Récupère toutes les alertes actives triées.
      
     
    public List<Alerte> obtenirAlertesActives() {
        List<Alerte> result = new ArrayList<>();
        for (Alerte a : alertes) {
            if (a.estActive()) {
                result.add(a);
            }
        }
        // Tri en utilisant le comparateur AlerteComparator
        result.sort(new AlerteComparator());
        return result;
    }

    /**/
     //Récupère la liste de toutes les alertes qui ont été acquittées.
     
    
    public List<Alerte> obtenirAlertesAcquittees() {
        List<Alerte> result = new ArrayList<>();
        for (Alerte a : alertes) {
            if (!a.estActive()) {
                result.add(a);
            }
        }
        return result;
    }

    // ==========================================
    //               STATISTIQUES
    // ==========================================

    /**
     * Retourne le nombre total d'alertes présentes dans l'historique du gestionnaire.
     */
    public int compterTotalAlertes() {
        return alertes.size();
    }

    /**
     * Compte le nombre d'alertes qui sont actuellement actives.
     */
    public int compterAlertesActives() {
        int count = 0;
        for (Alerte a : alertes) {
            if (a.estActive()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Compte le nombre d'alertes qui ont été acquittées.
     */
    public int compterAlertesAcquittees() {
        int count = 0;
        for (Alerte a : alertes) {
            if (!a.estActive()) {
                count++;
            }
        }
        return count;
    }

    /**/
     // Compte le nombre total d'alertes de niveau critique.
     
    public int compterAlertesCritiques() {
        int count = 0;
        for (Alerte a : alertes) {
            if (a.getGravite() == Gravite.CRITIQUE) {
                count++;
            }
        }
        return count;
    }

    /**/
      //Compte le nombre total d'alertes de niveau avertissement
     
    public int compterAvertissements() {
        int count = 0;
        for (Alerte a : alertes) {
            if (a.getGravite() == Gravite.AVERTISSEMENT) {
                count++;
            }
        }
        return count;
    }

    // ==========================================
    //            AFFICHAGES CONSOLE
    // ==========================================

    
    public void afficherPanneauAlertes() {
        System.out.println("\n========================================");
        System.out.println("      PANNEAU DE CONTRÔLE ALERTES       ");
        System.out.println("========================================");

        if (alertes.isEmpty()) {
            System.out.println("Aucune alerte en cours / Tous les capteurs sont OK.");
            return;
        }

        List<Alerte> actives = obtenirAlertesActives();
        if (!actives.isEmpty()) {
            System.out.println("\n[!] ALERTES ACTIVES (" + actives.size() + ") :");
            for (Alerte a : actives) {
                System.out.println("  - " + a.obtenirResume());
            }
        }

        List<Alerte> acquittees = obtenirAlertesAcquittees();
        if (!acquittees.isEmpty()) {
            System.out.println("\n[OK] ALERTES ACQUITTEES (" + acquittees.size() + ") :");
            for (Alerte a : acquittees) {
                System.out.println("  - " + a.obtenirResume());
            }
        }
        System.out.println("========================================");
    }

    /**/
     
     
    public void afficherHistoriqueAlertes() {
        System.out.println("\n========================================");
        System.out.println("     HISTORIQUE COMPLET DES ALERTES     ");
        System.out.println("========================================");

        if (alertes.isEmpty()) {
            System.out.println("Aucune alerte dans l'historique.");
            return;
        }

        for (Alerte a : alertes) {
            System.out.println(a.obtenirResume());
        }
        System.out.println("========================================");
    }

    /**
     * Affiche alertes critiques actives en details 
     */
    public void afficherAlertesCritiques() {
        List<Alerte> critiques = obtenirAlertesCritiquesActives();
        System.out.println("\n========================================");
        System.out.println("       ALERTES CRITIQUES ACTIVES        ");
        System.out.println("========================================");

        if (critiques.isEmpty()) {
            System.out.println("Aucune alerte critique active.");
            return;
        }

        System.out.println("Nombre d'alertes critiques : " + critiques.size() + "\n");
        for (Alerte a : critiques) {
            a.afficher();
        }
        System.out.println("========================================");
    }

    /**
     * Affiche alertes detat avertissement en details
     */
    public void afficherAvertissements() {
        List<Alerte> avertissements = obtenirAvertissementsActifs();
        System.out.println("\n========================================");
        System.out.println("          AVERTISSEMENTS ACTIFS         ");
        System.out.println("========================================");

        if (avertissements.isEmpty()) {
            System.out.println("Aucun avertissement actif.");
            return;
        }

        System.out.println("Nombre d'avertissements : " + avertissements.size() + "\n");
        for (Alerte a : avertissements) {
            a.afficher();
        }
        System.out.println("========================================");
    }

    /**/
     // Retourne toutes les alertes triées par gravité .
     
    
    public List<Alerte> obtenirAlertesTrieesParGravite() {
        List<Alerte> result = new ArrayList<>(alertes);
        result.sort(new AlerteComparator());
        return result;
    }

    /**
     * Affiche toutes les alertes triées par gravité.
     */
    public void afficherAlertesTrieesParGravite() {
        List<Alerte> tri = obtenirAlertesTrieesParGravite();
        System.out.println("\n========================================");
        System.out.println("    ALERTES TRIEES PAR GRAVITE    ");
        System.out.println("========================================");

        if (tri.isEmpty()) {
            System.out.println("Aucune alerte dans le gestionnaire.");
            return;
        }

        for (Alerte a : tri) {
            System.out.println(a.obtenirResume());
        }
        System.out.println("========================================");
    }

    /**
     * Affiche toutes les alertes lies a  une zone.
     
     */
    public void afficherAlerteParZone(String codeZone) {
        List<Alerte> alertesZone = obtenirAlerteParZone(codeZone);
        System.out.println("\n========================================");
        System.out.println("       ALERTES DE LA ZONE : " + codeZone);
        System.out.println("========================================");

        if (alertesZone.isEmpty()) {
            System.out.println("Aucune alerte pour cette zone.");
            return;
        }

        System.out.println("Nombre d'alertes : " + alertesZone.size() + "\n");
        for (Alerte a : alertesZone) {
            System.out.println(a.obtenirResume());
        }
        System.out.println("========================================");
    }

   

    
     //Retourne une copie de la liste complète des alertes.
     
    
    public List<Alerte> getToutesAlertes() {
        return new ArrayList<>(alertes);
    }

    /**
     * Retourne le nombre total d'alertes enregistrées.
     */
    public int getCompteurAlertes() {
        return compteurAlertes;
    }
}

/* */
 
class AlerteComparator implements Comparator<Alerte> {
    @Override
    public int compare(Alerte a1, Alerte a2) {
        if (a1.getGravite() == a2.getGravite()) {
            
            return a1.getDateCreation().compareTo(a2.getDateCreation());
        } else if (a1.getGravite() == Gravite.CRITIQUE) {
            
            return -1;
        }
    
        return 1;
    }
}
