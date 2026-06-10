package TP;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class AlertesView {

    public static BorderPane getView(List<Alerte> alertes) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Alertes en attente");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<Alerte> alertList = new ListView<>(FXCollections.observableArrayList(alertes));
        alertList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Alerte item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String status = item.isAcquittee() ? "(Acquittée)" : "(Active)";
                    setText(item.getIdAlerte() + " - " + item.getNiveau() + " -> " + item.getObjet() + " " + status);
                }
            }
        });

        Label detailsLabel = new Label("Sélectionnez une alerte pour voir les détails.");
        detailsLabel.setWrapText(true);
        detailsLabel.setStyle("-fx-font-size: 14px;");

        Button acquitButton = new Button("Acquitter");
        Button removeButton = new Button("Supprimer");
        Button acquitAllButton = new Button("Acquitter tout");
        Button removeAcquittedButton = new Button("Supprimer acquittées");
        Button filterButton = new Button("Filtrer par gravité");
        HBox actions = new HBox(10, acquitButton, removeButton);

        root.setTop(new VBox(10, title, actions));
        root.setCenter(alertList);
        root.setBottom(detailsLabel);
        BorderPane.setMargin(detailsLabel, new Insets(10, 0, 0, 0));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Label alertCount = new Label("Total alertes: " + alertes.size());

        Runnable rebuild = () -> {
            alertList.setItems(FXCollections.observableArrayList(alertes));
            alertCount.setText("Total alertes: " + alertes.size());
        };

        HBox topBox = new HBox(10, acquitButton, removeButton, acquitAllButton, removeAcquittedButton, filterButton, alertCount);
        topBox.setPadding(new Insets(6));
        root.setTop(new VBox(10, title, topBox));

        alertList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                detailsLabel.setText("Sélectionnez une alerte pour voir les détails.");
                return;
            }
            Releve releve = newSelection.getReleve();
            detailsLabel.setText(
                    "Capteur : " + releve.getCapteur().getCode() + "\n" +
                            "Date : " + releve.getDateHeure().format(formatter) + "\n" +
                            "Valeur : " + releve.getValeur() + "\n" +
                            "Niveau : " + newSelection.getNiveau() + "\n" +
                            "Message : " + newSelection.getObjet() + "\n" +
                            "Statut : " + (newSelection.isAcquittee() ? "Acquittée" : "Active")
            );
        });

        acquitButton.setOnAction(e -> {
            Alerte selected = alertList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.acquitter();
                alertList.refresh();
                rebuild.run();
                // persist changes
                CapteursView.sauvegarderAlertesCSV("TP/csv/alertes.csv", alertes);
            }
        });

        removeButton.setOnAction(e -> {
            Alerte selected = alertList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.supprimer();
                alertes.remove(selected);
                rebuild.run();
                detailsLabel.setText("Aucune alerte sélectionnée.");
                CapteursView.sauvegarderAlertesCSV("TP/csv/alertes.csv", alertes);
            }
        });

        acquitAllButton.setOnAction(e -> {
            for (Alerte a : new ArrayList<>(alertes)) a.acquitter();
            rebuild.run();
        });

        removeAcquittedButton.setOnAction(e -> {
            alertes.removeIf(a -> a.isAcquittee());
            rebuild.run();
        });

        filterButton.setOnAction(e -> {
            List<String> opts = new ArrayList<>();
            for (Gravite g : Gravite.values()) opts.add(g.toString());
            ChoiceDialog<String> cd = new ChoiceDialog<>(opts.get(0), opts);
            cd.setTitle("Filtrer"); cd.setHeaderText("Choisir gravité");
            Optional<String> pick = cd.showAndWait();
            pick.ifPresent(p -> {
                Gravite gsel = Gravite.valueOf(p);
                List<Alerte> filtered = new ArrayList<>();
                for (Alerte a : alertes) if (a.getNiveau() == gsel) filtered.add(a);
                alertList.setItems(FXCollections.observableArrayList(filtered));
                alertCount.setText("Affichage: " + filtered.size() + " / " + alertes.size());
            });
        });

        return root;
    }
}
