package org.erbr;

import javax.swing.SwingUtilities;
import org.erbr.screen.ScreenMain;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScreenMain::new);
    }

}