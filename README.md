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

Le logiciel permet :

- d’importer des vues à partir d’un document PDF ;
- de naviguer entre les vues ;
- de rogner une façade ;
- de définir différentes zones de travail ;
- de modifier l’échelle ;
- de manipuler les zones à la souris ;
- de configurer les dimensions des blocs ;
- de lancer une simulation de placement de blocs ;
- d’estimer le nombre total de blocs et le coût total.

Dans le cadre du **livrable 5**, ce README décrit l’état actuel de l’application, les fonctionnalités intégrées, les limites connues ainsi que les consignes de compilation et d’exécution.

---

## Fonctionnalités réalisées

### 1. Interface graphique modernisée
L’application utilise une interface `NewMainWindow` avec une apparence modernisée basée sur **FlatLaf**.

L’interface comprend :

- une **barre supérieure** pour les actions principales ;
- un **panneau latéral** pour la gestion des zones, des blocs et de la simulation ;
- un **panneau central** pour l’affichage de la vue active ;
- un système de **miniatures** pour certaines opérations sur les vues.

### 2. Importation de plans PDF
L’utilisateur peut importer un document PDF contenant une ou plusieurs pages.

Chaque page importée devient une **vue distincte** dans l’application, ce qui permet de travailler façade par façade ou page par page selon le contexte du plan.

### 3. Gestion des vues
L’application permet de :

- naviguer entre les vues ;
- ajouter une vue à partir de la vue courante ;
- supprimer une ou plusieurs vues ;
- exporter une ou plusieurs vues sélectionnées au format PDF.

La suppression et l’exportation peuvent utiliser une interface par **miniatures** afin de faciliter la sélection multiple.

### 4. Rognage d’une vue
Une vue peut être rognée directement à l’écran à l’aide d’un rectangle de sélection.

Cette fonctionnalité permet d’isoler uniquement la portion pertinente du plan avant d’ajouter des zones ou de lancer une simulation.

### 5. Gestion des zones
L’utilisateur peut créer différents types de zones.

#### Types de zones
- armature classique ;
- armature en blocs ;
- ouverture.

#### Formes disponibles
- rectangle ;
- triangle ;
- triangle tronqué.

Les zones sont affichées directement dans la vue et peuvent être utilisées comme base pour les opérations de simulation ou d’édition.

### 6. Manipulation interactive des zones
Les zones peuvent être :

- sélectionnées ;
- déplacées ;
- redimensionnées ;
- supprimées.

L’application met visuellement en évidence la zone sélectionnée afin de rendre l’édition plus claire.

### 7. Calibration et modification de l’échelle
L’échelle d’une vue peut être définie ou modifiée directement dans l’interface.

Cette valeur est utilisée pour relier les dimensions de l’image aux dimensions réelles nécessaires aux traitements et à la simulation.

### 8. Paramétrage des blocs
L’utilisateur peut modifier :

- la largeur du bloc ;
- la hauteur du bloc ;
- le coût unitaire du bloc.

Ces paramètres sont utilisés lors de la simulation.

### 9. Simulation du placement des blocs
L’application permet de lancer une simulation sur une zone admissible afin d’estimer :

- le **nombre total de blocs** ;
- le **coût total** selon le prix unitaire saisi.

La simulation tient compte des dimensions configurées pour les blocs et s’appuie sur le modèle interne des zones.

### 10. Zoom et navigation visuelle
L’interface intègre des boutons de **zoom avant** et **zoom arrière** afin d’améliorer le confort de manipulation.

Le zoom agit sur l’affichage et n’altère pas l’échelle réelle du plan.

### 11. Undo / Redo
L’application prend en charge un mécanisme de type **memento** permettant :

- d’annuler la dernière modification ;
- de rétablir une modification annulée.

Cette fonctionnalité s’applique aux opérations importantes sur les vues et les zones.

---

## Limites connues

Les éléments suivants doivent être considérés comme des limites ou des points de vigilance dans l’état actuel de l’application.

### 1. Simulation avancée des formes non rectangulaires
La prise en charge des zones triangulaires et triangulaires tronquées dépend de la logique géométrique implantée dans le modèle.

Selon l’état final du code remis, certaines validations ou adaptations supplémentaires peuvent encore être nécessaires pour garantir une simulation parfaitement cohérente sur toutes les formes.

### 2. Gestion complète des ouvertures dans la simulation
Les ouvertures peuvent être représentées comme type de zone, mais leur prise en charge complète dans la simulation peut nécessiter des règles supplémentaires selon le comportement attendu dans le projet.

### 3. Cohérence du JAR exécutable
Comme dans tout projet Java avec génération Maven, il est recommandé de tester systématiquement le **JAR final** avant la remise, afin de vérifier qu’il reproduit bien le comportement observé dans l’environnement de développement.

### 4. Raffinement de certains calculs métier
Certaines améliorations futures peuvent concerner :

- la distinction entre blocs complets et morceaux réutilisés ;
- le calcul précis du coût dans les cas de réutilisation de chutes ;
- l’ajustement de la logique de simulation selon toutes les formes supportées.

---

## Instructions de démonstration

Cette section propose un scénario de démonstration générique pour le **livrable 5**.  
Il peut être ajusté selon les consignes officielles de remise ou de présentation.

### Scénario recommandé

1. **Lancer l’application**.
2. **Importer un PDF** contenant une ou plusieurs pages.
3. **Naviguer entre les vues** importées.
4. **Rogner** une vue afin de conserver uniquement la façade ou la portion utile.
5. **Définir ou modifier l’échelle** de la vue.
6. **Choisir un type de zone** puis une **forme** dans le panneau latéral.
7. **Dessiner la zone** directement dans la vue.
8. **Sélectionner la zone**, puis la **déplacer** ou la **redimensionner** au besoin.
9. **Entrer les dimensions des blocs** ainsi que le **coût unitaire**.
10. **Lancer la simulation**.
11. **Vérifier l’affichage** du nombre total de blocs et du coût total.
12. Utiliser le **zoom** pour inspecter un détail de la vue.
13. **Supprimer** ou **modifier** une zone si nécessaire.
14. **Exporter** ou **supprimer plusieurs vues** à l’aide du panneau de miniatures.

### Points importants à montrer pendant la démo
Il est recommandé de mettre en évidence :

- la fluidité de l’importation PDF ;
- le fonctionnement du rognage ;
- la gestion des différents types de zones ;
- les interactions souris sur les zones ;
- la modification de l’échelle ;
- la configuration des blocs ;
- la simulation et l’affichage des résultats ;
- le mécanisme d’annulation et de rétablissement.

---

## Compilation / Exécution

### Prérequis
- **Java 25** ou version supérieure
- **Maven** pour la compilation

### Compilation du projet
À partir de la racine du dépôt :

```bash
mvn clean package
