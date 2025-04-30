package org.erbr.utils;

import java.awt.image.BufferedImage;
import java.io.File;

public class CaptureResult {
    private File file;
    private BufferedImage image;

    public CaptureResult(File file, BufferedImage image) {
        this.file = file;
        this.image = image;
    }

    public File getFile() {
        return file;
    }

    public BufferedImage getImage() {
        return image;
    }
}
