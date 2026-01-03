# Projet POO & Java : Images & Métadonnées  
**Année universitaire** : 2024-2025  

## **Membres du projet**
- **BASKARA** : Steven (Groupe TD : A)
- **GOARA** : Zakariya (Groupe TD : A)

---

## **Description du projet**  
Le projet consiste à développer une application en **Java** permettant de manipuler des images (formats pris en charge : PNG, JPEG, WEBP) et leurs métadonnées. Les principales fonctionnalités incluent :
- Listage des fichiers image d’un répertoire.
- Extraction de statistiques sur les fichiers image.
- Analyse des métadonnées d’un fichier image spécifique.
- Gestion et comparaison de snapshots d’un répertoire.
- Recherche d’images en fonction de critères.

---

## **Livrables**
1. **Code source** : Fichiers `.java` pour les classes :
   - `Cli.java` : Interface en ligne de commande.
   - `Gui.java` : Interface graphique utilisateur.
   - `ImageManager.java` : Gestion des images.
   - `SearchImage.java` : Recherche d’images.
   - `SnapshotManager.java` : Gestion des snapshots.
2. **Fichiers exécutables** :  
   - `cli.jar` pour l’interface en ligne de commande.  
   - `gui.jar` pour l’interface graphique.
3. **Documentation technique** : Javadoc et rapport détaillant les choix techniques, les diagrammes UML, et l'organisation du projet.
4. **Vidéo de démonstration** (mode console et graphique).

---

## **Informations spécifiques**
- **Technologies utilisées** :
  - Langage : Java SE 21.
  - Bibliothèques externes : `metadata-extractor` pour l’analyse des métadonnées.
  - IDE : Eclipse.

---

## **Comment exécuter**
### Mode Console :
1. Pour afficher l'aide et les options disponibles :
 
 - java -jar cli.jar -h