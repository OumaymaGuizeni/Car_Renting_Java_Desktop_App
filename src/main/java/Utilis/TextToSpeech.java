package Utilis;
import java.io.*;

public class TextToSpeech {
    public void speak(String text) {
        try {
            // Command to run espeak
            String command = "espeak -v en+f3 -s 150 -p 50 -a 200 \"" + text + "\"";
            // Use ProcessBuilder to execute the command
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}