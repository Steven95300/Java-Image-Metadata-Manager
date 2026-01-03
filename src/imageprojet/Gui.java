package imageprojet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

public class Gui {
    private final ImageManager imageManager;
    private final SnapshotManager snapshotManager;

    public Gui() {
        this.imageManager = new ImageManager();
        new SearchImage();
        this.snapshotManager = new SnapshotManager();
    }

    public void start() {
        JFrame frame = new JFrame("Image Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Lister les images", createListerPanel());
        tabbedPane.add("Statistiques répertoire", createStatistiquesPanel());
        tabbedPane.add("Recherche", createRecherchePanel());
        tabbedPane.add("Snapshot", createSnapshotPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JPanel createListerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JButton chooseDirButton = new JButton("Choisir un répertoire");
        chooseDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File dir = chooser.getSelectedFile();
                    String imagesText = imageManager.getImagesAsText(dir.getAbsolutePath());
                    resultArea.setText(imagesText);
                }
            }
        });

        panel.add(chooseDirButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatistiquesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;

        JButton chooseDirButton = new JButton("Choisir un répertoire");
        chooseDirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File dir = chooser.getSelectedFile();
                    System.setOut(ps);
                    imageManager.calculerStatistiquesDossier(dir.getAbsolutePath());
                    resultArea.setText(baos.toString());
                    baos.reset();
                    System.setOut(old);
                }
            }
        });

        panel.add(chooseDirButton, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRecherchePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        JScrollPane infoScrollPane = new JScrollPane(infoArea);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JCheckBox infoCheckBox = new JCheckBox("Afficher les informations du fichier");
        JCheckBox statCheckBox = new JCheckBox("Afficher les statistiques du fichier");

        JButton chooseFileButton = new JButton("Choisir une image");
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!infoCheckBox.isSelected() && !statCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Veuillez cocher au moins une option !");
                    return;
                }

                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(baos);
                    PrintStream old = System.out;
                    System.setOut(ps);

                    if (infoCheckBox.isSelected()) {
                        imageManager.afficherInfoFic(file.getAbsolutePath());
                    }
                    if (statCheckBox.isSelected()) {
                        imageManager.afficherStatFic(file.getAbsolutePath());
                    }

                    System.out.flush();
                    infoArea.setText(baos.toString());
                    System.setOut(old);

                    try {
                        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                        Image image = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(image));
                    } catch (Exception ex) {
                        imageLabel.setIcon(null);
                        JOptionPane.showMessageDialog(null, "Erreur lors du chargement de l'image : " + ex.getMessage());
                    }
                }
            }
        });

        JPanel optionsPanel = new JPanel(new GridLayout(3, 1));
        optionsPanel.add(infoCheckBox);
        optionsPanel.add(statCheckBox);
        optionsPanel.add(chooseFileButton);

        panel.add(optionsPanel, BorderLayout.NORTH);
        panel.add(infoScrollPane, BorderLayout.CENTER);
        panel.add(imageLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSnapshotPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;

        JButton saveSnapshotButton = new JButton("Sauvegarder un snapshot");
        saveSnapshotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File dir = chooser.getSelectedFile();

                    // Construire le chemin complet pour le fichier snapshot.json
                    File snapshotFile = new File(dir, "snapshot.json");

                    System.setOut(ps);

                    // Sauvegarder le snapshot dans le chemin complet
                    snapshotManager.saveSnapshot(dir.getAbsolutePath(), snapshotFile.getAbsolutePath());

                    resultArea.setText("Snapshot sauvegardé avec succès dans : " + snapshotFile.getAbsolutePath() + "\n");
                    baos.reset();
                    System.setOut(old);
                }
            }
        });

        JButton compareSnapshotButton = new JButton("Comparer avec un snapshot");
        compareSnapshotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int dirResult = chooser.showOpenDialog(null);
                if (dirResult == JFileChooser.APPROVE_OPTION) {
                    File dir = chooser.getSelectedFile();

                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setDialogTitle("Sélectionner le fichier snapshot à comparer");
                    int fileResult = chooser.showOpenDialog(null);
                    if (fileResult == JFileChooser.APPROVE_OPTION) {
                        File snapshotFile = chooser.getSelectedFile();
                        System.setOut(ps);
                        snapshotManager.compareSnapshots(dir.getAbsolutePath(), snapshotFile.getAbsolutePath());
                        resultArea.setText(baos.toString());
                        baos.reset();
                        System.setOut(old);
                    }
                }
            }
        });

        buttonPanel.add(saveSnapshotButton);
        buttonPanel.add(compareSnapshotButton);
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Gui().start();
            }
        });
    }
}
