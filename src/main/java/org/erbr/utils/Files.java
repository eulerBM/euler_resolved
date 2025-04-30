package org.erbr.utils;

import java.io.File;

public class Files {

    public static void delete(File pasta){

        File[] arquivos = pasta.listFiles();

        assert arquivos != null;
        for (File arquivo : arquivos) {

            arquivo.delete();

        }

    }
}
