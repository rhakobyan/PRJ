package jits.util;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileIO {

    public static String resourceFileToString(String path) throws IOException {
        File file = new ClassPathResource(path).getFile();
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];

        fis.read(data);
        fis.close();

        return new String(data, StandardCharsets.UTF_8);
    }

    public static void writeFileForCompilation(String code) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("exerciseCompilation/Main.java"));
        writer.write(code);
        writer.close();
    }
}
