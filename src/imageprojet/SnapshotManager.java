package imageprojet;

import java.io.*;
import java.util.*;

public class SnapshotManager {
    private final ImageManager imageManager;

    public SnapshotManager() {
        this.imageManager = new ImageManager();
    }

    public void saveSnapshot(String cheminDossier, String cheminSnapshot) {
        File dossier = new File(cheminDossier);
        if (!dossier.exists() || !dossier.isDirectory()) {
            System.out.println("Erreur : Le chemin spécifié n'est pas un répertoire valide.");
            return;
        }

        ArrayList<File> fichiers = new ArrayList<>();
        imageManager.parcourirDossier(dossier, fichiers);

        // Construire le fichier snapshot dans le répertoire spécifié
        File snapshotFile = new File(cheminDossier, "snapshot.json");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(snapshotFile))) {
            writer.write("{");
            writer.newLine();
            for (int i = 0; i < fichiers.size(); i++) {
                File fichier = fichiers.get(i);
                writer.write("  \"" + fichier.getAbsolutePath().replace("\\", "\\\\") + "\": {");
                writer.newLine();
                writer.write("    \"size\": " + fichier.length() + ",");
                writer.newLine();
                writer.write("    \"lastModified\": " + fichier.lastModified());
                writer.newLine();

                if (i < fichiers.size() - 1) {
                    writer.write("  },");
                } else {
                    writer.write("  }");
                }
                writer.newLine();
            }
            writer.write("}");
            System.out.println("Snapshot sauvegardé avec succès dans : " + snapshotFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde du snapshot : " + e.getMessage());
        }
    }


    public void compareSnapshots(String cheminDossier, String cheminSnapshot) {
        File dossier = new File(cheminDossier);
        if (!dossier.exists() || !dossier.isDirectory()) {
            System.out.println("Erreur : Le chemin spécifié n'est pas un répertoire valide.");
            return;
        }

        File snapshotFile = new File(cheminSnapshot);
        if (!snapshotFile.exists()) {
            System.out.println("Erreur : Le fichier snapshot spécifié n'existe pas.");
            return;
        }

        Map<String, Map<String, Long>> snapshotData = chargerSnapshot(snapshotFile);
        if (snapshotData == null || snapshotData.isEmpty()) {
            System.out.println("Erreur : Impossible de lire le fichier snapshot.");
            return;
        }

        Map<String, Map<String, Long>> etatActuel = chargerEtatActuel(dossier);

        System.out.println("Comparaison des snapshots :");
        System.out.println("------------------------");

        int nouveauxFichiers = 0;
        int fichiersSupprimes = 0;
        int fichiersModifies = 0;

        // Nouveaux fichiers
        for (String chemin : etatActuel.keySet()) {
            if (!snapshotData.containsKey(chemin)) {
                System.out.println("Nouveau fichier : " + chemin);
                nouveauxFichiers++;
            }
        }

        // Fichiers supprimés
        for (String chemin : snapshotData.keySet()) {
            if (!etatActuel.containsKey(chemin)) {
                System.out.println("Fichier supprimé : " + chemin);
                fichiersSupprimes++;
            }
        }

        // Fichiers modifiés
        for (String chemin : etatActuel.keySet()) {
            if (snapshotData.containsKey(chemin)) {
                Map<String, Long> ancienInfo = snapshotData.get(chemin);
                Map<String, Long> nouvelInfo = etatActuel.get(chemin);

                if (!ancienInfo.equals(nouvelInfo)) {
                    System.out.println("Fichier modifié : " + chemin);
                    fichiersModifies++;
                }
            }
        }

        System.out.println("------------------------");
        System.out.println("Résumé :");
        System.out.println("  Nouveaux fichiers : " + nouveauxFichiers);
        System.out.println("  Fichiers supprimés : " + fichiersSupprimes);
        System.out.println("  Fichiers modifiés : " + fichiersModifies);
    }


    private Map<String, Map<String, Long>> chargerSnapshot(File snapshotFile) {
        Map<String, Map<String, Long>> snapshotData = new HashMap<>();
        StringBuilder jsonContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(snapshotFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier snapshot : " + e.getMessage());
            return snapshotData;
        }

        String content = jsonContent.toString();
        try {
            content = content.trim();
            if (content.startsWith("{")) {
                content = content.substring(1);
            }
            if (content.endsWith("}")) {
                content = content.substring(0, content.length() - 1);
            }

            String[] entries = content.split("},");
            for (String entry : entries) {
                entry = entry.trim();
                if (entry.isEmpty()) continue;

                int pathStart = entry.indexOf("\"");
                int pathEnd = entry.indexOf("\":", pathStart + 1);
                if (pathStart == -1 || pathEnd == -1) continue;

                String path = entry.substring(pathStart + 1, pathEnd)
                                 .replace("\\\\", "\\");

                String[] lines = entry.split("\n");
                long size = 0;
                long lastModified = 0;

                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("\"size\":")) {
                        size = Long.parseLong(line.split(":")[1].replace(",", "").trim());
                    } else if (line.startsWith("\"lastModified\":")) {
                        lastModified = Long.parseLong(line.split(":")[1].replace(",", "").trim());
                    }
                }

                snapshotData.put(path, Map.of("size", size, "lastModified", lastModified));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du parsing du snapshot : " + e.getMessage());
        }

        return snapshotData;
    }

    private Map<String, Map<String, Long>> chargerEtatActuel(File dossier) {
        ArrayList<File> fichiersActuels = new ArrayList<>();
        imageManager.parcourirDossier(dossier, fichiersActuels);

        Map<String, Map<String, Long>> etatActuel = new HashMap<>();
        for (File fichier : fichiersActuels) {
            Map<String, Long> fileData = new HashMap<>();
            fileData.put("size", fichier.length());
            fileData.put("lastModified", fichier.lastModified());
            etatActuel.put(fichier.getAbsolutePath(), fileData);
        }
        return etatActuel;
    }
}
