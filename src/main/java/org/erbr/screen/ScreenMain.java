package org.erbr.screen;

import org.erbr.db.SQLiteConnection;
import org.erbr.ias.MainIa;
import org.erbr.screen.ScreenSettings;
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
    private File ultimoPrint = null;
    private boolean plusPressed = false;
    private boolean minusPressed = false;
    private static String[] apiKeysIas = SQLiteConnection.getApiKeys();

    public ScreenMain() {
        // Cria a tabela caso não exista
        SQLiteConnection.createTable();

        setTitle("Euler Resolved");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Botão de Configurações
        JButton configBtn = new JButton("⚙️ Configurações");
        configBtn.setFont(new Font("Arial", Font.BOLD, 14));
        configBtn.setBackground(Color.LIGHT_GRAY);
        configBtn.addActionListener(e -> ScreenSettings.open(this, chatArea, apiKeysIas));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(220, 180));
        imagePanel.setBorder(BorderFactory.createTitledBorder("Último print"));

        add(configBtn, BorderLayout.NORTH);
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
            File pastaPrints = new File("prints");
            if (!pastaPrints.exists()) {
                pastaPrints.mkdirs();
            }

            File[] arquivos = pastaPrints.listFiles();
            if (arquivos != null) {
                for (File arq : arquivos) {
                    arq.delete();
                }
            }

            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(area);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File novoArquivo = new File(pastaPrints, "print_" + timestamp + ".png");
            ImageIO.write(image, "png", novoArquivo);

            ultimoPrint = novoArquivo;

            Image resizedImg = image.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(resizedImg);

            imagePanel.removeAll();
            imagePanel.add(new JLabel(imageIcon));
            imagePanel.revalidate();
            imagePanel.repaint();

            String respostaIAFilter = MainIa.analyzeImage(novoArquivo);

            chatArea.append("🤖 IA respondeu: \n" + respostaIAFilter + "\n");

        } catch (Exception ex) {

            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());

        }
    }
}
