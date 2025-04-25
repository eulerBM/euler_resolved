package org.erbr.ias;

import org.erbr.db.SQLiteConnection;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class MainIa {

    private static final String API_KEY_GPT = SQLiteConnection.getApiKeys()[0];


    public static String analyzeImage(File image) throws IOException {

        if (!(API_KEY_GPT == null)){

            String chatGPT = ChatGPT.enviarImagemParaIA(image);

            JSONObject json = new JSONObject(chatGPT);

            String respostaIAFilter = json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return respostaIAFilter;

        }

        return "⚠️ Chave da API não definida!\n";
    }
}
