# BâtiBloc

Application de bureau Java qui aide à préparer la construction de façades à partir de plans PDF : on importe le plan, on y délimite des zones, et l'application simule la pose de blocs isolants pour estimer leur **nombre** et leur **coût**.

> Projet de session réalisé en équipe de 5 dans le cours *Analyse et conception des systèmes orientés objets* (IFT-2007 / GLO-2004), Université Laval, hiver 2026. Publié avec l'accord du professeur.

---

<img width="980" height="579" alt="image" src="https://github.com/user-attachments/assets/5a0abdf7-8274-4593-98b4-8e7dc6bda5e1" />

---

## Fonctionnalités

- **Importation de plans PDF** : chaque page devient une vue distincte (Apache PDFBox).
- **Gestion des vues** : navigation, ajout, suppression et exportation PDF d'une ou plusieurs vues, avec sélection par miniatures.
- **Rognage** d'une vue à la souris pour isoler la portion utile du plan.
- **Zones** de trois types (armature classique, armature en blocs, ouverture) et trois formes (rectangle, triangle, triangle tronqué).
- **Édition interactive des zones** : création, sélection, déplacement, redimensionnement et suppression à la souris.
- **Calibration de l'échelle** pour relier les pixels du plan aux dimensions réelles.
- **Simulation** du placement des blocs selon leurs dimensions et leur coût unitaire, avec affichage du nombre total de blocs et du coût total.
- **Zoom**, et **annuler / rétablir** basé sur le patron Memento.
- **Confirmation avant fermeture** lorsque des modifications ne sont pas enregistrées.

## Technologies

- Java 25, Swing, interface modernisée avec FlatLaf
- Apache PDFBox pour la lecture et l'export des PDF
- Maven
- Modélisation UML (Visual Paradigm, fichier `equipe07.vpp`)

## Architecture

Le code suit une séparation entre domaine et interface, avec un contrôleur unique comme point d'entrée :

```
src/main/java/BatiBloc/
├── Domaine/
│   ├── Controleur/   MainController : façade entre l'interface et le domaine
│   ├── Modeles/      Vue, Zone, Forme, Bloc, Echelle…
│   ├── Services/     simulation, géométrie, import/export, undo/redo, interactions…
│   ├── Dto/          objets de transfert vers l'interface
│   └── Constantes/
└── Vues/             fenêtres et panneaux Swing
```

L'interface ne manipule jamais directement les objets du domaine : elle passe par le `MainController` et reçoit des **DTO**.

## Ma contribution

Mes principales réalisations dans ce projet (Colombe Diallo) :

- **Module des zones** : création à la souris des zones rectangle, triangle et triangle tronqué, et intégration des zones dans le modèle (`Zone`, `Vue`, `TypeZone`, `TypeForme`, `ZoneDTO`, gestion dans le contrôleur).
- **Interactions sur les zones** (`DrawingPanel`) : modes dessin et déplacement, sélection, déplacement, suppression, et redimensionnement par **poignées** sur les sommets de la forme.
- **Restructuration du contrôleur principal** (`MainController`) : réorganisation du code, passage des coordonnées des zones en `Point2D.Double` pour plus de précision, et clarification des responsabilités entre le contrôleur et `ZoneInteractionService`.
- **Exportation PDF** qui intègre les zones dessinées sur chaque vue (rendu des zones en couleurs semi-transparentes selon leur type).
- **Suppression d'une ou plusieurs vues.**
- **Corrections du rognage** (désactivation du mode dessin pendant le rognage, affichage de la vue rognée).
- **Fermeture sécurisée** : confirmation lorsque des modifications ne sont pas enregistrées, avec messages traduits en français et en anglais.
- Intégration de mon travail avec celui de l'équipe (fusions et résolution de conflits Git).

## Lancer l'application

**Prérequis :** Java 25 ou plus récent.

Avec le JAR fourni à la racine :

```bash
java -jar equipe07.jar
```

Ou en compilant avec Maven :

```bash
mvn clean package
java -jar target/BatiBloc-1.0-SNAPSHOT.jar
```

## Limites connues

- La simulation sur les formes non rectangulaires et la prise en compte complète des ouvertures pourraient être raffinées.
- La réutilisation des chutes de blocs n'est pas prise en compte dans le calcul du coût.

## Équipe 07

- Nicky-Davis Ndayishimiye
- William Gauthier
- Emmanuella Iris Andréa Lehe
- Eliazardson Nehemie Robert
- Dickel Iman Colombe Diallo
