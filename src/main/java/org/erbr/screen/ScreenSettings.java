package org.erbr.screen;

import org.erbr.db.SQLiteConnection;

import javax.swing.*;
import java.awt.*;

public class ScreenSettings {

    public static void open(JFrame parent, JTextArea chatArea, String apiKeysIas){
        JDialog configDialog = new JDialog(parent, "⚙️ Configurações", true);
        configDialog.setSize(500, 300);
        configDialog.setLayout(new BorderLayout());
        configDialog.setLocationRelativeTo(parent);

        JPanel painelCampos = new JPanel(new BorderLayout(10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelChaves = new JPanel(new GridLayout(4, 2, 10, 10));

        JTextField campoGpt = new JTextField(apiKeysIas);

        painelChaves.add(new JLabel("🔑 API GPT:")); painelChaves.add(campoGpt);

        painelCampos.add(painelChaves, BorderLayout.CENTER);

        JButton salvarBtn = new JButton("💾 Salvar");

        salvarBtn.addActionListener(e -> {
            SQLiteConnection.saveApiKeyGPT(
                    campoGpt.getText().trim()
            );
            chatArea.append("✅ Chaves de API salvas!\n");
            configDialog.dispose();
        });

        configDialog.add(painelCampos, BorderLayout.CENTER);
        configDialog.add(salvarBtn, BorderLayout.SOUTH);
        configDialog.setVisible(true);
    }
}
