package info.mmpa.utilsdetector;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import info.mmpa.utilsdetector.gui.MainFrame;

import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LafManager.install(new DarculaTheme());
            new MainFrame();
        });
    }
}