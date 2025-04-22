package org.erbr.screen;

import org.erbr.ias.ChatGPT;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenMain extends JFrame {

    private JTextArea chatArea;
    private JPanel imagePanel;
    private boolean plusPressed = false;
    private boolean minusPressed = false;

    public ScreenMain() {
        setTitle("Euler Resolved");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JButton printBtn = new JButton("🖼️ Tirar Print da Tela");
        printBtn.setFont(new Font("Arial", Font.BOLD, 14));
        printBtn.setBackground(Color.LIGHT_GRAY);
        printBtn.addActionListener(e -> iniciarSelecaoPrint());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(220, 180));
        imagePanel.setBorder(BorderFactory.createTitledBorder("Último print"));

        add(printBtn, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(imagePanel, BorderLayout.EAST);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyChar() == '+') plusPressed = true;
                if (e.getKeyChar() == '-') minusPressed = true;
                if (plusPressed && minusPressed) {
                    iniciarSelecaoPrint();
                    plusPressed = false;
                    minusPressed = false;
                }
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (e.getKeyChar() == '+') plusPressed = false;
                if (e.getKeyChar() == '-') minusPressed = false;
            }
            return false;
        });

        setVisible(true);
    }

    private void iniciarSelecaoPrint() {
        setVisible(false);
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {}

        new AreaSelector(retanguloSelecionado -> {
            capturarArea(retanguloSelecionado);
            setVisible(true);
        });
    }

    private void capturarArea(Rectangle area) {
        try {
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(area);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File file = new File("print_" + timestamp + ".png");
            ImageIO.write(image, "png", file);

            Image resizedImg = image.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(resizedImg);

            imagePanel.removeAll();
            imagePanel.add(new JLabel(imageIcon));
            imagePanel.revalidate();
            imagePanel.repaint();

            chatArea.append("🖼️ Print salvo: " + file.getName() + "\n");

            String respostaIA = ChatGPT.enviarImagemParaIA(file);

            JSONObject json = new JSONObject(respostaIA);

            String respostaIAFilter = json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            chatArea.append("🤖 IA respondeu: \n" + respostaIAFilter + "\n");

        } catch (Exception ex) {

            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao tirar print: " + ex.getMessage());

        }
    }
}
