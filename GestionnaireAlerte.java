import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import TP.Alerte;
import TP.Gravite;

/**
 * Gestionnaire qui centralise et traite l'ensemble des alertes de la ferme intelligente.
 * Permet d'ajouter, filtrer, compter et afficher les alertes selon leur gravité ou leur statut.
 */
public class GestionnaireAlerte {
    // Liste stockant toutes les alertes reçues par le système
    private List<Alerte> alertes;
    // Compteur cumulatif du nombre total d'alertes générées depuis le démarrage
    private int compteurAlertes;

    /**
     * Constructeur par défaut. Initialise la liste vide et le compteur à zéro.
     */
    public GestionnaireAlerte() {
        this.alertes = new ArrayList<>();
        this.compteurAlertes = 0;
    }

    /**
     * Enregistre une nouvelle alerte dans le gestionnaire et incrémente le compteur.
     * @param alerte L'alerte à ajouter
     */
    public void ajouterAlerte(Alerte alerte) {
        this.alertes.add(alerte);
        this.compteurAlertes++;
    }

    /**
     * Supprime définitivement toutes les alertes enregistrées et affiche une confirmation.
     */
    public void supprimerToutesAlertes() {
        alertes.clear();
        System.out.println("Toutes les alertes ont été supprimées.");
    }

    /**
     * Recherche une alerte spécifique grâce à son identifiant unique.
     * @param idAlerte L'identifiant de l'alerte recherchée
     * @return L'alerte correspondante, ou null si aucune alerte ne possède cet ID
     */
    public Alerte obtenirAlerteParId(String idAlerte) {
        for (Alerte a : alertes) {
            if (a.getIdAlerte().equals(idAlerte)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Récupère la liste de toutes les alertes liées à une zone géographique particulière.
     * @param codeZone Le code de la zone à filtrer
     * @return Une liste d'alertes concernant cette zone
     */
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

    /**
     * Récupère toutes les alertes de niveau critique, qu'elles soient actives ou acquittées.
     * @return Liste des alertes critiques
     */
    public List<Alerte> obtenirAlertesCritiques() {
        List<Alerte> result = new ArrayList<>();
        for (Alerte a : alertes) {
            if (a.getGravite() == Gravite.CRITIQUE) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Récupère uniquement les alertes critiques qui sont encore actives (non acquittées).
     * @return Liste des alertes critiques actives
     */
    public List<Alerte> obtenirAlertesCritiquesActives() {
        List<Alerte> result = new ArrayList<>();
        for (Alerte a : alertes) {
            if (a.getGravite() == Gravite.CRITIQUE && a.estActive()) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Récupère toutes les alertes de niveau avertissement (warning), actives ou acquittées.
     * @return Liste des avertissements
     */
    public List<Alerte> obtenirAvertissements() {
        List<Alerte> result = new ArrayList<>();
        for (Alerte a : alertes) {
            if (a.getGravite() == Gravite.AVERTISSEMENT) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Récupère uniquement les avertissements qui sont encore actifs (non acquittés).
     * @return Liste des avertissements actifs
     */
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

    /**
     * Récupère toutes les alertes actives en les triant en utilisant le comparateur d'alertes personnalisé.
     * @return Liste triée des alertes actives
     */
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

    /**
     * Récupère la liste de toutes les alertes qui ont été résolues ou acquittées.
     * @return Liste des alertes acquittées
     */
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
     * Compte le nombre d'alertes qui sont actuellement en cours (non acquittées).
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
     * Compte le nombre d'alertes qui ont été traitées et acquittées.
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

    /**
     * Compte le nombre total d'alertes de niveau critique (actives et résolues).
     */
    public int compterAlertesCritiques() {
        int count = 0;
        for (Alerte a : alertes) {
            if (a.getGravite() == Gravite.CRITIQUE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Compte le nombre total d'alertes de niveau avertissement (actives et résolues).
     */
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

    /**
     * Affiche un tableau synthétique avec la liste des alertes actives d'un côté,
     * et les alertes résolues (acquittées) de l'autre.
     */
    public void afficherPanneauAlertes() {
        System.out.println("\n========================================");
        System.out.println("      PANNEAU DE CONTRÔLE ALERTES       ");
        System.out.println("========================================");

        if (alertes.isEmpty()) {
            System.out.println("Aucune alerte en cours - Tous les capteurs sont OK.");
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

    /**
     * Affiche la liste brute et complète de toutes les alertes du système par ordre chronologique.
     */
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
     * Affiche les détails complets de toutes les alertes de niveau critique actuellement actives.
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
     * Affiche les détails complets de tous les avertissements actuellement actifs.
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

    /**
     * Affiche le résumé de toutes les alertes qui concernent une zone spécifique.
     * @param codeZone Le code de la zone ciblée
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

    // ==========================================
    //                 GETTERS
    // ==========================================

    /**
     * Retourne une copie de la liste complète des alertes.
     * @return Copie de la liste des alertes
     */
    public List<Alerte> getToutesAlertes() {
        return new ArrayList<>(alertes);
    }

    /**
     * Retourne le nombre total d'alertes enregistrées depuis l'initialisation.
     */
    public int getCompteurAlertes() {
        return compteurAlertes;
    }
}

/**
 * Comparateur personnalisé d'alertes.
 * Permet de trier les alertes par gravité (CRITIQUE d'abord).
 * En cas d'égalité de gravité, les alertes sont triées par date de création (les plus anciennes d'abord).
 */
class AlerteComparator implements Comparator<Alerte> {
    @Override
    public int compare(Alerte a1, Alerte a2) {
        if (a1.getGravite() == a2.getGravite()) {
            // Même gravité : on trie par date de création (la plus ancienne d'abord)
            return a1.getDateCreation().compareTo(a2.getDateCreation());
        } else if (a1.getGravite() == Gravite.CRITIQUE) {
            // a1 est plus critique : il doit venir avant a2 (valeur négative)
            return -1;
        }
        // a2 est plus critique : il doit venir avant a1 (valeur positive)
        return 1;
    }
}
