# Batibloc
Description rapide du projet.

## Installation
Instructions pour compiler et exécuter.

## Fonctionnalités

# Application de gestion de vues PDF — Livrable 3

Ce document explique comment utiliser l’application fournie dans le fichier `equipeXX.jar`.  
Elle permet d’importer un plan PDF, de visualiser ses pages sous forme de vues, de les supprimer et de les rogner.

---

## 1. Lancement de l’application

1. Assurez-vous d’avoir **Java 25** installé sur votre machine.
2. Téléchargez le fichier `equipe07.jar` fourni à la racine du projet.
3. Double-cliquez sur le fichier **OU** lancez-le via la console :


java -jar equipe07.jar


L’interface principale de l’application s’ouvrira.

---

## 2. Importer un plan PDF

1. Cliquez sur le bouton **Importer**.
2. Sélectionnez un fichier PDF contenant une ou plusieurs pages.
3. L’application génère automatiquement une vue pour chaque page du document.

Vous verrez alors la liste des vues dans le panneau latéral ou dans la zone prévue à cet effet.

---

## 3. Visualiser les vues

- Cliquez sur une vue dans la liste pour l’afficher.
- Utilisez les contrôles de navigation pour passer d’une vue à l’autre.
- Chaque vue correspond à une page du PDF importé.


## 4. Supprimer une ou plusieurs vues

1. Sélectionnez la ou les vues à supprimer.
2. Cliquez sur **Supprimer**.
3. Les vues disparaîtront de la liste et ne seront plus affichables.

Cette action n’affecte pas le fichier PDF original.



## 5. Rogner une vue

1. Sélectionnez une vue.
2. Cliquez sur Rogner.
3. À l’aide de la zone de sélection, choisissez la portion de l’image à conserver.
4. Confirmez l’opération.

La vue est alors remplacée par sa version rognée.



## 7. Limitations connues

- L’application ne modifie jamais le PDF original.
- Les vues sont générées uniquement à partir du PDF importé.
- Le rognage s’applique uniquement à la vue sélectionnée.



## 8. Informations techniques

- Fonctionne avec 25
- Le contrôleur respecte les contraintes du projet :
  - ne retourne pas d’objets complexes du domaine,
  - ne reçoit pas d’objets complexes dans ses méthodes publiques,
  - communique uniquement via des DTO simples.


## 9. Équipe

- Équipe 07 — IFT‑2007 / GLO‑2004  
Université Laval — Hiver 2026


