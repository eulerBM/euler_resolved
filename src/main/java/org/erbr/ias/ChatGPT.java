package org.erbr.ias;

import org.erbr.db.SQLiteConnection;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

public class ChatGPT {

    private static final String API_KEY = SQLiteConnection.getApiKeys()[0];
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static String enviarImagemParaIA(File imagem, String chave) throws IOException {
        // Converter imagem em base64
        byte[] imageBytes = Files.readAllBytes(imagem.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        System.out.println(API_KEY);

        // Montar JSON para GPT-4 Vision
        String jsonInput = """
        {
          "model": "gpt-4o-mini",
          "messages": [
            {
              "role": "user",
              "content": [
                {
                  "type": "text",
                  "text": "Resolva a questão que está sendo proposta para você:"
                },
                {
                  "type": "image_url",
                  "image_url": {
                    "url": "data:image/png;base64,%s"
                  }
                }
              ]
            }
          ],
          "max_tokens": 1000
        }
        """.formatted(base64Image);

        // Conexão HTTP
        URL url = new URL(API_URL);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("POST");
        conexao.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conexao.setRequestProperty("Content-Type", "application/json");
        conexao.setDoOutput(true);

        try (OutputStream os = conexao.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conexao.getInputStream(), "utf-8"))) {
            StringBuilder resposta = new StringBuilder();
            String linha;
            while ((linha = br.readLine()) != null) {
                resposta.append(linha.trim());
            }
            return resposta.toString();
        }
    }
}
