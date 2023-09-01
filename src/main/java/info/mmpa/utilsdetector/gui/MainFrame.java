package info.mmpa.utilsdetector.gui;

import info.mmpa.concoction.Concoction;
import info.mmpa.concoction.input.io.archive.ArchiveLoadContext;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.scan.dynamic.DynamicScanException;
import info.mmpa.utilsdetector.utils.Network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

public class MainFrame extends JFrame {
    private static final String PROJECT_TITLE = "UtilsDetector";
    private static final String SCAN_BUTTON_TEXT = "Scan";
    private static final String LOGO_IMAGE_PATH = "mmpa-logo-transparent.png";
    private static final String SCAN_MODEL_URL = "https://minecraft-malware-prevention-alliance.github.io/meta/WeirdUtils.json";

    private JButton scanButton;
    private JTextField pathField;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle(PROJECT_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(createImageIcon(LOGO_IMAGE_PATH, "UtilsDetector Logo").getImage());

        JPanel topBarPanel = createTopBarPanel();
        JPanel mainPanel = createMainPanel();

        add(topBarPanel, BorderLayout.NORTH);
        add(mainPanel);

        pack();
        centerWindow(100 * 10, 100 * 5);
        setVisible(true);
    }

    private JPanel createTopBarPanel() {
        JPanel topBarPanel = new JPanel();
        topBarPanel.setBackground(Color.decode("#414547"));
        topBarPanel.setPreferredSize(new Dimension(getWidth(), 50));
        topBarPanel.setLayout(new BorderLayout());

        JLabel projectNameLabel = new JLabel(PROJECT_TITLE);
        projectNameLabel.setForeground(Color.WHITE);
        projectNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        projectNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topBarPanel.add(projectNameLabel, BorderLayout.WEST);

        ImageIcon logoIcon = createImageIcon(LOGO_IMAGE_PATH, "UtilsDetector Logo");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topBarPanel.add(logoLabel, BorderLayout.EAST);

        return topBarPanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.NONE;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel directoryLabel = new JLabel("Directory to scan (Game Directory)");
        pathField = createPathField();

        scanButton = createScanButton();

        mainPanel.add(directoryLabel, gbc);
        mainPanel.add(Box.createVerticalStrut(5), gbc);
        mainPanel.add(pathField, gbc);
        mainPanel.add(Box.createVerticalStrut(20), gbc);
        mainPanel.add(scanButton, gbc);
        mainPanel.add(Box.createVerticalStrut(20), gbc);

        return mainPanel;
    }

    private JTextField createPathField() {
        JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseDirectory();
            }
        });
        return textField;
    }

    private void chooseDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int chooser = fileChooser.showDialog(pathField, "Select");
        if (chooser == JFileChooser.APPROVE_OPTION) {
            pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private JButton createScanButton() {
        JButton button = new JButton(SCAN_BUTTON_TEXT);
        button.addActionListener(e -> scan());
        return button;
    }

    private void scan() {
        try {
            Path scanModelPath = Network.downloadTemp(SCAN_MODEL_URL).toPath();
            NavigableMap<Path, Results> resultsMap = Concoction.builder()
                    .addInputDirectory(ArchiveLoadContext.RANDOM_ACCESS_JAR, Path.of(pathField.getText()))
                    .addScanModel(scanModelPath)
                    .scan();

            // Process scan results and show appropriate dialog
            // ...

        } catch (IOException | DynamicScanException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void centerWindow(int width, int height) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(width, height);
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);
    }

    private static ImageIcon createImageIcon(String path, String description) {
        URL imgURL = MainFrame.class.getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
