package org.erbr.screen;

import org.erbr.db.SQLiteConnection;
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
    private String apiKeyGPT = "";
    private String apiKeyDeepSeek = "";
    private String apiKeyGemini = "";
    private String apiKeyGrok = "";
    private File ultimoPrint = null;
    private boolean plusPressed = false;
    private boolean minusPressed = false;

    public ScreenMain() {
        // Cria a tabela caso não exista
        SQLiteConnection.createTable();

        // Carrega a chave da API do banco de dados
        apiKeyGPT = SQLiteConnection.getApiKeys()[0];
        apiKeyDeepSeek = SQLiteConnection.getApiKeys()[1];

        setTitle("Euler Resolved");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Botão de Configurações
        JButton configBtn = new JButton("⚙️ Configurações");
        configBtn.setFont(new Font("Arial", Font.BOLD, 14));
        configBtn.setBackground(Color.LIGHT_GRAY);
        configBtn.addActionListener(e -> OpenSettings());

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

    private void abrirConfiguracoes() {
        JDialog configDialog = new JDialog(this, "⚙️ Configurações", true);
        configDialog.setSize(500, 300);
        configDialog.setLayout(new BorderLayout());
        configDialog.setLocationRelativeTo(this);

        JPanel painelCampos = new JPanel(new BorderLayout(10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelChaves = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel labelApiGpt = new JLabel("🔑 API GPT:");
        JTextField campoGpt = new JTextField(apiKeyGPT);

        JLabel labelApiDeep = new JLabel("🔑 API DeepSeek:");
        JTextField campoDeep = new JTextField(apiKeyDeepSeek);

        JLabel labelApiGemini = new JLabel("🔑 API Gemini:");
        JTextField campoGemini = new JTextField(apiKeyGemini);

        JLabel labelApiGrok = new JLabel("🔑 API Grok:");
        JTextField campoGrok = new JTextField(apiKeyGrok);

        painelChaves.add(labelApiGpt); painelChaves.add(campoGpt);
        painelChaves.add(labelApiDeep); painelChaves.add(campoDeep);
        painelChaves.add(labelApiGemini); painelChaves.add(campoGemini);
        painelChaves.add(labelApiGrok); painelChaves.add(campoGrok);

        painelCampos.add(painelChaves, BorderLayout.CENTER);

        JButton salvarBtn = new JButton("💾 Salvar");
        salvarBtn.addActionListener(e -> {
            apiKeyGPT = campoGpt.getText().trim();
            apiKeyDeepSeek = campoDeep.getText().trim();
            apiKeyGemini = campoGemini.getText().trim();
            apiKeyGrok = campoGrok.getText().trim();

            SQLiteConnection.saveApiKeys(apiKeyGPT, apiKeyDeepSeek, apiKeyGemini, apiKeyGrok);
            chatArea.append("✅ Chaves de API salvas!\n");
            configDialog.dispose();
        });

        configDialog.add(painelCampos, BorderLayout.CENTER);
        configDialog.add(salvarBtn, BorderLayout.SOUTH);
        configDialog.setVisible(true);
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

            if (apiKeyGPT.isEmpty()) {
                chatArea.append("⚠️ Chave da API não definida!\n");
                return;
            }

            String respostaIa = ChatGPT.enviarImagemParaIA(novoArquivo, apiKeyGPT);
            JSONObject json = new JSONObject(respostaIa);

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
