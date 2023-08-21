package info.mmpa.utilsdetector.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
}
