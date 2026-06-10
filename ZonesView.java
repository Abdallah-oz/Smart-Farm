package TP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ZonesView {
    private static List<Zone> zones;

    public static BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("Vue d'ensemble des Zones");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnZoneCulture = new Button("Zone Culture");
        Button btnZoneElevage = new Button("Zone Elevage");
        Button btnZoneAquacole = new Button("Zone Aquacole");

        Button btnAddZone = new Button("Ajouter Zone");
        HBox topBar = new HBox(15, titleLabel, btnZoneCulture, btnZoneElevage, btnZoneAquacole, btnAddZone);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #f4f4f4;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        root.setTop(topBar);

        final GridPane cards = new GridPane();
        cards.setHgap(20);
        cards.setVgap(20);

        if (zones == null) {
            zones = chargerZonesCSV("TP/csv/zones.csv");
        }
        int col = 0;
        for (Zone z : zones) {
            if (z instanceof ZoneCulture) {
                cards.add(createZoneCultureCard((ZoneCulture) z), col++, 0);
            } else if (z instanceof ZoneElevage) {
                ZoneElevage zoneElevage = (ZoneElevage) z;
                ProgrammeAlimentation programme = new ProgrammeAlimentation(zoneElevage, "Aliment", 10.0);
                cards.add(createZoneElevageCard(zoneElevage, programme), col++, 0);
            } else if (z instanceof ZoneAquacole) {
                cards.add(createZoneAquacoleCard((ZoneAquacole) z), col++, 0);
            }
        }

        root.setCenter(cards);

        btnZoneCulture.setOnAction(e -> System.out.println("Zone Culture sélectionnée"));
        btnZoneElevage.setOnAction(e -> System.out.println("Zone Elevage sélectionnée"));
        btnZoneAquacole.setOnAction(e -> System.out.println("Zone Aquacole sélectionnée"));

        btnAddZone.setOnAction(e -> {
            List<String> choices = new ArrayList<>();
            choices.add("Zone Culture");
            choices.add("Zone Elevage");
            choices.add("Zone Aquacole");
            ChoiceDialog<String> choice = new ChoiceDialog<>(choices.get(0), choices);
            choice.setTitle("Ajouter une zone");
            choice.setHeaderText("Type de zone");
            choice.setContentText("Choisissez :");
            Optional<String> res = choice.showAndWait();
            res.ifPresent(type -> {
                TextInputDialog codeDialog = new TextInputDialog();
                codeDialog.setTitle("Code zone");
                codeDialog.setHeaderText("Entrez le code de la zone");
                Optional<String> codeOpt = codeDialog.showAndWait();
                codeOpt.ifPresent(code -> {
                    TextInputDialog nameDialog = new TextInputDialog();
                    nameDialog.setTitle("Nom zone");
                    nameDialog.setHeaderText("Entrez le nom de la zone");
                    Optional<String> nameOpt = nameDialog.showAndWait();
                    nameOpt.ifPresent(name -> {
                        int nextCol = cards.getChildren().size();
                        if (type.equals("Zone Culture")) {
                            ZoneCulture newZc = new ZoneCulture(code, name);
                            zones.add(newZc);
                            cards.add(createZoneCultureCard(newZc), nextCol, 0);
                        } else if (type.equals("Zone Elevage")) {
                            ZoneElevage newZe = new ZoneElevage(code, name, TypeElevage.RUMINANT, 20.0, 10.0);
                            zones.add(newZe);
                            cards.add(createZoneElevageCard(newZe, new ProgrammeAlimentation(newZe, "Aliment", 10.0)), nextCol, 0);
                        } else if (type.equals("Zone Aquacole")) {
                            ZoneAquacole newZa = new ZoneAquacole(code, name, "Espèce");
                            zones.add(newZa);
                            cards.add(createZoneAquacoleCard(newZa), nextCol, 0);
                        }
                        sauvegarderZonesCSV("TP/csv/zones.csv", zones);
                    });
                });
            });
        });

        return root;
    }

    private static VBox createZoneCultureCard(ZoneCulture zc) {
        Label title = new Label(" Zone Culture");
        title.setStyle("-fx-font-weight: bold;");

        Label codeLabel = new Label("Code: " + zc.getCode());
        Label nomLabel = new Label("Nom: " + zc.getNom());
        Label statutLabel = new Label("Statut: " + zc.getStatut());
        Label countLabel = new Label("Nombre de cultures: " + zc.getCultures().size());
        VBox culturesBox = new VBox(5);
        for (Culture culture : zc.getCultures()) {
            culturesBox.getChildren().add(new Label(culture.getType() + " - " + culture.getStade()));
        }
        Label rendementLabel = new Label("Rendement global: " + zc.getRendement());

        Button calculerButton = new Button("Calculer Rendement");
        calculerButton.setOnAction(e -> {
            zc.calculerRendement();
            rendementLabel.setText("Rendement global: " + zc.getRendement());
        });

        Button rapportButton = new Button("📋 Rapport Culture");
        rapportButton.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Culture cu : zc.getCultures()) {
                sb.append("Type: ").append(cu.getType())
                  .append(" | Stade: ").append(cu.getStade())
                  .append(" | pH: ").append(cu.getExigencesPH())
                  .append(" | Rendement: ").append(cu.getRendement())
                  .append("\n");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rapport cultures");
            alert.setHeaderText("Détails des cultures de " + zc.getNom());
            alert.setContentText(sb.toString());
            alert.showAndWait();
        });

        Button ajouterCultureBtn = new Button(" Ajouter Culture");
        ajouterCultureBtn.setOnAction(e -> {
            List<FamilleCulture> fams = new ArrayList<>();
            for (FamilleCulture f : FamilleCulture.values()) fams.add(f);
            ChoiceDialog<FamilleCulture> famDialog = new ChoiceDialog<>(fams.get(0), fams);
            famDialog.setTitle("Ajouter Culture");
            famDialog.setHeaderText("Choisir la famille de culture");
            Optional<FamilleCulture> famRes = famDialog.showAndWait();
                famRes.ifPresent(fam -> {
                Culture newC = new Culture(fam, LocalDateTime.now(), LocalDateTime.now().plusMonths(3), 6.5);
                zc.ajouterCulture(newC);
                culturesBox.getChildren().add(new Label(newC.getType() + " - " + newC.getStade()));
                countLabel.setText("Nombre de cultures: " + zc.getCultures().size());
                sauvegarderZonesCSV("TP/csv/zones.csv", zones);
            });
        });

        Button modifierCultureBtn = new Button(" Modifier Culture");
        modifierCultureBtn.setOnAction(e -> {
            List<Culture> listC = zc.getCultures();
            if (listC.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Modifier Culture");
                a.setHeaderText(null);
                a.setContentText("Aucune culture à modifier.");
                a.showAndWait();
                return;
            }
            List<String> labels = new ArrayList<>();
            for (int i = 0; i < listC.size(); i++) labels.add(i + ": " + listC.get(i).getType());
            ChoiceDialog<String> choose = new ChoiceDialog<>(labels.get(0), labels);
            choose.setTitle("Modifier Culture");
            choose.setHeaderText("Choisir une culture");
            Optional<String> chosen = choose.showAndWait();
            chosen.ifPresent(sel -> {
                int idx = Integer.parseInt(sel.split(":")[0]);
                Culture target = listC.get(idx);
                TextInputDialog phDlg = new TextInputDialog(Double.toString(target.getExigencesPH()));
                phDlg.setTitle("Exigences pH");
                phDlg.setHeaderText("Modifier exigences pH");
                Optional<String> phOpt = phDlg.showAndWait();
                phOpt.ifPresent(phStr -> {
                    try {
                        double ph = Double.parseDouble(phStr);
                        target.setExigencesPH(ph);
                    } catch (NumberFormatException ex) { }
                });
                TextInputDialog rendDlg = new TextInputDialog(Double.toString(target.getRendement()));
                rendDlg.setTitle("Rendement");
                rendDlg.setHeaderText("Modifier rendement");
                Optional<String> rOpt = rendDlg.showAndWait();
                rOpt.ifPresent(rStr -> {
                    try {
                        double r = Double.parseDouble(rStr);
                        target.setRendement(r);
                        rendementLabel.setText("Rendement global: " + zc.getRendement());
                        sauvegarderZonesCSV("TP/csv/zones.csv", zones);
                    } catch (NumberFormatException ex) { }
                });
            });
        });

        Button voirCapteursBtn = new Button(" Voir capteurs");
        voirCapteursBtn.setOnAction(e -> {
            List<Capteur> caps = GestionnaireCapteurs.getCapteursByZone(zc);
            StringBuilder sbc = new StringBuilder();
            if (caps.isEmpty()) sbc.append("Aucun capteur pour cette zone.\n");
            for (Capteur c : caps) {
                sbc.append(c.getCode()).append(" - ").append(c.getClass().getSimpleName()).append(" - ").append(c.getStatut()).append("\n");
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Capteurs");
            a.setHeaderText("Capteurs pour " + zc.getNom());
            a.setContentText(sbc.toString());
            a.showAndWait();
        });

        Button changerStatutBtn = new Button(" Changer Statut");
        changerStatutBtn.setOnAction(e -> {
            ChoiceDialog<Statut> statDialog = new ChoiceDialog<>(zc.getStatut(), Statut.values());
            statDialog.setTitle("Changer statut");
            statDialog.setHeaderText("Choisir le nouveau statut");
            Optional<Statut> statRes = statDialog.showAndWait();
            statRes.ifPresent(s -> {
                zc.changerStatut(s);
                statutLabel.setText("Statut: " + zc.getStatut());
                sauvegarderZonesCSV("TP/csv/zones.csv", zones);
            });
        });

        HBox actions = new HBox(8, calculerButton, rapportButton, ajouterCultureBtn, modifierCultureBtn, voirCapteursBtn, changerStatutBtn);

        VBox card = new VBox(10, title, codeLabel, nomLabel, statutLabel, countLabel, culturesBox, rendementLabel, actions);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: #ffffff;");
        card.setPrefWidth(320);
        return card;
    }

    private static List<Zone> chargerZonesCSV(String filePath) {
        List<Zone> zones = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length == 0) continue;
                String type = parts[0].trim();
                switch (type) {
                    case "CULTURE" -> {
                        if (parts.length >= 3) {
                            String code = parts[1].trim();
                            String nom = parts[2].trim();
                            ZoneCulture zc = new ZoneCulture(code, nom);
                            zc.setStatut(Statut.ACTIVE);
                            zones.add(zc);
                        }
                    }
                    case "ELEVAGE" -> {
                        if (parts.length >= 6) {
                            try {
                                String code = parts[1].trim();
                                String nom = parts[2].trim();
                                TypeElevage typeElevage = TypeElevage.valueOf(parts[3].trim());
                                double longueur = Double.parseDouble(parts[4].trim());
                                double largeur = Double.parseDouble(parts[5].trim());
                                ZoneElevage ze = new ZoneElevage(code, nom, typeElevage, longueur, largeur);
                                ze.setStatut(Statut.ACTIVE);
                                zones.add(ze);
                            } catch (IllegalArgumentException ex) {
                                System.err.println("Erreur de format pour la ligne ELEVAGE : " + line);
                            }
                        }
                    }
                    case "AQUACOLE" -> {
                        if (parts.length >= 4) {
                            String code = parts[1].trim();
                            String nom = parts[2].trim();
                            String espece = parts[3].trim();
                            ZoneAquacole za = new ZoneAquacole(code, nom, espece);
                            za.setStatut(Statut.ACTIVE);
                            zones.add(za);
                        }
                    }
                    default -> { }
                }
            }
        } catch (IOException e) {
            System.err.println("Impossible de charger le fichier zones.csv : " + e.getMessage());
        }
        return zones;
    }

    public static List<Zone> getZones() {
        if (zones == null) zones = chargerZonesCSV("TP/csv/zones.csv");
        return zones;
    }

    private static void sauvegarderZonesCSV(String filePath, List<Zone> liste) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(filePath)))) {
            for (Zone z : liste) {
                if (z instanceof ZoneCulture) {
                    ZoneCulture zc = (ZoneCulture) z;
                    pw.println(String.join(",", "CULTURE", zc.getCode(), zc.getNom().replace(',', ' ')));
                } else if (z instanceof ZoneElevage) {
                    ZoneElevage ze = (ZoneElevage) z;
                    pw.println(String.join(",", "ELEVAGE", ze.getCode(), ze.getNom().replace(',', ' '), ze.getType().toString(), Double.toString(ze.getLongueur()), Double.toString(ze.getLargeur())));
                } else if (z instanceof ZoneAquacole) {
                    ZoneAquacole za = (ZoneAquacole) z;
                    pw.println(String.join(",", "AQUACOLE", za.getCode(), za.getNom().replace(',', ' '), za.getEspece().replace(',', ' ')));
                }
            }
        } catch (IOException ex) {
            System.err.println("Impossible d'enregistrer zones.csv : " + ex.getMessage());
        }
    }

    private static VBox createZoneElevageCard(ZoneElevage ze, ProgrammeAlimentation prog) {
        Label title = new Label("🐄 Zone Elevage");
        title.setStyle("-fx-font-weight: bold;");

        Label codeLabel = new Label("Code: " + ze.getCode());
        Label typeLabel = new Label("Type: " + ze.getType());
        Label dimensionsLabel = new Label("Dimensions: " + ze.getLongueur() + "m x " + ze.getLargeur() + "m");
        Label countLabel = new Label("Nombre d'animaux: " + ze.getAnimaux().size());
        VBox animalsBox = new VBox(5);
        for (Animal animal : ze.getAnimaux()) {
            animalsBox.getChildren().add(new Label(animal.getNom() + " - " + animal.getEspece() + " - " + animal.getEtatSante()));
        }
        Label programmeLabel = new Label("Programme: " + prog.getAliment() + " - " + prog.getQuantite() + " kg");

        Button programmeButton = new Button("Voir Programme");
        programmeButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Programme d'alimentation");
            alert.setHeaderText("Programme de la zone");
            alert.setContentText(prog.getAliment() + " - " + prog.getQuantite() + " kg");
            alert.showAndWait();
        });

        Button ajouterAnimalBtn = new Button("➕ Ajouter Animal");
        ajouterAnimalBtn.setOnAction(e -> {
            List<String> opts = new ArrayList<>();
            opts.add("Ruminant");
            opts.add("Volaille");
            ChoiceDialog<String> typeDialog = new ChoiceDialog<>(opts.get(0), opts);
            typeDialog.setTitle("Ajouter animal");
            typeDialog.setHeaderText("Choisir le type d'animal");
            Optional<String> typeRes = typeDialog.showAndWait();
            typeRes.ifPresent(type -> {
                TextInputDialog nameDlg = new TextInputDialog();
                nameDlg.setTitle("Nom");
                nameDlg.setHeaderText("Nom de l'animal");
                Optional<String> nameOpt = nameDlg.showAndWait();
                nameOpt.ifPresent(name -> {
                    TextInputDialog especeDlg = new TextInputDialog();
                    especeDlg.setTitle("Espèce");
                    especeDlg.setHeaderText("Espèce de l'animal");
                    Optional<String> especeOpt = especeDlg.showAndWait();
                    especeOpt.ifPresent(espece -> {
                        TextInputDialog ageDlg = new TextInputDialog("1");
                        ageDlg.setTitle("Âge");
                        ageDlg.setHeaderText("Âge (années)");
                        Optional<String> ageOpt = ageDlg.showAndWait();
                        ageOpt.ifPresent(ageStr -> {
                            TextInputDialog poidsDlg = new TextInputDialog("1.0");
                            poidsDlg.setTitle("Poids");
                            poidsDlg.setHeaderText("Poids (kg)");
                            Optional<String> poidsOpt = poidsDlg.showAndWait();
                            poidsOpt.ifPresent(pStr -> {
                                try {
                                    int age = Integer.parseInt(ageStr);
                                    double poids = Double.parseDouble(pStr);
                                    String numero = "A" + (System.currentTimeMillis() % 10000);
                                    if (type.equals("Ruminant")) {
                                        Ruminant newR = new Ruminant(numero, name, espece, age, poids, EtatSante.SAIN);
                                        ze.ajouterAnimal(newR);
                                        animalsBox.getChildren().add(new Label(newR.getNom() + " - " + newR.getEspece()));
                                    } else {
                                        Volaille newV = new Volaille(numero, name, espece, age, poids, EtatSante.SAIN);
                                        ze.ajouterAnimal(newV);
                                        animalsBox.getChildren().add(new Label(newV.getNom() + " - " + newV.getEspece()));
                                    }
                                    countLabel.setText("Nombre d'animaux: " + ze.getAnimaux().size());
                                } catch (NumberFormatException ex) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Valeur invalide");
                                    alert.setHeaderText("Âge ou poids invalide");
                                    alert.setContentText("Veuillez entrer des nombres valides.");
                                    alert.showAndWait();
                                }
                            });
                        });
                    });
                });
            });
        });

        Button changerStatutBtn = new Button("🔄 Changer Statut");
        changerStatutBtn.setOnAction(e -> {
            ChoiceDialog<Statut> statDialog = new ChoiceDialog<>(ze.getStatut(), Statut.values());
            statDialog.setTitle("Changer statut");
            statDialog.setHeaderText("Choisir le nouveau statut");
            Optional<Statut> statRes = statDialog.showAndWait();
            statRes.ifPresent(s -> {
                ze.changerStatut(s);
                // update label
                // refresh animals count label
                // simple approach: update countLabel text
                // (animalsBox updated on add)
                // set a small info alert
                Alert inf = new Alert(Alert.AlertType.INFORMATION);
                inf.setTitle("Statut modifié");
                inf.setHeaderText(null);
                inf.setContentText("Statut de la zone " + ze.getNom() + " mis à jour: " + ze.getStatut());
                inf.showAndWait();
            });
        });

        Button voirCapteursBtn = new Button("📡 Voir capteurs");
        voirCapteursBtn.setOnAction(e -> {
            List<Capteur> caps = GestionnaireCapteurs.getCapteursByZone(ze);
            StringBuilder sbc = new StringBuilder();
            if (caps.isEmpty()) sbc.append("Aucun capteur pour cette zone.\n");
            for (Capteur c : caps) {
                sbc.append(c.getCode()).append(" - ").append(c.getClass().getSimpleName()).append(" - ").append(c.getStatut()).append("\n");
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Capteurs");
            a.setHeaderText("Capteurs pour " + ze.getNom());
            a.setContentText(sbc.toString());
            a.showAndWait();
        });

        Button editProgrammeBtn = new Button(" Modifier Programme");
        editProgrammeBtn.setOnAction(e -> {
            TextInputDialog alimentDlg = new TextInputDialog(prog.getAliment());
            alimentDlg.setTitle("Modifier programme");
            alimentDlg.setHeaderText("Aliment");
            Optional<String> alimOpt = alimentDlg.showAndWait();
            alimOpt.ifPresent(aStr -> prog.setAliment(aStr));
            TextInputDialog qDlg = new TextInputDialog(Double.toString(prog.getQuantite()));
            qDlg.setTitle("Modifier programme");
            qDlg.setHeaderText("Quantité (kg)");
            Optional<String> qOpt = qDlg.showAndWait();
            qOpt.ifPresent(qStr -> {
                try {
                    double q = Double.parseDouble(qStr);
                    prog.setQuantite(q);
                    programmeLabel.setText("Programme: " + prog.getAliment() + " - " + prog.getQuantite() + " kg");
                } catch (NumberFormatException ex) { }
            });
        });

        HBox actions = new HBox(8, programmeButton, editProgrammeBtn, ajouterAnimalBtn, voirCapteursBtn, changerStatutBtn);

        VBox card = new VBox(10, title, codeLabel, typeLabel, dimensionsLabel, countLabel, animalsBox, programmeLabel, actions);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: #ffffff;");
        card.setPrefWidth(320);
        return card;
    }

    private static VBox createZoneAquacoleCard(ZoneAquacole za) {
        Label title = new Label("🐟 Zone Aquacole");
        title.setStyle("-fx-font-weight: bold;");

        Label codeLabel = new Label("Code: " + za.getCode());
        Label especeLabel = new Label("Espèce: " + za.getEspece());
        Label poidsLabel = new Label("Poids récolte: " + za.getPoidRecolte() + " kg");

        Button updateButton = new Button("Mettre à jour");
        updateButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(Double.toString(za.getPoidRecolte()));
            dialog.setTitle("Mettre à jour le poids");
            dialog.setHeaderText("Modifier le poids de récolte");
            dialog.setContentText("Nouveau poids (kg) :");
            dialog.showAndWait().ifPresent(value -> {
                try {
                    double nouveauPoids = Double.parseDouble(value);
                    za.affecterPoidRecolte(nouveauPoids);
                    poidsLabel.setText("Poids récolte: " + za.getPoidRecolte() + " kg");
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Valeur invalide");
                    alert.setHeaderText("Impossible de mettre à jour le poids");
                    alert.setContentText("Veuillez saisir un nombre valide.");
                    alert.showAndWait();
                }
            });
        });

        Button ajouterEspeceBtn = new Button(" Ajouter Espèce");
        ajouterEspeceBtn.setOnAction(e -> {
            TextInputDialog especeDlg = new TextInputDialog(za.getEspece());
            especeDlg.setTitle("Ajouter espèce");
            especeDlg.setHeaderText("Nom de la nouvelle espèce");
            Optional<String> especeOpt = especeDlg.showAndWait();
            especeOpt.ifPresent(s -> {
                za.ajouterEspece(s);
                especeLabel.setText("Espèce: " + za.getEspece());
            });
        });

        Button changerStatutBtn = new Button("🔄 Changer Statut");
        changerStatutBtn.setOnAction(e -> {
            ChoiceDialog<Statut> statDialog = new ChoiceDialog<>(za.getStatut(), Statut.values());
            statDialog.setTitle("Changer statut");
            statDialog.setHeaderText("Choisir le nouveau statut");
            Optional<Statut> statRes = statDialog.showAndWait();
            statRes.ifPresent(s -> {
                za.changerStatut(s);
                Alert inf = new Alert(Alert.AlertType.INFORMATION);
                inf.setTitle("Statut modifié");
                inf.setHeaderText(null);
                inf.setContentText("Statut de la zone " + za.getNom() + " mis à jour: " + za.getStatut());
                inf.showAndWait();
            });
        });

        Button voirCapteursBtn = new Button("📡 Voir capteurs");
        voirCapteursBtn.setOnAction(e -> {
            List<Capteur> caps = GestionnaireCapteurs.getCapteursByZone(za);
            StringBuilder sbc = new StringBuilder();
            if (caps.isEmpty()) sbc.append("Aucun capteur pour cette zone.\n");
            for (Capteur c : caps) {
                sbc.append(c.getCode()).append(" - ").append(c.getClass().getSimpleName()).append(" - ").append(c.getStatut()).append("\n");
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Capteurs");
            a.setHeaderText("Capteurs pour " + za.getNom());
            a.setContentText(sbc.toString());
            a.showAndWait();
        });

        HBox actions = new HBox(8, updateButton, ajouterEspeceBtn, voirCapteursBtn, changerStatutBtn);

        VBox card = new VBox(10, title, codeLabel, especeLabel, poidsLabel, actions);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: #ffffff;");
        card.setPrefWidth(320);
        return card;
    }
}
