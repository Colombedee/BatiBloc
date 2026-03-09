# BâtiBloc
 
Application de simulation de construction de bâtiments avec blocs isolants de type LEGO.  
 
Projet réalisé par l'équipe 07 — Session Hiver 2026  
 
Nicky-Davis NDAYISHIMIYE 
William GAUTHIER 
Emmanuella Iris Andréa LEHE 
Eliazardson Nehemie ROBERT 
Dickel Iman Colombe DIALLO
 
Cours : Analyse et conception des systèmes orientés objets (IFT-2007 / GLO-2004)  
Université Laval
---
 
## Prérequis
 
- **Java 25** ou version supérieure
- Le fichier `BatiBloc-1.0-SNAPSHOT.jar` (disponible dans le dossier `target/` après compilation)
 
---
 
## Lancer l'application
 
### Option 1 — Double-clic
Double-cliquez sur le fichier `BatiBloc-1.0-SNAPSHOT.jar`.
 
### Option 2 — Ligne de commande
```bash
java -jar BatiBloc-1.0-SNAPSHOT.jar
```
 
---
 
## Fonctionnalités
 
### 1. Importer un plan PDF
1. Cliquez sur le bouton **"Importer"** dans la barre du haut.
2. Sélectionnez un fichier **PDF** (plusieurs pages supportées).
3. Le plan s'affiche dans la zone de visualisation à droite.
 
### 2. Naviguer entre les vues
- Cliquez sur **`>`** pour passer à la vue suivante.
- Cliquez sur **`<`** pour revenir à la vue précédente.
- Chaque page du PDF correspond à une vue distincte.
 
### 3. Supprimer une vue
1. Cliquez sur **"Supprimer"** — les vues s'affichent en miniature en bas de l'écran.
2. Cochez les cases des vues à supprimer, ou cliquez sur **"Tout sélectionner"**.
3. Confirmez avec **"Confirmer"** ou annulez avec **"Annuler"**.
 
### 4. Rogner une vue
1. Naviguez jusqu'à la vue à rogner.
2. Cliquez sur **"Rogner"** — le bouton affiche **"Rognage actif"**.
3. Cliquez sur la vue, maintenez et glissez pour dessiner un rectangle autour de la zone à conserver.
4. Relâchez la souris — la vue est mise à jour automatiquement et le mode rognage se désactive.
 
### 5. Changer la langue
- Cliquez sur le bouton **"FR"** / **"EN"** en haut à droite pour basculer entre le français et l'anglais.


