# BâtiBloc

Application de simulation de construction de bâtiments à partir de plans PDF, avec gestion de vues, définition de zones et simulation de placement de blocs.

## Équipe 07 — Session Hiver 2026

- **Nicky-Davis NDAYISHIMIYE**  
- **William GAUTHIER**  
- **Emmanuella Iris Andréa LEHE**  
- **Eliazardson Nehemie ROBERT**  
- **Dickel Iman Colombe DIALLO**  

**Cours :** Analyse et conception des systèmes orientés objets (IFT-2007 / GLO-2004)  
**Université Laval**

---

## Description du projet

**BâtiBloc** est une application Java conçue pour assister la préparation de façades à partir de plans importés en PDF.  
Le logiciel permet d’importer des vues, de les rogner, d’y définir des zones de travail de différentes formes, d’ajuster l’échelle, puis de lancer une simulation de placement de blocs selon des dimensions configurables.

Le livrable 4 vise notamment l’import PDF, la gestion des vues, l’édition de zones, le rognage, la calibration de l’échelle, la simulation de placement, le calcul du nombre de blocs, le zoom, ainsi que l’utilisation d’une interface claire sans fenêtres superflues. 
---

## Fonctionnalités réalisées

### 1. Interface graphique modernisée
L’application utilise une nouvelle interface `NewMainWindow` avec une apparence modernisée basée sur **FlatLaf**.  
L’interface comprend :

- une **barre supérieure** pour les actions globales ;
- un **panneau latéral** pour les opérations sur les zones et la simulation ;
- un **panneau central** pour l’affichage de la vue active ;
- un système de **miniatures** pour la sélection multiple de vues.

### 2. Importation de plans PDF
L’utilisateur peut importer un document PDF contenant une ou plusieurs pages.  
Chaque page importée devient une **vue distincte** dans l’application, permettant de travailler façade par façade. Cette fonctionnalité fait partie des exigences du livrable 4. 

### 3. Gestion des vues
L’application permet de :

- naviguer entre les vues ;
- ajouter une vue à partir de la vue courante ;
- supprimer une ou plusieurs vues ;
- exporter une ou plusieurs vues sélectionnées au format PDF.

La suppression et l’exportation utilisent une interface par **miniatures** afin de faciliter la sélection multiple.

### 4. Rognage d’une vue
Une vue peut être rognée directement à l’écran en dessinant un rectangle de sélection.  
Cette étape permet de conserver uniquement la portion utile d’une façade avant l’ajout de zones.

### 5. Gestion des zones
L’utilisateur peut créer des zones selon :

#### Types de zones
- armature classique ;
- armature en blocs ;
- ouverture.

#### Formes disponibles
- rectangle ;
- triangle ;
- triangle tronqué.

Le livrable 4 exige la définition, l’affichage, la sélection, la modification et la suppression de zones, avec interaction à la souris.
### 6. Manipulation interactive des zones
Les zones peuvent être :

- sélectionnées ;
- déplacées ;
- redimensionnées ;
- supprimées.

L’application gère également la mise en évidence de la zone sélectionnée afin de rendre l’édition plus claire pour l’utilisateur.

### 7. Calibration et modification de l’échelle
L’échelle d’une vue peut être définie ou modifiée directement dans l’interface.  
Cette valeur est ensuite utilisée dans les traitements dépendant des dimensions réelles. La saisie et l’édition de mesures impériales font partie des attentes du livrable. 

### 8. Simulation du placement des blocs
L’application permet de lancer une simulation sur une zone admissible afin d’estimer :

- le **nombre total de blocs** ;
- le **coût total** selon un prix unitaire saisi par l’utilisateur.

Le livrable 4 demande une simulation de placement avec dimensions minimales, réutilisation des chutes et affichage du nombre de blocs. 

### 9. Paramétrage des blocs
L’utilisateur peut modifier :

- la largeur du bloc ;
- la hauteur du bloc ;
- le coût unitaire du bloc.

Ces paramètres sont pris en compte dans la simulation.

### 10. Zoom et navigation visuelle
L’interface intègre des boutons de **zoom avant** et **zoom arrière** afin d’améliorer le confort de manipulation des vues.  
Le zoom agit sur l’affichage et non sur les dimensions réelles du plan.

### 11. Undo / Redo
L’application prend en charge un mécanisme de **memento** permettant :

- d’annuler la dernière modification ;
- de rétablir une modification annulée.

Cette fonctionnalité couvre les opérations importantes sur les vues et les zones.

---

## Limites connues

Les éléments suivants doivent être considérés comme des limites ou points de vigilance dans l’état actuel du livrable :

### 1. Simulation avancée des formes non rectangulaires
La prise en charge des zones triangulaires et triangulaires tronquées dépend de la logique géométrique disponible dans le modèle.  
Une validation supplémentaire peut être nécessaire pour garantir que la largeur utile varie correctement selon la hauteur de chaque rangée.

### 2. Gestion complète des ouvertures dans la simulation
Les ouvertures font partie des types de zones attendus dans le livrable, mais la simulation complète autour des ouvertures peut nécessiter des règles additionnelles, notamment pour le linteau, les dégagements minimaux et certaines armatures traditionnelles automatiques. Ces règles sont mentionnées dans l’énoncé. 

### 3. Cohérence du JAR exécutable
Lors du développement, des écarts ont été observés entre l’exécution dans l’IDE et l’exécution à partir du JAR, notamment autour du rognage et de certains problèmes de reconstruction Maven. Un test systématique du JAR final demeure recommandé avant la remise.

### 4. Ajouts futurs possibles
Certaines validations métier plus poussées peuvent encore être raffinées, notamment :

- la comptabilisation exacte des blocs neufs versus les morceaux réutilisés ;
- le comportement détaillé de la simulation sur toutes les formes ;
- l’intégration complète de certaines règles métier propres aux ouvertures.

---

## Instructions de démonstration

Le scénario de démonstration attendu pour le livrable 4 inclut notamment : lancer le JAR, importer une page par vue, conserver uniquement les façades utiles, rogner, saisir l’échelle, ajouter des zones rectangulaires et triangulaires, déplacer/redimensionner les zones, lancer la simulation, afficher le nombre de blocs, utiliser le zoom et supprimer une zone. ### Scénario recommandé

1. **Lancer l’application**.
2. **Importer un PDF** contenant une ou plusieurs façades.
3. **Naviguer entre les vues** importées.
4. **Rogner** une vue pour conserver uniquement la façade utile.
5. **Entrer l’échelle** dans le champ prévu puis cliquer sur **Appliquer**.
6. **Choisir un type de zone** et une **forme** dans le panneau latéral.
7. **Dessiner la zone** directement dans la vue.
8. **Sélectionner la zone**, puis la **déplacer** ou la **redimensionner** au besoin.
9. **Entrer les dimensions des blocs** ainsi que le **coût unitaire**.
10. **Lancer la simulation** avec le bouton **Simuler**.
11. **Vérifier l’affichage** du nombre total de blocs et du coût total.
12. Utiliser le **zoom** pour inspecter visuellement un détail de la vue.
13. **Supprimer une zone** si nécessaire.
14. **Exporter** ou **supprimer plusieurs vues** à l’aide du panneau de miniatures.

---

## Compilation / Exécution

### Prérequis
- **Java 25** ou version supérieure
- **Maven** pour la compilation

### Compilation du projet
À partir de la racine du dépôt :

```bash
mvn clean package