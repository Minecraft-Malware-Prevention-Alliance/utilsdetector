package info.mmpa.utilsdetector.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Network {
    public static File downloadTemp(String link) throws IOException {
        File tempFile = File.createTempFile("weirdutils-detector", "json");
        InputStream inputStream = new URL(link).openStream();
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
        }
        return tempFile;
    }

    public static void sendSample(Path sample) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://liftoff.mmpa.info").openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        OutputStream output = connection.getOutputStream();
        Files.copy(sample, output);
        connection.getResponseCode();
    }
}
