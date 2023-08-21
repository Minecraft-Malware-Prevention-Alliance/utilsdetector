package info.mmpa.utilsdetector;

import com.formdev.flatlaf.FlatLightLaf;
import info.mmpa.utilsdetector.gui.MainFrame;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new MainFrame();
        });
    }
}