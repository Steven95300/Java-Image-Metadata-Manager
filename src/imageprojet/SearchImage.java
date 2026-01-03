package imageprojet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchImage {

    private final ImageManager imageManager;

    public SearchImage() {
        this.imageManager = new ImageManager();
    }

    public void rechercherImages(String cheminDossier, String nom, String date, String dimensions, boolean validName, boolean validDate, boolean validDimensions) {
        File dossier = new File(cheminDossier);
        if (!dossier.exists() || !dossier.isDirectory()) {
            System.out.println("Erreur : Le chemin spécifié n'est pas un répertoire valide.");
            return;
        }

        ArrayList<File> fichiers = new ArrayList<>();
        imageManager.parcourirDossier(dossier, fichiers);

        ArrayList<String> resultats = new ArrayList<>();
        for (int i = 0; i < fichiers.size(); i++) {
            File fichier = fichiers.get(i);
            boolean valide = true;

            if (validName && !fichier.getName().toLowerCase().contains(nom.toLowerCase())) {
                valide = false;
            }
            if (validDate && valide) {
                valide = estFichierDeDate(fichier, date);
            }
            if (validDimensions && valide) {
                valide = estFichierDeDimensions(fichier, dimensions);
            }

            if (valide) {
                resultats.add(fichier.getName());
            }
        }

        afficherResultats(resultats);
    }

    private boolean estFichierDeDate(File fichier, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateRecherchee = sdf.parse(date);
            Date dateFichier = new Date(fichier.lastModified());
            return sdf.format(dateRecherchee).equals(sdf.format(dateFichier));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean estFichierDeDimensions(File fichier, String dimensions) {
        String dims = ImageManager.extraireDimensions(fichier);
        return dims.equals(dimensions);
    }

    private void afficherResultats(ArrayList<String> resultats) {
        if (resultats.isEmpty()) {
            System.out.println("Aucun fichier trouvé correspondant aux critères.");
        } else {
            System.out.println("Fichiers correspondant aux critères :");
            for (int i = 0; i < resultats.size(); i++) {
                System.out.println("- " + resultats.get(i));
            }
        }
    }
}
