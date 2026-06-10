package TP;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

public class AnimauxView {
    
    private static List<Animal> animaux;

    public static BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Tableau de bord des Animaux");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnRefresh = new Button("Actualiser");
        Button btnAdd = new Button("➕ Ajouter Animal");
        HBox header = new HBox(10, title, btnRefresh, btnAdd);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #f4f4f4;");
        HBox.setHgrow(title, Priority.ALWAYS);
        title.setMaxWidth(Double.MAX_VALUE);

        if (animaux == null) {
            animaux = chargerAnimauxCSV("TP/csv/animaux.csv");
        }

        ListView<String> animalList = new ListView<>();
        animalList.setItems(FXCollections.observableArrayList());
        // populate list view from animaux
        Runnable rebuildList = () -> {
            List<String> items = new ArrayList<>();
            for (Animal a : animaux) items.add(a.getNom() + " (" + a.getEspece() + ")");
            animalList.setItems(FXCollections.observableArrayList(items));
        };
        rebuildList.run();
        animalList.setPrefWidth(250);

        GridPane detailGrid = new GridPane();
        detailGrid.setHgap(10);
        detailGrid.setVgap(10);
        detailGrid.setPadding(new Insets(10));

        Label totalLabel = new Label("Total d'animaux: " + animaux.size());
        totalLabel.setStyle("-fx-font-weight: bold;");

        Button changeStateButton = new Button("Changer état");
        Button historyButton = new Button("Voir historique");
        Button prodButton = new Button("Enregistrer production");
        Button deleteButton = new Button("Supprimer");
        Button editButton = new Button("Modifier");
        HBox buttons = new HBox(10, changeStateButton, prodButton, editButton, deleteButton, historyButton);

        VBox leftPane = new VBox(15, animalList, totalLabel);
        leftPane.setPadding(new Insets(10));
        leftPane.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: #ffffff;");

        root.setTop(header);
        root.setLeft(leftPane);
        root.setCenter(detailGrid);
        root.setBottom(buttons);
        BorderPane.setMargin(buttons, new Insets(10, 0, 0, 10));

        Runnable refreshDetails = () -> {
            int index = animalList.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < animaux.size()) {
                updateDetails(detailGrid, animaux.get(index));
            }
        };

        animalList.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> refreshDetails.run());
        btnRefresh.setOnAction(e -> refreshDetails.run());

        changeStateButton.setOnAction(e -> {
            int index = animalList.getSelectionModel().getSelectedIndex();
            if (index < 0 || index >= animaux.size()) {
                return;
            }
            Animal current = animaux.get(index);
            ChoiceDialog<EtatSante> dialog = new ChoiceDialog<>(current.getEtatSante(), EtatSante.values());
            dialog.setTitle("Changer l'état de santé");
            dialog.setHeaderText("Sélectionnez un nouvel état");
            dialog.setContentText("État :");
            Optional<EtatSante> result = dialog.showAndWait();
            result.ifPresent(selected -> {
                TextInputDialog poidsDlg = new TextInputDialog(Double.toString(current.getPoids()));
                poidsDlg.setTitle("Nouveau poids");
                poidsDlg.setHeaderText("Entrez le nouveau poids (kg) pour l'animal");
                Optional<String> poidsOpt = poidsDlg.showAndWait();
                double newPoids = current.getPoids();
                if (poidsOpt.isPresent()) {
                    try { newPoids = Double.parseDouble(poidsOpt.get()); } catch (NumberFormatException ex) { }
                }
                current.changerEtatSante(selected, newPoids);
                sauvegarderAnimauxCSV("TP/csv/animaux.csv", animaux);
                refreshDetails.run();
            });
        });

        historyButton.setOnAction(e -> {
            int index = animalList.getSelectionModel().getSelectedIndex();
            if (index < 0 || index >= animaux.size()) {
                return;
            }
            Animal current = animaux.get(index);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Historique de l'animal");
            alert.setHeaderText("Historique pour " + current.getNom());
            StringBuilder content = new StringBuilder();
            if (current.getHistorique().isEmpty()) {
                content.append("Aucun événement enregistré.");
            } else {
                current.getHistorique().forEach(evt -> content.append(evt.getDateHeure()).append(" - ").append(evt.getDescription()).append("\n"));
            }
            alert.setContentText(content.toString());
            alert.showAndWait();
        });

        prodButton.setOnAction(e -> {
            int index = animalList.getSelectionModel().getSelectedIndex();
            if (index < 0 || index >= animaux.size()) return;
            Animal current = animaux.get(index);
            if (current instanceof Ruminant) {
                TextInputDialog dlg = new TextInputDialog(Double.toString(((Ruminant) current).getProductionLait()));
                dlg.setTitle("Enregistrer production de lait");
                dlg.setHeaderText("Litres");
                Optional<String> val = dlg.showAndWait();
                val.ifPresent(s -> {
                        try {
                        double v = Double.parseDouble(s);
                        ((Ruminant) current).enregistrerProdLait(v);
                        sauvegarderAnimauxCSV("TP/csv/animaux.csv", animaux);
                        refreshDetails.run();
                    } catch (NumberFormatException ex) { }
                });
            } else if (current instanceof Volaille) {
                TextInputDialog dlg = new TextInputDialog(Integer.toString(((Volaille) current).getProductionOeufs()));
                dlg.setTitle("Enregistrer production d'oeufs");
                dlg.setHeaderText("Nombre d'oeufs");
                Optional<String> val = dlg.showAndWait();
                val.ifPresent(s -> {
                        try {
                        int v = Integer.parseInt(s);
                        ((Volaille) current).enregistrerProdOeufs(v);
                        sauvegarderAnimauxCSV("TP/csv/animaux.csv", animaux);
                        refreshDetails.run();
                    } catch (NumberFormatException ex) { }
                });
            } else {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Production non applicable pour ce type d'animal.");
                a.showAndWait();
            }
        });

        deleteButton.setOnAction(e -> {
            int index = animalList.getSelectionModel().getSelectedIndex();
            if (index < 0 || index >= animaux.size()) return;
            animaux.remove(index);
            sauvegarderAnimauxCSV("TP/csv/animaux.csv", animaux);
            rebuildList.run();
            totalLabel.setText("Total d'animaux: " + animaux.size());
            detailGrid.getChildren().clear();
        });

        editButton.setOnAction(e -> {
            int index = animalList.getSelectionModel().getSelectedIndex();
            if (index < 0 || index >= animaux.size()) return;
            Animal current = animaux.get(index);
            TextInputDialog nameDlg = new TextInputDialog(current.getNom());
            nameDlg.setTitle("Modifier nom");
            nameDlg.setHeaderText("Nom");
            Optional<String> nameOpt = nameDlg.showAndWait();
            nameOpt.ifPresent(current::setNom);
            TextInputDialog ageDlg = new TextInputDialog(Integer.toString(current.getAge()));
            ageDlg.setTitle("Modifier âge");
            ageDlg.setHeaderText("Âge");
            Optional<String> ageOpt = ageDlg.showAndWait();
            ageOpt.ifPresent(s -> { try { current.setAge(Integer.parseInt(s)); } catch (NumberFormatException ex){} });
            TextInputDialog poidsDlg = new TextInputDialog(Double.toString(current.getPoids()));
            poidsDlg.setTitle("Modifier poids");
            poidsDlg.setHeaderText("Poids (kg)");
            Optional<String> poidsOpt = poidsDlg.showAndWait();
            poidsOpt.ifPresent(s -> { try { current.setPoids(Double.parseDouble(s)); } catch (NumberFormatException ex){} });
            sauvegarderAnimauxCSV("TP/csv/animaux.csv", animaux);
            rebuildList.run();
            refreshDetails.run();
        });

        animalList.getSelectionModel().selectFirst();
        refreshDetails.run();

        btnAdd.setOnAction(e -> {
            List<String> opts = new ArrayList<>(); opts.add("Ruminant"); opts.add("Volaille");
            ChoiceDialog<String> dlg = new ChoiceDialog<>(opts.get(0), opts);
            dlg.setTitle("Ajouter Animal"); dlg.setHeaderText("Type");
            Optional<String> t = dlg.showAndWait();
            t.ifPresent(type -> {
                TextInputDialog nameDlg = new TextInputDialog(); nameDlg.setTitle("Nom"); nameDlg.setHeaderText("Nom de l'animal");
                Optional<String> nameOpt = nameDlg.showAndWait();
                nameOpt.ifPresent(name -> {
                    TextInputDialog especeDlg = new TextInputDialog(); especeDlg.setTitle("Espèce"); especeDlg.setHeaderText("Espèce");
                    Optional<String> especeOpt = especeDlg.showAndWait();
                    especeOpt.ifPresent(espece -> {
                        TextInputDialog ageDlg2 = new TextInputDialog("1"); ageDlg2.setTitle("Âge");
                        Optional<String> ageOpt2 = ageDlg2.showAndWait();
                        ageOpt2.ifPresent(ageStr -> {
                            TextInputDialog poidsDlg2 = new TextInputDialog("1.0"); poidsDlg2.setTitle("Poids");
                            Optional<String> poidsOpt2 = poidsDlg2.showAndWait();
                            poidsOpt2.ifPresent(pStr -> {
                                try {
                                    int age = Integer.parseInt(ageStr);
                                    double poids = Double.parseDouble(pStr);
                                    String numero = "A" + (System.currentTimeMillis() % 10000);
                                    Animal aNew;
                                    if (type.equals("Ruminant")) aNew = new Ruminant(numero, name, espece, age, poids, EtatSante.SAIN);
                                    else aNew = new Volaille(numero, name, espece, age, poids, EtatSante.SAIN);
                                    animaux.add(aNew);
                                    sauvegarderAnimauxCSV("TP/csv/animaux.csv", animaux);
                                    rebuildList.run();
                                    totalLabel.setText("Total d'animaux: " + animaux.size());
                                } catch (NumberFormatException ex) { }
                            });
                        });
                    });
                });
            });
        });

        return root;
    }

    private static void sauvegarderAnimauxCSV(String filePath, List<Animal> liste) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(filePath)))) {
            for (Animal a : liste) {
                String type = (a instanceof Ruminant) ? "RUMINANT" : "VOLAILLE";
                String zoneCode = "";
                String code = a.getNumero();
                String nom = a.getNom().replace(',', ' ');
                String espece = a.getEspece().replace(',', ' ');
                String age = Integer.toString(a.getAge());
                String poids = Double.toString(a.getPoids());
                String etat = a.getEtatSante().toString();
                pw.println(String.join(",", type, zoneCode, code, nom, espece, age, poids, etat));
            }
        } catch (IOException ex) {
            System.err.println("Impossible d'enregistrer animaux.csv : " + ex.getMessage());
        }
    }

    private static List<Animal> chargerAnimauxCSV(String filePath) {
        List<Animal> animaux = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length < 8) continue;
                String type = parts[0].trim();
                String code = parts[2].trim();
                String nom = parts[3].trim();
                String espece = parts[4].trim();
                String ageStr = parts[5].trim();
                String poidsStr;
                String etatStr;
                if (parts.length == 8) {
                    poidsStr = parts[6].trim();
                    etatStr = parts[7].trim();
                } else {
                    // If the line was split because of comma decimals (eg. "600,0" -> "600","0"),
                    // rebuild poids from parts[6]..parts[length-2] and take the last token as etat
                    etatStr = parts[parts.length - 1].trim();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 6; i <= parts.length - 2; i++) {
                        if (sb.length() > 0) sb.append(',');
                        sb.append(parts[i].trim());
                    }
                    // convert decimal comma to dot if present
                    poidsStr = sb.toString().replace(',', '.');
                }
                try {
                    int age = Integer.parseInt(ageStr);
                    double poids = Double.parseDouble(poidsStr);
                    EtatSante etat = EtatSante.valueOf(etatStr);
                    if (type.equals("RUMINANT")) {
                        animaux.add(new Ruminant(code, nom, espece, age, poids, etat));
                    } else if (type.equals("VOLAILLE")) {
                        animaux.add(new Volaille(code, nom, espece, age, poids, etat));
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Erreur de lecture animal CSV (format numérique) : " + line);
                } catch (IllegalArgumentException ex) {
                    System.err.println("Erreur de lecture animal CSV : " + line);
                }
            }
        } catch (IOException ex) {
            System.err.println("Impossible de charger le fichier animaux.csv : " + ex.getMessage());
        }
        return animaux;
    }

    private static void updateDetails(GridPane detailGrid, Animal animal) {
        detailGrid.getChildren().clear();
        detailGrid.add(new Label("Nom :"), 0, 0);
        detailGrid.add(new Label(animal.getNom()), 1, 0);
        detailGrid.add(new Label("Espèce :"), 0, 1);
        detailGrid.add(new Label(animal.getEspece()), 1, 1);
        detailGrid.add(new Label("Poids :"), 0, 2);
        detailGrid.add(new Label(String.format("%.1f kg", animal.getPoids())), 1, 2);
        detailGrid.add(new Label("État :"), 0, 3);
        detailGrid.add(new Label(animal.getEtatSante().toString()), 1, 3);

        if (animal instanceof Ruminant) {
            Ruminant rum = (Ruminant) animal;
            detailGrid.add(new Label("Production laitière :"), 0, 4);
            detailGrid.add(new Label(String.format("%.1f L", rum.getProductionLait())), 1, 4);
        } else if (animal instanceof Volaille) {
            Volaille vol = (Volaille) animal;
            detailGrid.add(new Label("Production d'oeufs :"), 0, 4);
            detailGrid.add(new Label(String.format("%d oeufs", vol.getProductionOeufs())), 1, 4);
        }
    }
}
