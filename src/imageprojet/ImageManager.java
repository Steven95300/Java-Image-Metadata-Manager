package imageprojet;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import com.drew.imaging.ImageMetadataReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageManager {

    public void parcourirDossier(File dossier, ArrayList<File> fichiers) {
        File[] contenu = dossier.listFiles();
        if (contenu == null) return;

        for (int i = 0; i < contenu.length; i++) {
            File fichier = contenu[i];
            if (fichier.isHidden()) continue;
            if (fichier.isDirectory()) {
                parcourirDossier(fichier, fichiers);
            } else if (fichier.isFile() && verifierMimeType(fichier)) {
                fichiers.add(fichier);
            }
        }
    }

    public void listerImages(String cheminDossier) {
        File dossier = new File(cheminDossier);
        if (!dossier.exists() || !dossier.isDirectory()) {
            System.out.println("Erreur : Le chemin spécifié n'est pas un répertoire valide.");
            return;
        }

        ArrayList<File> fichiers = new ArrayList<>();
        parcourirDossier(dossier, fichiers);

        ArrayList<String> listeImages = new ArrayList<>();
        for (int i = 0; i < fichiers.size(); i++) {
            File fichier = fichiers.get(i);
            if (estImage(fichier)) {
                listeImages.add(fichier.getName());
            }
        }

        if (listeImages.isEmpty()) {
            System.out.println("Aucune image trouvée dans le répertoire : " + cheminDossier);
        } else {
            System.out.println("Images trouvées dans le répertoire :");
            for (int i = 0; i < listeImages.size(); i++) {
                System.out.println("- " + listeImages.get(i));
            }
        }
    }

    public static boolean estImage(File fichier) {
        String nom = fichier.getName().toLowerCase();
        boolean extensionValide = nom.endsWith(".png") || nom.endsWith(".jpeg") || nom.endsWith(".jpg") || nom.endsWith(".webp");
        return extensionValide && verifierMimeType(fichier);
    }



    public static String extraireDimensions(File fichier) {
        if (!fichier.exists() || !fichier.isFile()) {
            return "Fichier non valide ou introuvable.";
        }

        try {
            // Extraction des métadonnées avec MetadataExtractor
            Metadata metadata = ImageMetadataReader.readMetadata(fichier);

            // 1. Vérifier pour JPEG ou JPG
            Directory jpegDirectory = metadata.getFirstDirectoryOfType(com.drew.metadata.jpeg.JpegDirectory.class);
            if (jpegDirectory != null) {
                Integer largeur = jpegDirectory.getInteger(com.drew.metadata.jpeg.JpegDirectory.TAG_IMAGE_WIDTH);
                Integer hauteur = jpegDirectory.getInteger(com.drew.metadata.jpeg.JpegDirectory.TAG_IMAGE_HEIGHT);
                if (largeur != null && hauteur != null && largeur > 0 && hauteur > 0) {
                    return largeur + "x" + hauteur;
                }
            }

            // 2. Vérifier pour PNG ou WEBP avec EXIF
            ExifSubIFDDirectory exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifDirectory != null) {
                Integer largeur = exifDirectory.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
                Integer hauteur = exifDirectory.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
                if (largeur != null && hauteur != null && largeur > 0 && hauteur > 0) {
                    return largeur + "x" + hauteur;
                }
            }

            // 3. Utilisation de ImageIO comme solution de secours
            BufferedImage image = ImageIO.read(fichier);
            if (image != null) {
                return image.getWidth() + "x" + image.getHeight();
            }

        } catch (Exception e) {
            // Gestion d'erreur : Retourne le message d'erreur sans interrompre le programme
            System.err.println("Erreur lors de l'extraction des dimensions : " + e.getMessage());
        }

        // Retour par défaut si aucune méthode ne fonctionne
        return "Dimensions non disponibles.";
    }


    public void calculerStatistiquesDossier(String cheminRepertoire) {
        File dossier = new File(cheminRepertoire);

        if (!dossier.exists() || !dossier.isDirectory()) {
            System.out.println("Erreur : Le chemin spécifié n'est pas un répertoire valide.");
            return;
        }

        ArrayList<File> fichiers = new ArrayList<>();
        parcourirDossier(dossier, fichiers);

        int totalFichiers = 0;
        int totalImages = 0;
        int nombrePng = 0;
        int nombreJpeg = 0;
        int nombreJpg = 0;
        int nombreWebp = 0;
        long tailleTotaleImages = 0;
        File fichierPlusAncien = null;
        File fichierPlusRecent = null;

        for (int i = 0; i < fichiers.size(); i++) {
            File fichier = fichiers.get(i);
            totalFichiers++;

            if (fichierPlusAncien == null || fichier.lastModified() < fichierPlusAncien.lastModified()) {
                fichierPlusAncien = fichier;
            }
            if (fichierPlusRecent == null || fichier.lastModified() > fichierPlusRecent.lastModified()) {
                fichierPlusRecent = fichier;
            }

            if (estImage(fichier)) { 
                totalImages++;
                tailleTotaleImages += fichier.length();

                String extension = fichier.getName().substring(fichier.getName().lastIndexOf('.') + 1).toLowerCase();
                switch (extension) {
                    case "png":
                        nombrePng++;
                        break;
                    case "jpeg":
                        nombreJpeg++;
                        break;
                    case "jpg":
                        nombreJpg++;
                        break;
                    case "webp":
                        nombreWebp++;
                        break;
                }
            }
        }

        System.out.println("Statistiques du répertoire : " + cheminRepertoire);
        System.out.println("- Total de fichiers : " + totalFichiers);
        System.out.println("- Total d'images : " + totalImages);
        System.out.println("- Répartition par format :");
        System.out.println("  png : " + nombrePng);
        System.out.println("  jpeg : " + nombreJpeg);
        System.out.println("  jpg : " + nombreJpg);
        System.out.println("  webp : " + nombreWebp);
        System.out.println("- Taille totale des images : " + String.format("%.2f", tailleTotaleImages / (1024.0 * 1024.0)) + " Mo");
        if (fichierPlusAncien != null) {
            System.out.println("- Fichier le plus ancien : " + fichierPlusAncien.getName() + " (" + new java.util.Date(fichierPlusAncien.lastModified()) + ")");
        }
        if (fichierPlusRecent != null) {
            System.out.println("- Fichier le plus récent : " + fichierPlusRecent.getName() + " (" + new java.util.Date(fichierPlusRecent.lastModified()) + ")");
        }
    }
    
    public ArrayList<String> getImagesFromDirectory(String cheminDossier) {
        ArrayList<String> listeImages = new ArrayList<>();
        File dossier = new File(cheminDossier);
        if (dossier.exists() && dossier.isDirectory()) {
            File[] fichiers = dossier.listFiles();
            if (fichiers != null) {
                for (File fichier : fichiers) {
                    if (fichier.isFile() && ImageManager.estImage(fichier)) {
                        listeImages.add(fichier.getName());
                    }
                }
            }
        }
        return listeImages;
    }

    public void afficherStatFic(String cheminFichier) {
        File fichier = new File(cheminFichier);

        if (!fichier.exists() || !fichier.isFile()) {
            System.out.println("Erreur : Le chemin spécifié n'est pas un fichier valide.");
            return;
        }

        System.out.println("=== Statistiques pour le fichier : " + fichier.getName() + " ===");
        System.out.println("- Taille : " + fichier.length() + " octets");
        System.out.println("- Date de dernière modification : " + new java.util.Date(fichier.lastModified()));

        String mimeType = verifierMimeType(fichier) ? "Image valide" : "Non supporté";
        System.out.println("- Type MIME : " + mimeType);

        String dimensions = extraireDimensions(fichier);
        if (dimensions != null) {
            System.out.println("- Dimensions : " + dimensions);
        } else {
            System.out.println("- Dimensions : Impossible de les extraire.");
        }
    }

    private static boolean verifierMimeType(File fichier) {
        try {
            String mimeType = Files.probeContentType(fichier.toPath());
            return mimeType != null && (mimeType.equals("image/jpeg") || mimeType.equals("image/png") || mimeType.equals("image/webp"));
        } catch (Exception e) {
            return false;
        }
    }


    public void afficherInfoFic(String cheminFichier) {
        File fichier = new File(cheminFichier);

        if (!fichier.exists() || !fichier.isFile()) {
            System.out.println("Erreur : Le chemin spécifié n'est pas un fichier valide.");
            return;
        }

        System.out.println("=== Informations détaillées pour le fichier : " + fichier.getName() + " ===");

        // Taille du fichier
        long tailleFichier = fichier.length();
        System.out.println("Taille du fichier : " + String.format("%.2f", tailleFichier / 1024.0) + " Ko");

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(fichier);

            // Dimensions
            String dimensions = extraireDimensions(fichier);
            System.out.println("Dimensions : " + (dimensions != null ? dimensions : "Non disponibles"));

            // Vérification de la présence d'une miniature
            ExifThumbnailDirectory thumbnailDirectory = metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class);
            if (thumbnailDirectory != null) {
                Integer thumbnailLength = thumbnailDirectory.getInteger(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH);
                if (thumbnailLength != null && thumbnailLength > 0) {
                    System.out.println("Miniature : Disponible");
                } else {
                    System.out.println("Miniature : Non disponible");
                }
            } else {
                System.out.println("Miniature : Non disponible");
            }

         // Extraction des informations XMP : Titre et description
            XmpDirectory xmpDirectory = metadata.getFirstDirectoryOfType(XmpDirectory.class);
            if (xmpDirectory != null) {
                Map<String, String> xmpProperties = xmpDirectory.getXmpProperties();
                if (xmpProperties.containsKey("dc:description")) {
                    System.out.println("Description : " + xmpProperties.get("dc:description"));
                } else {
                    System.out.println("Description : Non disponible");
                }
            } else {
                System.out.println("Description : Non disponible");
            }

            // Vérification pour les descriptions EXIF (alternative possible)
            Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifDirectory != null) {
                String imageDescription = exifDirectory.getString(ExifSubIFDDirectory.TAG_IMAGE_DESCRIPTION);
                if (imageDescription != null) {
                    System.out.println("Description (EXIF) : " + imageDescription);
                }
            }



            // Autres métadonnées (comme GPS)
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null && gpsDirectory.getGeoLocation() != null) {
                double latitude = gpsDirectory.getGeoLocation().getLatitude();
                double longitude = gpsDirectory.getGeoLocation().getLongitude();

                System.out.println("--- Localisation GPS ---");
                System.out.println("Format décimal :");
                System.out.println("    Latitude : " + latitude);
                System.out.println("    Longitude : " + longitude);
                System.out.println("Format DMS (Degrés, Minutes, Secondes) :");
                System.out.println("    Latitude : " + convertirEnDMS(latitude, "N", "S"));
                System.out.println("    Longitude : " + convertirEnDMS(longitude, "E", "W"));
            } else {
                System.out.println("Aucune information GPS disponible.");
            }


        } catch (Exception e) {
            System.out.println("Erreur lors de l'extraction des informations : " + e.getMessage());
        }
    }



    public static String convertirEnDMS(double coord, String positif, String negatif) {
        String direction = coord >= 0 ? positif : negatif;
        if (coord < 0) coord = -coord;
        int degres = (int) coord;
        double minutesDouble = (coord - degres) * 60;
        int minutes = (int) minutesDouble;
        double secondes = (minutesDouble - minutes) * 60;
        return String.format("%d°%d'%.2f\" %s", degres, minutes, secondes, direction);
    }
    
    public String getImagesAsText(String cheminDossier) {
        File dossier = new File(cheminDossier);
        if (!dossier.exists() || !dossier.isDirectory()) {
            return "Erreur : Le répertoire spécifié n'est pas valide.";
        }

        ArrayList<File> fichiers = new ArrayList<>();
        parcourirDossier(dossier, fichiers);

        if (fichiers.isEmpty()) {
            return "Aucune image trouvée dans le répertoire.";
        }

        StringBuilder result = new StringBuilder("Images trouvées dans le répertoire :\n");
        for (File fichier : fichiers) {
            if (estImage(fichier)) {
                result.append("- ").append(fichier.getName()).append("\n");
            }
        }

        return result.toString();
    }


}
