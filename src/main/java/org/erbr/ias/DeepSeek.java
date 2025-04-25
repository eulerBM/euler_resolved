package org.erbr.ias;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DeepSeek {

    public static String enviarMensagem(String apiKey, String mensagem) {
        String endpoint = "https://api.deepseek.com/v1/chat/completions";
        String modelo = "deepseek-chat"; // ou "deepseek-reasoner" conforme necessário

        try {
            // Criar a URL e abrir a conexão
            URL url = new URL(endpoint);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

            // Configurar a conexão
            conexao.setRequestMethod("POST");
            conexao.setRequestProperty("Content-Type", "application/json");
            conexao.setRequestProperty("Authorization", "Bearer " + apiKey);
            conexao.setDoOutput(true);

            // Construir o corpo da requisição
            String corpoRequisicao = String.format(
                    "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}",
                    modelo, mensagem
            );

            // Enviar a requisição
            try (OutputStream os = conexao.getOutputStream()) {
                byte[] entrada = corpoRequisicao.getBytes(StandardCharsets.UTF_8);
                os.write(entrada, 0, entrada.length);
            }

            // Ler a resposta
            int codigoResposta = conexao.getResponseCode();
            InputStream is = (codigoResposta >= 200 && codigoResposta < 300)
                    ? conexao.getInputStream()
                    : conexao.getErrorStream();

            BufferedReader leitor = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder resposta = new StringBuilder();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                resposta.append(linha.trim());
            }

            leitor.close();
            return resposta.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "Erro ao se comunicar com a API do DeepSeek: " + e.getMessage();
        }
    }
}
