package org.erbr.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Capture {

    public static CaptureResult captureScreenArea(Rectangle area) throws AWTException, IOException {
        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(area);

        File directory = new File("prints");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File[] photos = directory.listFiles();

        if (photos.length > 0) {
            for (File photo : photos) {
                photo.delete();
            }
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File outputFile = new File(directory, "print_" + timestamp + ".png");
        ImageIO.write(image, "png", outputFile);

        return new CaptureResult(outputFile, image);

    }
}
