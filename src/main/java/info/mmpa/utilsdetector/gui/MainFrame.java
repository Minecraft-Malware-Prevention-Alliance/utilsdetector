package info.mmpa.utilsdetector.gui;

import info.mmpa.concoction.Concoction;
import info.mmpa.concoction.input.io.archive.ArchiveLoadContext;
import info.mmpa.concoction.output.Results;
import info.mmpa.utilsdetector.utils.Network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("UtilsDetector");


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topBarPanel = new JPanel();
        topBarPanel.setBackground(Color.decode("#414547"));
        topBarPanel.setPreferredSize(new Dimension(getWidth(), 50));
        topBarPanel.setLayout(new BorderLayout());

        JLabel projectNameLabel = new JLabel("UtilsDetector");
        projectNameLabel.setForeground(Color.WHITE);
        projectNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        projectNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topBarPanel.add(projectNameLabel, BorderLayout.WEST);

        ImageIcon logoIcon = new ImageIcon(MainFrame.class.getClassLoader().getResource("mmpa-logo-transparent.png"));

        setIconImage(logoIcon.getImage());

        logoIcon.setImage(logoIcon.getImage().getScaledInstance(48, 48, Image.SCALE_DEFAULT));
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topBarPanel.add(logoLabel, BorderLayout.EAST);



        add(topBarPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.NONE;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(new JLabel("Directory to scan (Game Directory)"), gbc);
        mainPanel.add(Box.createVerticalStrut(5), gbc);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JTextField pathField = new JTextField();
        pathField.setEditable(false);
        pathField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (task != null) {
                    return;
                }
                int chooser = fileChooser.showDialog(pathField, "Select");
                if (chooser == JFileChooser.APPROVE_OPTION) {
                    pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        JButton scanButton = getScanButton(fileChooser);



        mainPanel.add(pathField, gbc);
        mainPanel.add(Box.createVerticalStrut(20), gbc);
        mainPanel.add(scanButton, gbc);
        mainPanel.add(Box.createVerticalStrut(20), gbc);

        add(topBarPanel, BorderLayout.NORTH);
        add(mainPanel);

        pack();

        centerWindow(100 * 10, 100 * 5);
        setVisible(true);
    }

    private Future<Void> task;

    private JButton getScanButton(JFileChooser pathField) {
        JButton scanButton = new JButton("Scan");
        scanButton.addActionListener(e -> {
            if (task == null) {
                task = startScanning(scanButton, pathField.getCurrentDirectory().toPath());
            }
        });
        return scanButton;
    }

    private Future<Void> startScanning(JButton button, Path directory) {
        return Executors.newSingleThreadExecutor().submit(() -> {
            SwingUtilities.invokeLater(() -> {
                button.setEnabled(false);
                button.setText("Scanning...");
            });
            try {
                scan(directory);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occurred:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                SwingUtilities.invokeLater(() -> {
                    button.setEnabled(true);
                    button.setText("Scan");
                });
                task = null;
            }
            return null;
        });
    }

    private static void scan(Path directory) throws Exception {
        NavigableMap<Path, Results> resultsMap = Concoction.builder()
                .addInputDirectory(ArchiveLoadContext.RANDOM_ACCESS_JAR, directory)
                .addScanModel(Network.downloadTemp("https://minecraft-malware-prevention-alliance.github.io/meta/WeirdUtils.json").toPath())
                .scan();
        List<Path> toRemove = new ArrayList<>();
        StringBuilder logs = new StringBuilder();
        resultsMap.forEach((path, results) -> {
            if (!results.isEmpty()) toRemove.add(path);
            results.forEach(detection -> {
                logs.append("Infected class found within ")
                        .append(path.toString())
                        .append(" in the class file ")
                        .append(detection.path().fullDisplay());
            });
        });
        boolean hadResults = !toRemove.isEmpty();
        List<String> options = new ArrayList<>();
        options.add("Quit");
        if (hadResults) {
            options.add("Remove");
            options.add("Remove and send samples");
        }

        int chosen = JOptionPane.showOptionDialog(
                null,
                String.format("%s You have %s infected file%s.%s",
                        hadResults ? "Oh no!" : "Great!",
                        toRemove.size(),
                        toRemove.size() != 1 ? "s" : "",
                        hadResults ? " Would you like to proceed with the removal process?" : ""
                ),
                "Your results are in!",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options.toArray(new String[0]), "");
        if (hadResults) {
            switch (chosen) {
                case 2:
                    for (Path path : toRemove) {
                        Network.sendSample(path);
                        Files.deleteIfExists(path);
                    }
                    JOptionPane.showMessageDialog(
                            null,
                            "Thanks for your contribution! Sending samples helps us identify malware families easier. We recommend doing a full-reset of your computer as deleting the files doesn't remove the already collected information to the attackers.",
                            "Sent samples and deleted files.",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    break;
                case 1:
                    for (Path path : toRemove) {
                        Files.deleteIfExists(path);
                    }
                    JOptionPane.showMessageDialog(
                            null,
                            "We have deleted the infected files in the directory you specified and its sub directories. We recommend doing a full-reset of your computer as deleting the files doesn't remove the already collected information to the attackers.",
                            "Deleted files",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                default:
                    int option = JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure to exit without any action?",
                            "Are you sure?",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (option == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
            }
        } else {
            System.exit(0);
        }
    }

    public void centerWindow(int width, int height) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(width, height);
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setBounds(x, y, width, height);
    }
}
