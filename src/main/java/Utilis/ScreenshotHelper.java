package Utilis;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import Utilis.LogManager;

public class ScreenshotHelper {

    public static void captureNode(Node node, String filePath) {
        // Get logger instance 
        LogManager logger = LogManager.getInstance();
        WritableImage image = node.snapshot(null, null);
        File file = new File(filePath);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            // System.out.println("Screenshot saved: " + file.getAbsolutePath());
            logger.info("Screenshot saved in : " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
