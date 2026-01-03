# 📸 Java Image & Metadata Manager

**Projet Universitaire - Licence d'Informatique (L2)**
Réalisé par : **Steven BASKARA** & **Zakariya GOARA**

## 📝 Présentation
Ce logiciel a été développé dans le cadre du cours de Programmation Orientée Objet (POO) à CY Cergy Paris Université. Il permet de manipuler des fichiers images (PNG, JPEG, WEBP) et d'extraire leurs métadonnées techniques via une interface graphique (Swing) ou une ligne de commande (CLI).

## ✨ Fonctionnalités principales
- **Exploration et Listage** : Identification automatique des fichiers images dans un répertoire et affichage de leurs informations de base.
- **Analyse des Métadonnées** : Extraction des données EXIF (résolution, date) et GPS (coordonnées décimales et DMS).
- **Gestion de Snapshots** : Sauvegarde de l'état d'un répertoire pour détecter les fichiers ajoutés, modifiés ou supprimés lors de comparaisons ultérieures.
- **Affichage et Recherche** : Visualisation des images, de leurs miniatures et recherche multicritères par nom, date ou dimensions.

## 🛠️ Technologies utilisées
- **Langage** : Java SE 21.
- **Bibliothèques externes** : 
  - `MetadataExtractor 2.19.0` : Pour l'extraction des données EXIF et GPS.
  - `XmpCore 6.1.11` : Pour la gestion des propriétés XMP.
- **Environnement de développement** : Eclipse IDE.

## 🚀 Installation et Utilisation
### Prérequis
Les bibliothèques présentes dans le dossier `/lib` doivent être ajoutées au Classpath du projet pour permettre l'extraction des métadonnées.

### Exécution (Mode Console)
L'interface CLI permet d'interagir avec l'application via des arguments spécifiques.
```bash
# Lister les images d'un dossier
java -jar cli.jar -d "chemin/vers/repertoire" --list

# Afficher les métadonnées détaillées d'un fichier
java -jar cli.jar -f "chemin/image.jpg" -i
```

### Exécution (Mode Graphique)
Le mode GUI offre une navigation intuitive via des onglets dédiés pour lister, analyser et comparer les fichiers.
```bash
java -jar gui.jar
```

### 📊 Structure du Projet

- **Cli.java** : Analyse les arguments et gère les interactions en ligne de commande.

- **Gui.java** : Propose une interface utilisateur avec panneaux interactifs (Swing).

- **ImageManager.java** : Contient les méthodes principales pour parcourir les répertoires et extraire les métadonnées.

- **SnapshotManager.java** : Gère la création et la comparaison des versions de répertoires.

- **SearchImage.java** : Implémente la logique de recherche avancée par critères.

