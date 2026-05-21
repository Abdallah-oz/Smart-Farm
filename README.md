# Smart Farming — Guide d'utilisation (TP POO, ESI Alger 2CP)

## 1. Introduction
Ce projet Java « Smart Farming » modélise une petite ferme intelligente : zones (culture, élevage, aquacole), cultures, animaux, capteurs, relevés et système d'alertes. Ce guide pratique explique comment charger les données, utiliser les principales fonctionnalités et exécuter les tests.

> Pré-requis : Java 8+ et le dossier `TP` présent dans le répertoire courant.

---

## 2. Charger les données (obligatoire avant tout)
1. Créez les fichiers CSV (format exact ci-dessous) et placez-les dans `TP/`.

- `zones.csv` (exemples) :
```
CULTURE,ZC01,Zone Ble
ELEVAGE,ZE01,Zone Vaches,RUMINANT,10.0,10.0
AQUACOLE,ZA01,Zone Poissons,Saumon
```

- `cultures.csv` :
```
ZC01,CEREALE,2026-05-20,2026-11-20,7.0
ZC01,LEGUME,2026-05-20,2026-08-20,6.5
ZC01,FRUIT,2026-05-20,2027-01-20,6.0
```

- `animaux.csv` :
```
RUMINANT,ZE01,R001,Bessie,Vache,5,600.0,SAIN
RUMINANT,ZE01,R002,Daisy,Vache Jersey,4,450.0,SAIN
VOLAILLE,ZE01,V001,Coco,Poule Rousse,2,2.5,SAIN
```

- `capteurs.csv` :
```
MESURE,CM01,ZC01,6.0,7.5,pH,PH_SOL
MESURE,CM02,ZE01,15.0,35.0,°C,TEMPERATURE
GPS,GPS01,ZE01,0.0,15.0,5.0,5.0
GPS,GPS02,ZE01,0.0,15.0,50.0,50.0
```

2. Charger les fichiers depuis le code :

```java
import TP.Chargeur;
import TP.Zone;
import java.util.List;

List<Zone> zones = Chargeur.chargerZones("TP/zones.csv");
Chargeur.chargerCultures("TP/cultures.csv", zones);
Chargeur.chargerAnimaux("TP/animaux.csv", zones);
Chargeur.chargerCapteurs("TP/capteurs.csv", zones);
```

3. Récupérer chaque zone après chargement :

```java
ZoneCulture zoneCulture = null;
ZoneElevage zoneElevage = null;
ZoneAquacole zoneAquacole = null;
for (Zone z : zones) {
    if (z instanceof ZoneCulture) zoneCulture = (ZoneCulture) z;
    else if (z instanceof ZoneElevage) zoneElevage = (ZoneElevage) z;
    else if (z instanceof ZoneAquacole) zoneAquacole = (ZoneAquacole) z;
}
```

Assurez-vous d'appeler le chargement avant d'utiliser les zones/animaux/capteurs.

---

## 3. Fonctionnalité 1 — Gérer les zones
- Ajouter une zone (manuellement) :

```java
ZoneCulture newZone = new ZoneCulture("ZC99", "Nouvelle Zone");
// (ou via Chargeur.chargerZones)
```

- Changer le statut d'une zone :

```java
zone.changerStatut(Statut.SUSPENDUE);
// pour réactiver :
zone.changerStatut(Statut.ACTIVE);
```

- Afficher la vue d'ensemble :

```java
zone.afficherVueEnsemble();
```

- Afficher les capteurs d'une zone :

```java
zone.afficherCapteurs();
```

Exemple complet :

```java
// zoneCulture déjà chargée
zoneCulture.afficherVueEnsemble();
zoneCulture.afficherCapteurs();
zoneCulture.changerStatut(Statut.SUSPENDUE);
```

---

## 4. Fonctionnalité 2 — Gérer les cultures
- Ajouter une culture :

```java
Culture c = new Culture(FamilleCulture.CEREALE, LocalDateTime.now(), LocalDateTime.now().plusMonths(6), 7.0);
zoneCulture.ajouterCulture(c);
```

- Mettre à jour le stade :

```java
culture.mettreAJourStade(StadeCroissance.RECOLTE);
```

- Enregistrer le rendement :

```java
culture.enregistrerRendement(950.0);
```

- Générer le rapport de la zone :

```java
zoneCulture.rapportCulture();
```

Exemple complet :

```java
Culture c2 = new Culture(FamilleCulture.LEGUME, LocalDateTime.now(), LocalDateTime.now().plusMonths(3), 6.5);
zoneCulture.ajouterCulture(c2);
c2.mettreAJourStade(StadeCroissance.GERMINATION);
zoneCulture.rapportCulture();
```

---

## 5. Fonctionnalité 3 — Gérer les animaux
- Ajouter un animal :

```java
Ruminant r = new Ruminant("R010", "Nom", "Vache", 4, 420.0, EtatSante.SAIN);
zoneElevage.ajouterAnimal(r);
```

- Enregistrer une production (lait / œufs) :

```java
r.enregistrerProdLait(25.5);
// pour volaille
v.enregistrerProdOeufs(280);
```

- Changer l'état de santé d'un animal :

```java
animal.changerEtatSante(EtatSante.MALADE, 590.0);
```

- Afficher l'historique sanitaire d'un animal :

```java
animal.afficherHistorique();
```

- Définir un programme d'alimentation pour la zone :

```java
ProgrammeAlimentation prog = new ProgrammeAlimentation(zoneElevage, "Foin", 50.0);
zoneElevage.definirProgramme(prog);
zoneElevage.afficherProgramme();
```

Exemple complet :

```java
Ruminant r1 = new Ruminant("R001", "Bessie", "Vache", 5, 600.0, EtatSante.SAIN);
zoneElevage.ajouterAnimal(r1);
r1.enregistrerProdLait(25.5);
r1.changerEtatSante(EtatSante.MALADE, 590.0);
r1.afficherHistorique();
```

---

## 6. Fonctionnalité 4 — Gérer les capteurs
- Créer un capteur (il s'enregistre automatiquement dans `GestionnaireCapteurs` dans le constructeur) :

```java
CapteurMesure cm = new CapteurMesure("CM01", zoneCulture, 6.0, 7.5, "pH", TypeMesure.PH_SOL);
CapteurGPS gps = new CapteurGPS("GPS01", zoneElevage, 0.0, 15.0);
gps.setLatitude(5.0); gps.setLongitude(5.0);
```

- Changer le statut du capteur :

```java
cm.desactiver();
// pour réactiver, la méthode disponible est `activer()`
cm.activer();
```

- Envoyer une mesure (retourne `Alerte` ou `null`) :

```java
Alerte a = cm.envoyerMesure(8.5);
if (a == null) System.out.println("Mesure normale"); else a.afficher();
```

- Envoyer une position GPS :

```java
Alerte ag = gps.envoyerPosition();
if (ag == null) System.out.println("Position OK"); else ag.afficher();
```

- Afficher le tableau de bord des relevés :

```java
TableauDeBord.afficherTableauDeBord(GestionnaireCapteurs.getCapteurs());
```

- Consulter l'historique d'un capteur :

```java
GestionnaireHistorique.afficherHistorique(cm);
// Filtrer par date (LocalDateTime debut, LocalDateTime fin)
GestionnaireHistorique.afficherHistorique(cm, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
```

- Obtenir les capteurs d'une zone :

```java
List<Capteur> capteursZone = GestionnaireCapteurs.getCapteursByZone(zoneElevage);
```

Exemple complet :

```java
CapteurMesure cm02 = new CapteurMesure("CM02", zoneElevage, 15.0, 35.0, "°C", TypeMesure.TEMPERATURE);
cm02.afficher();
Alerte a2 = cm02.envoyerMesure(40.0);
if (a2 != null) a2.afficher();
```

---

## 7. Fonctionnalité 5 — Gérer les alertes
- Les alertes sont générées automatiquement par `envoyerMesure()` (capteurs de mesure) et `envoyerPosition()` (capteurs GPS) lorsqu'un relevé dépasse les seuils.

- Vérifier et afficher :

```java
if (alerte != null) alerte.afficher();
```

- Acquitter une alerte :

```java
alerte.acquitter();
```

- Supprimer (méthode simple d'affichage actuellement) :

```java
alerte.supprimer();
```

- Utiliser le `GestionnaireAlerte` pour analyser les alertes :

```java
GestionnaireAlerte ga = new GestionnaireAlerte();
// ga.ajouterAlerte(alerte) si vous voulez construire l'historique manuellement
ga.afficherPanneauAlertes();
ga.afficherAlertesCritiques();
```

---

## 8. Compilation et exécution
Depuis le dossier racine (contenant `TP/`) :

```bash
javac TP/*.java
java TP.Test
```

Le fichier `TP/Test.java` contient des scénarios de test. Assurez-vous d'avoir créé et rempli les CSV avant d'exécuter si la version du `Test` en cours effectue le chargement.

---

## Remarques
- Les exemples de code montrent les usages de base ; adaptez-les à vos besoins.
- Les parsers CSV sont simples : évitez les lignes vides et gardez le format exact.
- Si la méthode `activer()` ou d'autres signatures évoluent, mettez à jour les appels correspondants dans vos tests.

---

ESI Alger — 2CP (2025/2026)
Smart Farming — TP POO
