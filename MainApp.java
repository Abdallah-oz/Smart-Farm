package TP;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Button btnZones = new Button("🌱 Zones");
        Button btnAnimaux = new Button("🐄 Animaux");
        Button btnCapteurs = new Button("📡 Capteurs");
        Button btnAlertes = new Button("🔔 Alertes");

        btnZones.setMaxWidth(Double.MAX_VALUE);
        btnAnimaux.setMaxWidth(Double.MAX_VALUE);
        btnCapteurs.setMaxWidth(Double.MAX_VALUE);
        btnAlertes.setMaxWidth(Double.MAX_VALUE);

        btnZones.setStyle("-fx-text-fill: white;");
        btnAnimaux.setStyle("-fx-text-fill: white;");
        btnCapteurs.setStyle("-fx-text-fill: white;");
        btnAlertes.setStyle("-fx-text-fill: white;");

        HBox navigation = new HBox(10, btnZones, btnAnimaux, btnCapteurs, btnAlertes);
        navigation.setPadding(new Insets(12));
        navigation.setStyle("-fx-background-color: #2d6a2e;");
        HBox.setHgrow(btnZones, Priority.ALWAYS);
        HBox.setHgrow(btnAnimaux, Priority.ALWAYS);
        HBox.setHgrow(btnCapteurs, Priority.ALWAYS);
        HBox.setHgrow(btnAlertes, Priority.ALWAYS);

        root.setTop(navigation);

        root.setCenter(ZonesView.getView());

        btnZones.setOnAction(e -> root.setCenter(ZonesView.getView()));
        btnAnimaux.setOnAction(e -> root.setCenter(AnimauxView.getView()));
        btnCapteurs.setOnAction(e -> root.setCenter(CapteursView.getView()));
        btnAlertes.setOnAction(e -> root.setCenter(AlertesView.getView(CapteursView.getAlertes())));

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setTitle("Gestion Agricole - Ferme Intelligente");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
