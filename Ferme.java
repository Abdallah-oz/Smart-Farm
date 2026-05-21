package TP;

import java.util.ArrayList;
import java.util.List;


public class Ferme {
   

    
    private String nom;
    private List<Zone> zones;

    /** Gestionnaire centralisé des alertes pour la ferme. */
    private GestionnaireAlerte gestionnaireAlerte;

    public Ferme(String nom) {
        this.nom = nom;
        this.zones = new ArrayList<>();
        this.gestionnaireAlerte = new GestionnaireAlerte();
    }

    
     
    public void ajouterZone(Zone zone) {
        if (zone != null) {
            this.zones.add(zone);
        }
    }

    /**
     * Déclenche une mesure sur un capteur de mesure spécifique et enregistre 
     * automatiquement l'alerte générée si le seuil est franchi.
     */
    public Alerte enregistrerMesure(String codeCapteur, double valeur) {
        
        for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
            if (c.getCode().equals(codeCapteur)) {
                if (c instanceof CapteurMesure) {
                    CapteurMesure cm = (CapteurMesure) c;
                    Alerte alerte = cm.envoyerMesure(valeur);
                    
                
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
     * Parcourt l'ensemble des capteurs pour faire un relevé général,
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

    

    /**
     * Active un capteur spécifique par son code.
     */
    public void activerCapteur(String codeCapteur) {
        for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
            if (c.getCode().equals(codeCapteur)) {
                c.activer();
                System.out.println(" Capteur " + codeCapteur + " activé.");
                return;
            }
        }
        System.out.println("Erreur: Aucun capteur trouvé avec le code " + codeCapteur);
    }

    /**
     * Désactive un capteur spécifique par son code.
     */
    public void deactiverCapteur(String codeCapteur) {
        for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
            if (c.getCode().equals(codeCapteur)) {
                c.desactiver();
                System.out.println(" Capteur " + codeCapteur + " désactivé.");
                return;
            }
        }
        System.out.println("Erreur: Aucun capteur trouvé avec le code " + codeCapteur);
    }

    /**
     * Active une zone et tous les capteurs liés à cette zone.
     */
    public void activerZone(String codeZone) {
        Zone zoneToActivate = null;
        
        // Trouve la zone à activer
        for (Zone z : zones) {
            if (z.getCode().equals(codeZone)) {
                zoneToActivate = z;
                break;
            }
        }
        
        if (zoneToActivate != null) {
            zoneToActivate.changerStatut(Statut.ACTIVE);
            System.out.println(" Zone " + codeZone + " activée.");
            
            // Active tous les capteurs de cette zone
            for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
                if (c.getZone().getCode().equals(codeZone)) {
                    c.activer();
                }
            }
            System.out.println(" Tous les capteurs de la zone " + codeZone + " ont été activés.");
        } else {
            System.out.println("Erreur: Aucune zone trouvée avec le code " + codeZone);
        }
    }

    /**
     * Désactive une zone et tous les capteurs liés à cette zone.
     */
    public void deactiverZone(String codeZone) {
        Zone zoneToDeactivate = null;
        
        // Trouve la zone à désactiver
        for (Zone z : zones) {
            if (z.getCode().equals(codeZone)) {
                zoneToDeactivate = z;
                break;
            }
        }
        
        if (zoneToDeactivate != null) {
            zoneToDeactivate.changerStatut(Statut.SUSPENDUE);
            System.out.println(" Zone " + codeZone + " désactivée.");
            
            // Désactive tous les capteurs de cette zone
            for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
                if (c.getZone().getCode().equals(codeZone)) {
                    c.desactiver();
                }
            }
            System.out.println(" Zone " + codeZone + " désactivée.");
        } else {
            System.out.println("Erreur: Aucune zone trouvée avec le code " + codeZone);
        }
    }

   

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
