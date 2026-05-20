  /*   package TP;

    import java.time.LocalDateTime;

    public class Main {
        public static void main(String[] args) {
            
            // ==========================================
            System.out.println("==========================================");
            System.out.println("1. CREATION DES ZONES");
            System.out.println("==========================================");
            
            ZoneCulture zoneCulture = new ZoneCulture("ZC01", "Zone Culture Blé");
            ZoneElevage zoneElevage = new ZoneElevage("ZE01", "Zone Elevage Vaches", TypeElevage.RUMINANT);
            ZoneAquacole zoneAquacole = new ZoneAquacole("ZA01", "Zone Aquacole Poissons", "Poisson");
            
            System.out.println("Zone Culture créée: " + zoneCulture.getNom());
            System.out.println("Zone Elevage créée: " + zoneElevage.getNom());
            System.out.println("Zone Aquacole créée: " + zoneAquacole.getNom());
            
            // ==========================================
            System.out.println("\n==========================================");
            System.out.println("2. GESTION DES CULTURES");
            System.out.println("==========================================");
            
            // Créer 2 cultures
            Culture culture1 = new Culture(FamilleCulture.CEREALE, 
                                        LocalDateTime.of(2025, 3, 15, 10, 0),
                                        LocalDateTime.of(2025, 8, 30, 10, 0),
                                        7.0);
            
            Culture culture2 = new Culture(FamilleCulture.LEGUME,
                                        LocalDateTime.of(2025, 4, 1, 10, 0),
                                        LocalDateTime.of(2025, 7, 15, 10, 0),
                                        6.5);
            
            // Ajouter les cultures à la zone
            zoneCulture.ajouterCulture(culture1);
            zoneCulture.ajouterCulture(culture2);
            
            System.out.println("Cultures ajoutées à la zone");
            
            // Mettre à jour le stade
            culture1.mettreAJourStade(StadeCroissance.CROISSANCE);
            culture2.mettreAJourStade(StadeCroissance.CROISSANCE);
            System.out.println("Stade des cultures mis à jour: CROISSANCE");
            
            // Enregistrer le rendement (d'abord passer à RECOLTE)
            culture1.mettreAJourStade(StadeCroissance.RECOLTE);
            culture1.enregistrerRendement(950.0);
            System.out.println("Rendement enregistré pour culture 1: 950.0");
            
            // Afficher les rapports
            zoneCulture.calculerRendement();
            zoneCulture.rapportCulture();
            
            System.out.println("\n--- Vue d'ensemble Zone Culture ---");
            zoneCulture.afficherVueEnsemble();
            
            // ==========================================
            System.out.println("\n==========================================");
            System.out.println("3. GESTION DES ANIMAUX");
            System.out.println("==========================================");
            
            // Créer 2 Ruminants et 1 Volaille
            Ruminant ruminant1 = new Ruminant("R001", "Bessie", "Vache Holstein", 5, 600.0, EtatSante.SAIN);
            Ruminant ruminant2 = new Ruminant("R002", "Daisy", "Vache Jersey", 4, 450.0, EtatSante.SAIN);
            Volaille volaille1 = new Volaille("V001", "Chickpea", "Poule Rousse", 2, 2.5, EtatSante.SAIN);
            
            // Ajouter à la zone
            zoneElevage.ajouterAnimal(ruminant1);
            zoneElevage.ajouterAnimal(ruminant2);
            zoneElevage.ajouterAnimal(volaille1);
            
            System.out.println("Animaux ajoutés à la zone");
            
            // Enregistrer les productions
            ruminant1.enregistrerProdLait(25.5);
            ruminant2.enregistrerProdLait(18.3);
            volaille1.enregistrerProdOeufs(280);
            
            System.out.println("Productions enregistrées");
            
            // Afficher chaque animal
            System.out.println("\n--- Détails des animaux ---");
            ruminant1.afficher();
            System.out.println();
            ruminant2.afficher();
            System.out.println();
            volaille1.afficher();
            
            System.out.println("\n--- Vue d'ensemble Zone Elevage ---");
            zoneElevage.afficherVueEnsemble();
            
            // ==========================================
            System.out.println("\n==========================================");
            System.out.println("4. PROGRAMME D'ALIMENTATION");
            System.out.println("==========================================");
            
            ProgrammeAlimentation programme = new ProgrammeAlimentation(zoneElevage, "Foin", 50.0);
            zoneElevage.definirProgramme(programme);
            
            System.out.println("Programme d'alimentation assigné");
            zoneElevage.afficherProgramme();
            
            // ==========================================
            System.out.println("\n==========================================");
            System.out.println("5. ZONE AQUACOLE");
            System.out.println("==========================================");
            
            zoneAquacole.affecterPoidRecolte(500.0);
            System.out.println("Poids de récolte défini: 500.0 kg");
            zoneAquacole.afficherVueEnsemble();
            
            // ==========================================
            System.out.println("\n==========================================");
            System.out.println("6. CREATION DES CAPTEURS");
            System.out.println("==========================================");
            
            // Capteur de mesure pour pH du sol
            CapteurMesure capteurMesure = new CapteurMesure("CM01", zoneCulture, 6.0, 7.5, "pH", TypeMesure.PH_SOL);
            
            // Capteur GPS pour zone d'élevage
            CapteurGPS capteurGPS = new CapteurGPS("GPS01", zoneElevage, 0.0, 100.0);
            capteurGPS.setLatitude(45.5017);
            capteurGPS.setLongitude(2.5556);
            
            System.out.println("Capteur de mesure créé: " + capteurMesure.getCode());
            System.out.println("Capteur GPS créé: " + capteurGPS.getCode());
            
            // ==========================================
            System.out.println("\n==========================================");
            System.out.println("7. RELEVES ET ALERTES");
            System.out.println("==========================================");
            
            // Relevé normal (dans les seuils)
            System.out.println("\n--- Relevé Normal (valeur=7.0) ---");
            Alerte alerte1 = capteurMesure.envoyerMesure(7.0);
            if (alerte1 == null) {
                ReleveMesure releve1 = new ReleveMesure(7.0, capteurMesure);
                releve1.afficher();
                System.out.println("✓ Relevé valide - Aucune alerte");
            } else {
                alerte1.afficher();
            }
            
            // Relevé anormal (hors limites)
            System.out.println("\n--- Relevé Anormal (valeur=8.5) ---");
            Alerte alerte2 = capteurMesure.envoyerMesure(8.5);
            if (alerte2 == null) {
                ReleveMesure releve2 = new ReleveMesure(8.5, capteurMesure);
                releve2.afficher();
                System.out.println(" Relevé valide - Aucune alerte");
            } else {
                alerte2.afficher();
                System.out.println("Gravité: " + alerte2.getGravite());
                System.out.println("\n--- Acquittement de l'alerte ---");
                alerte2.acquitter();
                alerte2.afficher();
            }
            
            // Relevé GPS
            System.out.println("\n--- Relevé GPS ---");
            Alerte alertGPS = capteurGPS.envoyerPosition();
            if (alertGPS == null) {
                ReleveGPS releveGPS = new ReleveGPS(capteurGPS.getLatitude(), capteurGPS.getLongitude(), capteurGPS);
                releveGPS.afficher();
            } else {
                alertGPS.afficher();
            }
            
            System.out.println("\n--- Position du capteur GPS ---");
            capteurGPS.afficher();
            
            // ==========================================
            System.out.println("\n==========================================");
            System.out.println("FIN DES TESTS");
            System.out.println("==========================================");
        }
    }*/
