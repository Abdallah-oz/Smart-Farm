package TP;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe coordinatrice principale représentant la ferme intelligente.
 * Elle centralise la gestion des zones et automatise l'enregistrement des alertes
 * générées par les différents capteurs de la ferme (via le GestionnaireCapteurs).
 */
public class Ferme {
    // Nom significatif de la ferme
    private String nom;
    // Liste des zones de la ferme (culture, élevage, aquacole)
    private List<Zone> zones;
    // Gestionnaire centralisé pour l'historique et le traitement de toutes les alertes de la ferme
    private GestionnaireAlerte gestionnaireAlerte;

    /**
     * Constructeur pour initialiser la ferme avec un nom.
     * @param nom Le nom de la ferme
     */
    public Ferme(String nom) {
        this.nom = nom;
        this.zones = new ArrayList<>();
        this.gestionnaireAlerte = new GestionnaireAlerte();
    }

    /**
     * Ajoute une nouvelle zone à la ferme.
     * @param zone La zone à ajouter
     */
    public void ajouterZone(Zone zone) {
        if (zone != null) {
            this.zones.add(zone);
        }
    }

    /**
     * Déclenche une mesure sur un capteur de mesure spécifique et enregistre 
     * automatiquement l'alerte générée si le seuil est franchi.
     * @param codeCapteur Le code unique du capteur de mesure
     * @param valeur La nouvelle valeur mesurée à envoyer
     * @return L'alerte générée en cas d'anomalie, ou null si tout est normal
     */
    public Alerte enregistrerMesure(String codeCapteur, double valeur) {
        // Recherche du capteur dans la liste globale gérée par le binôme
        for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
            if (c.getCode().equals(codeCapteur)) {
                if (c instanceof CapteurMesure) {
                    CapteurMesure cm = (CapteurMesure) c;
                    Alerte alerte = cm.envoyerMesure(valeur);
                    
                    // Si le seuil est franchi et qu'une alerte est créée, on l'enregistre automatiquement
                    if (alerte != null) {
                        this.gestionnaireAlerte.ajouterAlerte(alerte);
                    }
                    return alerte;
                }
            }
        }
        System.out.println("Erreur: Aucun capteur de mesure trouvé avec le code " + codeCapteur);
        return null;
    }

    /**
     * Met à jour la position géographique d'un capteur GPS spécifique et enregistre
     * automatiquement l'alerte générée si l'animal est hors de sa zone autorisée.
     * @param codeCapteur Le code unique du capteur GPS
     * @param latitude La nouvelle latitude
     * @param longitude La nouvelle longitude
     * @return L'alerte générée si l'animal sort de la zone, ou null si tout est correct
     */
    public Alerte enregistrerPosition(String codeCapteur, double latitude, double longitude) {
        // Recherche du capteur dans la liste globale
        for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
            if (c.getCode().equals(codeCapteur)) {
                if (c instanceof CapteurGPS) {
                    CapteurGPS cgps = (CapteurGPS) c;
                    cgps.setLatitude(latitude);
                    cgps.setLongitude(longitude);
                    Alerte alerte = cgps.envoyerPosition();
                    
                    // Si l'animal a fui sa zone, l'alerte est automatiquement enregistrée
                    if (alerte != null) {
                        this.gestionnaireAlerte.ajouterAlerte(alerte);
                    }
                    return alerte;
                }
            }
        }
        System.out.println("Erreur: Aucun capteur GPS trouvé avec le code " + codeCapteur);
        return null;
    }

    /**
     * Déclenche un relevé générique sur n'importe quel capteur et enregistre
     * automatiquement l'alerte si une anomalie est détectée.
     * @param codeCapteur Le code unique du capteur à interroger
     * @return L'alerte générée, ou null si tout est en ordre
     */
    public Alerte enregistrerReleve(String codeCapteur) {
        for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
            if (c.getCode().equals(codeCapteur)) {
                Alerte alerte = c.envoyerReleve();
                
                // Enregistrement automatique de l'alerte
                if (alerte != null) {
                    this.gestionnaireAlerte.ajouterAlerte(alerte);
                }
                return alerte;
            }
        }
        System.out.println("Erreur: Aucun capteur trouvé avec le code " + codeCapteur);
        return null;
    }

    /**
     * Parcourt l'ensemble des capteurs de la ferme intelligente pour faire un relevé général,
     * et enregistre automatiquement toutes les alertes de dépassement de seuil détectées.
     */
    public void actualiserFerme() {
        for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
            Alerte alerte = c.envoyerReleve();
            if (alerte != null) {
                this.gestionnaireAlerte.ajouterAlerte(alerte);
            }
        }
    }

    // ==========================================
    //            GETTERS ET SETTERS
    // ==========================================

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public GestionnaireAlerte getGestionnaireAlerte() {
        return gestionnaireAlerte;
    }
}
