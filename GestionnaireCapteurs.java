package TP;

import java.util.List;
import java.util.ArrayList;

public class GestionnaireCapteurs {
    private static List<Capteur> capteurs = new ArrayList<>();

    public static void ajouterCapteur(Capteur capteur) {
        capteurs.add(capteur);
    }

    public static List<Capteur> getCapteurs() {
        return capteurs;
    }
}
