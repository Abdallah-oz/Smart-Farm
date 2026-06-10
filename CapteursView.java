package TP;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CapteursView {

    private static final List<Alerte> alertes = new ArrayList<>();
    private static List<Capteur> capteurs;

    public static BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Surveillance des Capteurs");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox topBar = new HBox(10, title);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #f4f4f4;");

        if (capteurs == null) capteurs = chargerCapteursCSV("TP/csv/capteurs.csv");
        if (capteurs == null) capteurs = chargerCapteursCSV("TP/csv/capteurs.csv");
        Zone placeholderZone = creerZoneParCode("ZC_DEF");

        ListView<String> capteurList = new ListView<>();
        capteurList.setItems(FXCollections.observableArrayList());
        capteurList.setPrefWidth(260);

        Runnable rebuildList = () -> {
            List<String> items = new ArrayList<>();
            for (Capteur c : capteurs) items.add(c.getCode() + " (" + c.getClass().getSimpleName() + ")");
            capteurList.setItems(FXCollections.observableArrayList(items));
        };
        rebuildList.run();

        Label capteurCodeLabel = new Label("Capteur : -");
        Label zoneLabel = new Label("Zone : -");
        Label statutLabel = new Label("Statut : -");
        Label seuilLabel = new Label("Seuil : -");

        Label pHValueLabel = new Label("Valeur pH actuel : -");
        Button simulatePHButton = new Button("Simuler mesure pH");
        simulatePHButton.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= capteurs.size()) return;
            Capteur sel = capteurs.get(idx);
            if (!(sel instanceof CapteurMesure)) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Action non valide");
                a.setHeaderText(null);
                a.setContentText("Sélectionnez un capteur de mesure pour simuler une valeur.");
                a.showAndWait();
                return;
            }
            CapteurMesure cm = (CapteurMesure) sel;
            Alerte alerte = cm.envoyerMesure(9.5);
            if (alerte != null) {
                alertes.add(alerte);
                pHValueLabel.setText("Valeur pH actuel : 9.5 (alerte)");
            } else {
                pHValueLabel.setText("Valeur pH actuel : 9.5 (normal)");
            }
        });

        Label gpsValueLabel = new Label("Coordonnées : -");
        Button simulateGpsButton = new Button("Simuler position");
        simulateGpsButton.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= capteurs.size()) return;
            Capteur sel = capteurs.get(idx);
            if (!(sel instanceof CapteurGPS)) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Action non valide");
                a.setHeaderText(null);
                a.setContentText("Sélectionnez un capteur GPS pour simuler une position.");
                a.showAndWait();
                return;
            }
            CapteurGPS cg = (CapteurGPS) sel;
            cg.setLatitude(36.0);
            cg.setLongitude(-1.5);
            Alerte alerte = cg.envoyerPosition();
            if (alerte != null) {
                alertes.add(alerte);
                gpsValueLabel.setText("Coordonnées : 36.0, -1.5 (alerte)");
            } else {
                gpsValueLabel.setText("Coordonnées : 36.0, -1.5 (normal)");
            }
        });

        TextField customValueField = new TextField();
        customValueField.setPromptText("Valeur pH personnalisée");
        Button customValueButton = new Button("Envoyer mesure");
        Label customStatusLabel = new Label();
        customValueButton.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= capteurs.size()) {
                customStatusLabel.setText("Sélectionnez un capteur de mesure.");
                return;
            }
            Capteur sel = capteurs.get(idx);
            if (!(sel instanceof CapteurMesure)) {
                customStatusLabel.setText("Sélectionnez un capteur de mesure.");
                return;
            }
            try {
                double value = Double.parseDouble(customValueField.getText());
                Alerte alerte = ((CapteurMesure) sel).envoyerMesure(value);
                if (alerte != null) {
                    alertes.add(alerte);
                    customStatusLabel.setText("Alerte générée pour valeur " + value);
                } else {
                    customStatusLabel.setText("Valeur " + value + " enregistrée normalement.");
                }
            } catch (NumberFormatException ex) {
                customStatusLabel.setText("Entrez un nombre valide.");
            }
        });

        Label alertCountLabel = new Label("Alertes détectées : " + alertes.size());

        Label totalLabel = new Label("Total capteurs: " + capteurs.size());

        // selection handler
        Runnable refreshDetails = () -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= capteurs.size()) return;
            Capteur sel = capteurs.get(idx);
            capteurCodeLabel.setText("Capteur : " + sel.getCode());
            zoneLabel.setText("Zone : " + (sel.getZone() != null ? sel.getZone().getNom() : "-"));
            statutLabel.setText("Statut : " + sel.getStatut());
            if (sel instanceof CapteurMesure) {
                CapteurMesure cm = (CapteurMesure) sel;
                seuilLabel.setText(String.format("Seuil : %.1f - %.1f %s", cm.getSeuilMin(), cm.getSeuilMax(), cm.getUnite()));
            } else {
                seuilLabel.setText("Seuil : -");
            }
        };

        capteurList.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> refreshDetails.run());

        // Buttons for capteur operations
        Button addButton = new Button("➕ Ajouter Capteur");
        Button editButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");
        Button simulateButton = new Button("Simuler mesure/position");
        Button historyButton = new Button("Voir dernier relevé");

        HBox btns = new HBox(10, addButton, editButton, deleteButton, simulateButton, historyButton);
        btns.setPadding(new Insets(8));

        VBox infoBox = new VBox(12,
                capteurCodeLabel,
                zoneLabel,
                statutLabel,
                seuilLabel,
                pHValueLabel,
                simulatePHButton,
                gpsValueLabel,
                simulateGpsButton,
                customValueField,
                customValueButton,
                customStatusLabel,
                alertCountLabel);
        infoBox.setPadding(new Insets(15));
        infoBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: #ffffff;");

        customValueField.setMaxWidth(200);


        VBox leftPane = new VBox(12, capteurList, totalLabel);
        leftPane.setPadding(new Insets(10));
        leftPane.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: #ffffff;");

        root.setTop(topBar);
        root.setLeft(leftPane);
        root.setCenter(infoBox);
        root.setBottom(btns);
        BorderPane.setMargin(btns, new Insets(10));

        // button actions
        addButton.setOnAction(e -> {
            List<String> types = new ArrayList<>(); types.add("Mesure"); types.add("GPS");
            ChoiceDialog<String> td = new ChoiceDialog<>(types.get(0), types);
            td.setTitle("Ajouter Capteur"); td.setHeaderText("Type de capteur");
            Optional<String> chosen = td.showAndWait();
            chosen.ifPresent(t -> {
                TextInputDialog codeDlg = new TextInputDialog(); codeDlg.setTitle("Code"); codeDlg.setHeaderText("Code du capteur");
                Optional<String> codeOpt = codeDlg.showAndWait();
                codeOpt.ifPresent(code -> {
                    // choose zone
                    List<Zone> zones = ZonesView.getZones();
                    List<String> zoneOpts = new ArrayList<>();
                    zoneOpts.add("Aucune");
                    for (Zone z : zones) zoneOpts.add(z.getCode() + " - " + z.getNom());
                    ChoiceDialog<String> zd = new ChoiceDialog<>(zoneOpts.get(0), zoneOpts);
                    zd.setTitle("Associer zone"); zd.setHeaderText("Choisir la zone du capteur");
                    Optional<String> zChoice = zd.showAndWait();
                    Zone selectedZone = placeholderZone;
                    if (zChoice.isPresent() && !zChoice.get().equals("Aucune")) {
                        String sel = zChoice.get();
                        String selCode = sel.split(" - ")[0];
                        for (Zone z : zones) if (z.getCode().equals(selCode)) { selectedZone = z; break; }
                    }
                    if (t.equals("Mesure")) {
                        CapteurMesure cm = new CapteurMesure(code, selectedZone, 0.0, 100.0, "unit", TypeMesure.PH_SOL);
                        capteurs.add(cm);
                        sauvegarderCapteursCSV("TP/csv/capteurs.csv", capteurs);
                    } else {
                        CapteurGPS cg = new CapteurGPS(code, selectedZone, 0.0, 100.0);
                        capteurs.add(cg);
                        sauvegarderCapteursCSV("TP/csv/capteurs.csv", capteurs);
                    }
                    rebuildList.run();
                    totalLabel.setText("Total capteurs: " + capteurs.size());
                });
            });
        });

        deleteButton.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= capteurs.size()) return;
            capteurs.remove(idx);
            sauvegarderCapteursCSV("TP/csv/capteurs.csv", capteurs);
            rebuildList.run();
            totalLabel.setText("Total capteurs: " + capteurs.size());
            capteurCodeLabel.setText("Capteur : -"); zoneLabel.setText("Zone : -"); statutLabel.setText("Statut : -"); seuilLabel.setText("Seuil : -");
        });

        editButton.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= capteurs.size()) return;
            Capteur sel = capteurs.get(idx);
            TextInputDialog statutDlg = new TextInputDialog(sel.getStatut().toString());
            statutDlg.setTitle("Modifier statut"); statutDlg.setHeaderText("Statut (ACTIF/INACTIF)");
            Optional<String> sOpt = statutDlg.showAndWait();
            sOpt.ifPresent(s -> {
                try { sel.setStatut(StatutCapteur.valueOf(s)); } catch (Exception ex) { }
                if (sel instanceof CapteurMesure) {
                    CapteurMesure cm = (CapteurMesure) sel;
                    TextInputDialog unitDlg = new TextInputDialog(cm.getUnite()); unitDlg.setTitle("Unité"); unitDlg.setHeaderText("Unité du capteur");
                    Optional<String> uOpt = unitDlg.showAndWait();
                    uOpt.ifPresent(cm::setUnite);
                }
                sauvegarderCapteursCSV("TP/csv/capteurs.csv", capteurs);
                refreshDetails.run();
            });
        });

        simulateButton.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= capteurs.size()) return;
            Capteur sel = capteurs.get(idx);
            if (sel instanceof CapteurMesure) {
                TextInputDialog valDlg = new TextInputDialog("0.0"); valDlg.setTitle("Valeur mesure");
                Optional<String> vOpt = valDlg.showAndWait();
                vOpt.ifPresent(vs -> {
                    try {
                        double v = Double.parseDouble(vs);
                        Alerte a = ((CapteurMesure) sel).envoyerMesure(v);
                        if (a != null) { alertes.add(a); alertCountLabel.setText("Alertes détectées : " + alertes.size()); sauvegarderAlertesCSV("TP/csv/alertes.csv", alertes); }
                        pHValueLabel.setText("Valeur pH actuel : " + v + (a!=null?" (alerte)":" (normal)"));
                    } catch (NumberFormatException ex) {}
                });
            } else if (sel instanceof CapteurGPS) {
                TextInputDialog latDlg = new TextInputDialog("0.0"); latDlg.setTitle("Latitude");
                Optional<String> latOpt = latDlg.showAndWait();
                latOpt.ifPresent(latS -> {
                    TextInputDialog lonDlg = new TextInputDialog("0.0"); lonDlg.setTitle("Longitude");
                    Optional<String> lonOpt = lonDlg.showAndWait();
                    lonOpt.ifPresent(lonS -> {
                        try {
                            double lat = Double.parseDouble(latS); double lon = Double.parseDouble(lonS);
                            ((CapteurGPS) sel).setLatitude(lat); ((CapteurGPS) sel).setLongitude(lon);
                            Alerte a = ((CapteurGPS) sel).envoyerPosition();
                            if (a != null) { alertes.add(a); alertCountLabel.setText("Alertes détectées : " + alertes.size()); sauvegarderAlertesCSV("TP/csv/alertes.csv", alertes); }
                            gpsValueLabel.setText("Coordonnées : " + lat + ", " + lon + (a!=null?" (alerte)":" (normal)"));
                        } catch (NumberFormatException ex) {}
                    });
                });
            }
        });

        historyButton.setOnAction(e -> {
            int idx = capteurList.getSelectionModel().getSelectedIndex();
            if (idx < 0 || idx >= capteurs.size()) return;
            Capteur sel = capteurs.get(idx);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Dernier relevé");
            a.setHeaderText("Dernier relevé pour " + sel.getCode());
            String content = "Aucun relevé disponible.";
            // if capteur provides last reading methods, we attempt to show generic info
            a.setContentText(content);
            a.showAndWait();
        });

        return root;
    }

    private static Zone creerZoneParCode(String code) {
        if (code.startsWith("ZC")) {
            ZoneCulture zc = new ZoneCulture(code, "Zone " + code);
            zc.setStatut(Statut.ACTIVE);
            return zc;
        }
        if (code.startsWith("ZE")) {
            ZoneElevage ze = new ZoneElevage(code, "Zone " + code, TypeElevage.RUMINANT, 20.0, 10.0);
            ze.setStatut(Statut.ACTIVE);
            return ze;
        }
        if (code.startsWith("ZA")) {
            ZoneAquacole za = new ZoneAquacole(code, "Zone " + code, "Espèce");
            za.setStatut(Statut.ACTIVE);
            return za;
        }
        ZoneCulture defaultZone = new ZoneCulture(code, "Zone " + code);
        defaultZone.setStatut(Statut.ACTIVE);
        return defaultZone;
    }

    private static List<Capteur> chargerCapteursCSV(String filePath) {
        List<Capteur> capteurs = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length < 7) continue;
                String type = parts[0].trim();
                String code = parts[1].trim();
                String zoneCode = parts[2].trim();
                Zone zone = creerZoneParCode(zoneCode);
                try {
                    if (type.equals("MESURE") && parts.length >= 7) {
                        double seuilMin = Double.parseDouble(parts[3].trim());
                        double seuilMax = Double.parseDouble(parts[4].trim());
                        String unite = parts[5].trim();
                        TypeMesure mesureType = TypeMesure.valueOf(parts[6].trim());
                        CapteurMesure cm = new CapteurMesure(code, zone, seuilMin, seuilMax, unite, mesureType);
                        capteurs.add(cm);
                    } else if (type.equals("GPS") && parts.length >= 7) {
                        double seuilMin = Double.parseDouble(parts[3].trim());
                        double seuilMax = Double.parseDouble(parts[4].trim());
                        double latitude = Double.parseDouble(parts[5].trim());
                        double longitude = Double.parseDouble(parts[6].trim());
                        CapteurGPS cg = new CapteurGPS(code, zone, seuilMin, seuilMax);
                        cg.setLatitude(latitude);
                        cg.setLongitude(longitude);
                        capteurs.add(cg);
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Erreur de lecture capteur CSV (format numérique) : " + line);
                } catch (IllegalArgumentException ex) {
                    System.err.println("Erreur de lecture capteur CSV : " + line);
                }
            }
        } catch (IOException ex) {
            System.err.println("Impossible de charger le fichier capteurs.csv : " + ex.getMessage());
        }
        return capteurs;
    }

    private static void sauvegarderCapteursCSV(String filePath, List<Capteur> liste) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(filePath)))) {
            for (Capteur c : liste) {
                String zoneCode = (c.getZone() != null) ? c.getZone().getCode() : "";
                if (c instanceof CapteurMesure) {
                    CapteurMesure cm = (CapteurMesure) c;
                    String code = cm.getCode();
                    String sMin = Double.toString(cm.getSeuilMin());
                    String sMax = Double.toString(cm.getSeuilMax());
                    String unite = cm.getUnite().replace(',', ' ');
                    String type = cm.getType().toString();
                    pw.println(String.join(",", "MESURE", code, zoneCode, sMin, sMax, unite, type));
                } else if (c instanceof CapteurGPS) {
                    CapteurGPS cg = (CapteurGPS) c;
                    String code = cg.getCode();
                    String sMin = Double.toString(cg.getSeuilMin());
                    String sMax = Double.toString(cg.getSeuilMax());
                    String lat = Double.toString(cg.getLatitude());
                    String lon = Double.toString(cg.getLongitude());
                    pw.println(String.join(",", "GPS", code, zoneCode, sMin, sMax, lat, lon));
                }
            }
        } catch (IOException ex) {
            System.err.println("Impossible d'enregistrer capteurs.csv : " + ex.getMessage());
        }
    }

    public static List<Alerte> getAlertes() {
        return alertes;
    }

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
