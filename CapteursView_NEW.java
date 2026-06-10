package TP;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Vue JavaFX pour la gestion des Capteurs.
 * 
 * Cette vue respecte la logique métier suivante :
 * - Les capteurs sont chargés au démarrage depuis un CSV et stockés dans GestionnaireCapteurs
 * - Chaque capteur est affiché avec son type (Mesure ou GPS), sa zone, son statut et ses seuils
 * - L'envoi de relevé crée un Releve via envoyerMesure() ou envoyerPosition()
 * - Le relevé appelle verifierSeuil() ; si AlerteException, une Alerte est créée et persiste dans alertes statique
 * - Les alertes sont accessibles par AlertesView via getAlertes()
 * - Après chaque modification (activation, désactivation, ajout), sauvegarde dans CSV
 */
public class CapteursView {

    // Liste statique d'alertes partagée avec AlertesView
    private static final List<Alerte> alertes = new ArrayList<>();

    public static BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Surveillance des Capteurs");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox topBar = new HBox(10, title);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #f4f4f4;");

        // Charger les capteurs au démarrage (lazy loading)
        chargerCapteursDepuisGestionnaire();

        // ========== ZONE GAUCHE : LISTE DES CAPTEURS ==========
        ListView<String> capteurList = new ListView<>();
        capteurList.setPrefWidth(260);
        Runnable rebuildList = () -> {
            List<String> items = new ArrayList<>();
            for (Capteur c : GestionnaireCapteurs.getCapteurs()) {
                String type = (c instanceof CapteurMesure) ? "📊 Mesure" : "📍 GPS";
                items.add(type + " - " + c.getCode() + " (" + c.getStatut() + ")");
            }
            capteurList.setItems(FXCollections.observableArrayList(items));
        };
        rebuildList.run();

        Label totalLabel = new Label("Total capteurs: " + GestionnaireCapteurs.getCapteurs().size());
        totalLabel.setStyle("-fx-font-weight: bold;");

        VBox leftPane = new VBox(12, capteurList, totalLabel);
        leftPane.setPadding(new Insets(10));
        leftPane.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: #ffffff;");

        // ========== ZONE CENTRALE : DÉTAILS + ACTIONS ==========
        Label capteurCodeLabel = new Label("Capteur : -");
        Label typeLabel = new Label("Type : -");
        Label zoneLabel = new Label("Zone : -");
        Label statutLabel = new Label("Statut : -");
        Label seuilLabel = new Label("Seuil : -");
        Label valeurActuelleLabel = new Label("Valeur actuelle : -");

        // Rafraîchir les détails quand un capteur est sélectionné
        Runnable refreshDetails = () -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= GestionnaireCapteurs.getCapteurs().size()) {
                capteurCodeLabel.setText("Capteur : -");
                typeLabel.setText("Type : -");
                zoneLabel.setText("Zone : -");
                statutLabel.setText("Statut : -");
                seuilLabel.setText("Seuil : -");
                valeurActuelleLabel.setText("Valeur actuelle : -");
                return;
            }

            Capteur sel = GestionnaireCapteurs.getCapteurs().get(idx);
            capteurCodeLabel.setText("Capteur : " + sel.getCode());
            zoneLabel.setText("Zone : " + (sel.getZone() != null ? sel.getZone().getNom() : "-"));
            statutLabel.setText("Statut : " + sel.getStatut());

            if (sel instanceof CapteurMesure) {
                CapteurMesure cm = (CapteurMesure) sel;
                typeLabel.setText("Type : Mesure (" + cm.getType() + ")");
                seuilLabel.setText("Seuil : " + cm.getSeuilMin() + " - " + cm.getSeuilMax() + " " + cm.getUnite());
                valeurActuelleLabel.setText("Valeur actuelle : " + cm.getValeur() + " " + cm.getUnite());
            } else if (sel instanceof CapteurGPS) {
                CapteurGPS cg = (CapteurGPS) sel;
                typeLabel.setText("Type : GPS");
                seuilLabel.setText("Rayon max : " + cg.getSeuilMax() + "m");
                valeurActuelleLabel.setText("Position : " + cg.getLatitude() + ", " + cg.getLongitude());
            }
        };

        capteurList.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> refreshDetails.run());

        // ========== BOUTONS D'ACTION ==========
        Button activerButton = new Button("✅ Activer");
        Button desactiverButton = new Button("❌ Désactiver");
        Button envoyerReleve = new Button("📤 Envoyer Relevé");
        Button ajouterButton = new Button("➕ Ajouter Capteur");
        Button supprimerButton = new Button("🗑️ Supprimer");

        HBox actionButtons = new HBox(10, activerButton, desactiverButton, envoyerReleve, ajouterButton, supprimerButton);
        actionButtons.setPadding(new Insets(8));

        // **ACTION : Activer un capteur**
        activerButton.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= GestionnaireCapteurs.getCapteurs().size()) {
                showAlert("Erreur", "Sélectionnez un capteur.");
                return;
            }
            Capteur sel = GestionnaireCapteurs.getCapteurs().get(idx);
            sel.activer();
            sauvegarderCapteursCSV("TP/csv/capteurs.csv", GestionnaireCapteurs.getCapteurs());
            rebuildList.run();
            refreshDetails.run();
        });

        // **ACTION : Désactiver un capteur**
        desactiverButton.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= GestionnaireCapteurs.getCapteurs().size()) {
                showAlert("Erreur", "Sélectionnez un capteur.");
                return;
            }
            Capteur sel = GestionnaireCapteurs.getCapteurs().get(idx);
            sel.desactiver();
            sauvegarderCapteursCSV("TP/csv/capteurs.csv", GestionnaireCapteurs.getCapteurs());
            rebuildList.run();
            refreshDetails.run();
        });

        // **ACTION : Envoyer un Relevé**
        // Cette action est le cœur de la logique métier :
        // 1. Pour CapteurMesure : demander une valeur, appeler envoyerMesure(valeur) → ReleveMesure
        // 2. Pour CapteurGPS : appeler envoyerPosition() → ReleveGPS
        // 3. Appeler releve.verifierSeuil() ; si AlerteException → créer Alerte et l'ajouter
        envoyerReleve.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= GestionnaireCapteurs.getCapteurs().size()) {
                showAlert("Erreur", "Sélectionnez un capteur.");
                return;
            }

            Capteur sel = GestionnaireCapteurs.getCapteurs().get(idx);

            // Vérifier que le capteur est actif
            if (sel.getStatut() != StatutCapteur.ACTIF) {
                showAlert("Erreur", "Le capteur n'est pas actif.");
                return;
            }

            // CAS 1 : CapteurMesure
            if (sel instanceof CapteurMesure) {
                CapteurMesure cm = (CapteurMesure) sel;

                // Demander la nouvelle valeur
                TextInputDialog dialog = new TextInputDialog(String.valueOf(cm.getValeur()));
                dialog.setTitle("Envoyer Relevé");
                dialog.setHeaderText("Entrez la valeur de mesure pour " + cm.getCode());
                dialog.setContentText("Valeur (" + cm.getUnite() + "):");
                Optional<String> result = dialog.showAndWait();

                result.ifPresent(valueStr -> {
                    try {
                        double value = Double.parseDouble(valueStr);
                        // Appeler envoyerMesure() : crée un ReleveMesure et l'enregistre
                        Releve releve = cm.envoyerMesure(value);

                        // Vérifier le seuil et créer une alerte si nécessaire
                        try {
                            releve.verifierSeuil();
                            showAlert("Succès", "Relevé enregistré. Valeur: " + value + " " + cm.getUnite());
                        } catch (AlerteException ex) {
                            // Une alerte est levée : créer un objet Alerte et l'ajouter
                            Alerte alerte = new Alerte(releve, ex.getGravite());
                            alertes.add(alerte);
                            showAlert("⚠️ Alerte détectée", "Gravité: " + ex.getGravite() + "\nMessage: " + ex.getMessage());
                        }

                        refreshDetails.run();
                    } catch (NumberFormatException ex) {
                        showAlert("Erreur", "Format numérique invalide.");
                    }
                });
            }
            // CAS 2 : CapteurGPS
            else if (sel instanceof CapteurGPS) {
                CapteurGPS cg = (CapteurGPS) sel;

                // Demander la latitude
                TextInputDialog latDialog = new TextInputDialog(String.valueOf(cg.getLatitude()));
                latDialog.setTitle("Envoyer Relevé GPS");
                latDialog.setHeaderText("Entrez la latitude pour " + cg.getCode());
                latDialog.setContentText("Latitude:");
                Optional<String> latResult = latDialog.showAndWait();

                latResult.ifPresent(latStr -> {
                    // Demander la longitude
                    TextInputDialog lonDialog = new TextInputDialog(String.valueOf(cg.getLongitude()));
                    lonDialog.setTitle("Envoyer Relevé GPS");
                    lonDialog.setHeaderText("Entrez la longitude pour " + cg.getCode());
                    lonDialog.setContentText("Longitude:");
                    Optional<String> lonResult = lonDialog.showAndWait();

                    lonResult.ifPresent(lonStr -> {
                        try {
                            double lat = Double.parseDouble(latStr);
                            double lon = Double.parseDouble(lonStr);

                            // Mettre à jour les coordonnées du capteur
                            cg.setLatitude(lat);
                            cg.setLongitude(lon);

                            // Appeler envoyerPosition() : crée un ReleveGPS et l'enregistre
                            Releve releve = cg.envoyerPosition();

                            // Vérifier le seuil et créer une alerte si nécessaire
                            try {
                                releve.verifierSeuil();
                                showAlert("Succès", "Relevé GPS enregistré.\nPosition: " + lat + ", " + lon);
                            } catch (AlerteException ex) {
                                // Une alerte est levée : créer un objet Alerte et l'ajouter
                                Alerte alerte = new Alerte(releve, ex.getGravite());
                                alertes.add(alerte);
                                showAlert("⚠️ Alerte détectée", "Gravité: " + ex.getGravite() + "\nMessage: " + ex.getMessage());
                            }

                            refreshDetails.run();
                        } catch (NumberFormatException ex) {
                            showAlert("Erreur", "Format numérique invalide.");
                        }
                    });
                });
            }
        });

        // **ACTION : Ajouter un nouveau capteur**
        ajouterButton.setOnAction(e -> {
            List<String> types = new ArrayList<>();
            types.add("Mesure");
            types.add("GPS");

            ChoiceDialog<String> typeDialog = new ChoiceDialog<>(types.get(0), types);
            typeDialog.setTitle("Ajouter Capteur");
            typeDialog.setHeaderText("Type de capteur");
            typeDialog.setContentText("Choisir:");
            Optional<String> typeChoice = typeDialog.showAndWait();

            typeChoice.ifPresent(typeStr -> {
                // Demander le code
                TextInputDialog codeDialog = new TextInputDialog();
                codeDialog.setTitle("Code Capteur");
                codeDialog.setHeaderText("Entrez le code du capteur");
                codeDialog.setContentText("Code:");
                Optional<String> codeOpt = codeDialog.showAndWait();

                codeOpt.ifPresent(code -> {
                    // Demander la zone
                    List<Zone> zones = ZonesView.getZones();
                    List<String> zoneOptions = new ArrayList<>();
                    for (Zone z : zones) {
                        zoneOptions.add(z.getCode() + " - " + z.getNom());
                    }

                    ChoiceDialog<String> zoneDialog = new ChoiceDialog<>(
                            zoneOptions.isEmpty() ? "" : zoneOptions.get(0), zoneOptions);
                    zoneDialog.setTitle("Associer Zone");
                    zoneDialog.setHeaderText("Choisir la zone du capteur");
                    Optional<String> zoneChoice = zoneDialog.showAndWait();

                    zoneChoice.ifPresent(zoneStr -> {
                        Zone selectedZone = null;
                        for (Zone z : zones) {
                            if ((z.getCode() + " - " + z.getNom()).equals(zoneStr)) {
                                selectedZone = z;
                                break;
                            }
                        }

                        if (selectedZone == null) {
                            showAlert("Erreur", "Zone non trouvée.");
                            return;
                        }

                        try {
                            if (typeStr.equals("Mesure")) {
                                // Demander les seuils et unité
                                TextInputDialog minDialog = new TextInputDialog("0.0");
                                minDialog.setTitle("Seuil Min");
                                minDialog.setHeaderText("Seuil minimum:");
                                Optional<String> minOpt = minDialog.showAndWait();
                                if (!minOpt.isPresent()) return;

                                TextInputDialog maxDialog = new TextInputDialog("100.0");
                                maxDialog.setTitle("Seuil Max");
                                maxDialog.setHeaderText("Seuil maximum:");
                                Optional<String> maxOpt = maxDialog.showAndWait();
                                if (!maxOpt.isPresent()) return;

                                TextInputDialog uniteDialog = new TextInputDialog("°C");
                                uniteDialog.setTitle("Unité");
                                uniteDialog.setHeaderText("Unité de mesure:");
                                Optional<String> uniteOpt = uniteDialog.showAndWait();
                                if (!uniteOpt.isPresent()) return;

                                double min = Double.parseDouble(minOpt.get());
                                double max = Double.parseDouble(maxOpt.get());
                                String unite = uniteOpt.get();

                                // Créer le capteur (sera ajouté automatiquement à GestionnaireCapteurs via le constructeur)
                                new CapteurMesure(code, selectedZone, min, max, unite, TypeMesure.TEMPERATURE);

                            } else { // GPS
                                TextInputDialog rayonDialog = new TextInputDialog("100.0");
                                rayonDialog.setTitle("Rayon Max");
                                rayonDialog.setHeaderText("Rayon maximum (m):");
                                Optional<String> rayonOpt = rayonDialog.showAndWait();
                                if (!rayonOpt.isPresent()) return;

                                double rayon = Double.parseDouble(rayonOpt.get());

                                // Créer le capteur (sera ajouté automatiquement à GestionnaireCapteurs via le constructeur)
                                new CapteurGPS(code, selectedZone, 0.0, rayon);
                            }

                            // Sauvegarder les capteurs
                            sauvegarderCapteursCSV("TP/csv/capteurs.csv", GestionnaireCapteurs.getCapteurs());
                            rebuildList.run();
                            totalLabel.setText("Total capteurs: " + GestionnaireCapteurs.getCapteurs().size());
                            showAlert("Succès", "Capteur ajouté.");

                        } catch (NumberFormatException ex) {
                            showAlert("Erreur", "Format numérique invalide.");
                        }
                    });
                });
            });
        });

        // **ACTION : Supprimer un capteur**
        supprimerButton.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= GestionnaireCapteurs.getCapteurs().size()) {
                showAlert("Erreur", "Sélectionnez un capteur.");
                return;
            }

            Capteur sel = GestionnaireCapteurs.getCapteurs().get(idx);
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmer");
            confirmDialog.setHeaderText("Supprimer le capteur " + sel.getCode() + "?");
            Optional<ButtonType> confirmOpt = confirmDialog.showAndWait();

            if (confirmOpt.isPresent() && confirmOpt.get() == ButtonType.OK) {
                GestionnaireCapteurs.getCapteurs().remove(idx);
                sauvegarderCapteursCSV("TP/csv/capteurs.csv", GestionnaireCapteurs.getCapteurs());
                rebuildList.run();
                totalLabel.setText("Total capteurs: " + GestionnaireCapteurs.getCapteurs().size());
                refreshDetails.run();
            }
        });

        // ========== ZONE INFORMATION ==========
        VBox infoBox = new VBox(12,
                capteurCodeLabel,
                typeLabel,
                zoneLabel,
                statutLabel,
                seuilLabel,
                valeurActuelleLabel,
                new Separator());
        infoBox.setPadding(new Insets(15));
        infoBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: #ffffff;");

        // ========== MISE EN PAGE ==========
        root.setTop(topBar);
        root.setLeft(leftPane);
        root.setCenter(new VBox(10, infoBox, actionButtons));
        BorderPane.setMargin(new VBox(10, infoBox, actionButtons), new Insets(10));

        // Sélectionner le premier capteur par défaut
        if (!GestionnaireCapteurs.getCapteurs().isEmpty()) {
            capteurList.getSelectionModel().selectFirst();
            refreshDetails.run();
        }

        return root;
    }

    /**
     * Charge les capteurs depuis GestionnaireCapteurs.
     * Note : les capteurs sont déjà chargés depuis le CSV lors de l'initialisation du projet
     * via chargerCapteursCSV(), donc cette méthode initialise juste la liste.
     */
    private static void chargerCapteursDepuisGestionnaire() {
        if (GestionnaireCapteurs.getCapteurs().isEmpty()) {
            chargerCapteursCSV("TP/csv/capteurs.csv");
        }
    }

    /**
     * Charge les capteurs depuis un fichier CSV.
     * Format : TYPE,CODE,ZONE_CODE,SEUIL_MIN,SEUIL_MAX,[UNITE],[TYPE_MESURE] pour Mesure
     *          TYPE,CODE,ZONE_CODE,SEUIL_MIN,SEUIL_MAX,LATITUDE,LONGITUDE pour GPS
     */
    private static void chargerCapteursCSV(String filePath) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(",");
                if (parts.length < 7) continue;

                String type = parts[0].trim();
                String code = parts[1].trim();
                String zoneCode = parts[2].trim();
                Zone zone = ZonesView.getZones().stream()
                        .filter(z -> z.getCode().equals(zoneCode))
                        .findFirst()
                        .orElse(null);

                if (zone == null) continue;

                try {
                    if (type.equals("MESURE") && parts.length >= 7) {
                        double seuilMin = Double.parseDouble(parts[3].trim());
                        double seuilMax = Double.parseDouble(parts[4].trim());
                        String unite = parts[5].trim();
                        TypeMesure mesureType = parts.length > 6 ? TypeMesure.valueOf(parts[6].trim()) : TypeMesure.TEMPERATURE;
                        // Le constructeur enregistre automatiquement le capteur dans GestionnaireCapteurs
                        new CapteurMesure(code, zone, seuilMin, seuilMax, unite, mesureType);

                    } else if (type.equals("GPS") && parts.length >= 7) {
                        double seuilMin = Double.parseDouble(parts[3].trim());
                        double seuilMax = Double.parseDouble(parts[4].trim());
                        double latitude = Double.parseDouble(parts[5].trim().replace(',', '.'));
                        double longitude = Double.parseDouble(parts[6].trim().replace(',', '.'));
                        // Le constructeur enregistre automatiquement le capteur dans GestionnaireCapteurs
                        CapteurGPS cg = new CapteurGPS(code, zone, seuilMin, seuilMax);
                        cg.setLatitude(latitude);
                        cg.setLongitude(longitude);
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Erreur de lecture capteur CSV (format numérique) : " + line);
                } catch (IllegalArgumentException ex) {
                    System.err.println("Erreur de lecture capteur CSV (enum) : " + line);
                }
            }
        } catch (IOException ex) {
            System.err.println("Impossible de charger le fichier capteurs.csv : " + ex.getMessage());
        }
    }

    /**
     * Sauvegarde les capteurs dans un fichier CSV.
     */
    private static void sauvegarderCapteursCSV(String filePath, List<Capteur> liste) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(filePath)))) {
            for (Capteur c : liste) {
                String zoneCode = (c.getZone() != null) ? c.getZone().getCode() : "";
                if (c instanceof CapteurMesure) {
                    CapteurMesure cm = (CapteurMesure) c;
                    pw.println(String.join(",", "MESURE", cm.getCode(), zoneCode,
                            Double.toString(cm.getSeuilMin()), Double.toString(cm.getSeuilMax()),
                            cm.getUnite(), cm.getType().toString()));
                } else if (c instanceof CapteurGPS) {
                    CapteurGPS cg = (CapteurGPS) c;
                    pw.println(String.join(",", "GPS", cg.getCode(), zoneCode,
                            Double.toString(cg.getSeuilMin()), Double.toString(cg.getSeuilMax()),
                            Double.toString(cg.getLatitude()), Double.toString(cg.getLongitude())));
                }
            }
        } catch (IOException ex) {
            System.err.println("Impossible d'enregistrer capteurs.csv : " + ex.getMessage());
        }
    }

    /**
     * Affiche un dialogue d'alerte générique.
     */
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Retourne la liste des alertes générées.
     * Accessible par AlertesView pour afficher les alertes actives.
     */
    public static List<Alerte> getAlertes() {
        return alertes;
    }

    /**
     * Sauvegarde les alertes dans un fichier CSV (optionnel, pour persistance future).
     */
    public static void sauvegarderAlertesCSV(String filePath, List<Alerte> liste) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(filePath)))) {
            for (Alerte a : liste) {
                Releve r = a.getReleve();
                String cap = r.getCapteur().getCode();
                String date = a.getDateCreation().toString();
                String valeur = r.isGPS() ? (Double.toString(((ReleveGPS) r).getLatitude()) + "," + Double.toString(((ReleveGPS) r).getLongitude())) : Double.toString(((ReleveMesure) r).getValeur());
                String niveau = a.getGravite().toString();
                String objet = a.getObjet().replace(',', ' ');
                String acquit = a.isAcquittee() ? "1" : "0";
                pw.println(String.join(",", a.getIdAlerte(), cap, date, valeur, niveau, objet, acquit));
            }
        } catch (IOException ex) {
            System.err.println("Impossible d'enregistrer alertes.csv : " + ex.getMessage());
        }
    }
}
