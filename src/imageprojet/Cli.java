package imageprojet;

import java.io.File;

public class Cli {

    private final ImageManager imageManager;
    private final SearchImage searchImage;
    private final SnapshotManager snapshotManager;

    public Cli() {
        this.imageManager = new ImageManager();
        this.searchImage = new SearchImage();
        this.snapshotManager = new SnapshotManager();
    }

    public static void main(String[] args) {
        Cli cli = new Cli();
        cli.traiterArguments(args);
    }

    public void traiterArguments(String[] args) {
        String cheminRepertoire = null;
        String cheminFichier = null;
        boolean afficherAide = false;
        boolean afficherStatistiques = false;
        boolean effectuerRecherche = false;
        boolean afficherInfosFichier = false;
        boolean snapshotSave = false;
        String snapshotCompare = null;
        boolean listerImages = false;

        String nom = null;
        String date = null;
        String dimensions = null;
        boolean validName = false;
        boolean validDate = false;
        boolean validDimensions = false;

        try {
            // Parcours des arguments
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-h":
                    case "--help":
                        afficherAide = true;
                        break;
                    case "-d":
                    case "--directory":
                        if (i + 1 < args.length) {
                            cheminRepertoire = args[++i];
                        } else {
                            throw new IllegalArgumentException("Erreur : Veuillez fournir un chemin valide pour le répertoire après -d ou --directory.");
                        }
                        break;
                    case "--list":
                        listerImages = true;
                        break;
                    case "-f":
                    case "--file":
                        if (i + 1 < args.length) {
                            cheminFichier = args[++i];
                        } else {
                            throw new IllegalArgumentException("Erreur : Veuillez fournir un chemin valide pour le fichier après -f ou --file.");
                        }
                        break;
                    case "--stat":
                        afficherStatistiques = true;
                        break;
                    case "-i":
                    case "--info":
                        afficherInfosFichier = true;
                        break;
                    case "--snapshotsave":
                        snapshotSave = true;
                        break;
                    case "--snapshotcompare":
                        if (i + 1 < args.length) {
                            snapshotCompare = args[++i];
                        } else {
                            throw new IllegalArgumentException("Erreur : Veuillez fournir un fichier snapshot pour comparer après --snapshotcompare.");
                        }
                        break;
                    case "--search":
                        effectuerRecherche = true;
                        if (i + 1 < args.length) {
                            cheminRepertoire = args[++i];
                        } else {
                            throw new IllegalArgumentException("Erreur : Veuillez fournir un chemin pour le répertoire à rechercher après --search.");
                        }
                        break;
                    default:
                        String arg = args[i];
                        if (arg.contains("/") && Character.isDigit(arg.charAt(0))) {
                            date = arg;
                            validDate = true;
                        } else if (arg.contains("x") && Character.isDigit(arg.charAt(0))) {
                            dimensions = arg;
                            validDimensions = true;
                        } else if (!arg.startsWith("-")) {
                            nom = arg;
                            validName = true;
                        } else {
                            throw new IllegalArgumentException("Erreur : Option inconnue '" + arg + "'. Utilisez -h ou --help pour voir les options disponibles.");
                        }
                }
            }

            // Validation des options
            if (afficherAide) {
                afficherAide();
                return;
            }

            if (cheminFichier != null) {
                File fichier = new File(cheminFichier);
                if (!fichier.exists() || !fichier.isFile()) {
                    throw new IllegalArgumentException("Erreur : Le fichier spécifié n'existe pas ou n'est pas un fichier valide : " + cheminFichier);
                }
                if (afficherInfosFichier && afficherStatistiques) {
                    imageManager.afficherInfoFic(cheminFichier);
                    System.out.println(); 
                    imageManager.afficherStatFic(cheminFichier);
                } else if (afficherInfosFichier) {
                    imageManager.afficherInfoFic(cheminFichier);
                } else if (afficherStatistiques) {
                    imageManager.afficherStatFic(cheminFichier);
                } else {
                    throw new IllegalArgumentException("Erreur : Veuillez spécifier une action pour le fichier (ex : -i ou --stat).");
                }
                return;
            }

            if (cheminRepertoire != null) {
                File dir = new File(cheminRepertoire);
                if (!dir.exists() || !dir.isDirectory()) {
                    throw new IllegalArgumentException("Erreur : Le répertoire spécifié n'existe pas ou n'est pas un répertoire valide : " + cheminRepertoire);
                }

                if (snapshotSave) {
                    snapshotManager.saveSnapshot(cheminRepertoire, "snapshot.json");
                    return;
                }
                if (snapshotCompare != null) {
                    File snapshotFile = new File(snapshotCompare);
                    if (!snapshotFile.exists() || !snapshotFile.isFile()) {
                        throw new IllegalArgumentException("Erreur : Le fichier snapshot spécifié n'existe pas : " + snapshotCompare);
                    }
                    snapshotManager.compareSnapshots(cheminRepertoire, snapshotCompare);
                    return;
                }
                if (effectuerRecherche) {
                    searchImage.rechercherImages(cheminRepertoire, nom, date, dimensions, validName, validDate, validDimensions);
                } else if (afficherStatistiques) {
                    imageManager.calculerStatistiquesDossier(cheminRepertoire);
                } else if (listerImages) {
                    imageManager.listerImages(cheminRepertoire);
                } else {
                    throw new IllegalArgumentException("Erreur : Veuillez spécifier une commande comme --list, --stat, --snapshotsave ou --snapshotcompare.");
                }
                return;
            }

            throw new IllegalArgumentException("Erreur : Aucun chemin valide fourni. Utilisez -h ou --help pour voir les commandes disponibles.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur inattendue : " + e.getMessage());
        }
    }


    public void afficherAide() {
        System.out.println("Options :");
        System.out.println("  -h, --help                  Affiche cette aide");
        System.out.println("  -d, --directory <chemin>    Analyse un répertoire");
        System.out.println("      --list                  Liste les images dans le répertoire");
        System.out.println("      --stat                  Affiche les statistiques du répertoire");
        System.out.println("  -f, --file <chemin>         Analyse un fichier spécifique");
        System.out.println("      --stat                  Affiche les statistiques du fichier");
        System.out.println("      -i, --info              Affiche les métadonnées du fichier");
        System.out.println("  --snapshotsave              Sauvegarde un snapshot du répertoire");
        System.out.println("  --snapshotcompare <fichier> Compare l'état d'un répertoire avec un snapshot");
        System.out.println("  --search <chemin>           Recherche dans un répertoire avec des critères");
    }
}
