package prj.util;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;

/*
 * The FileIO utility class is used performing file-specific operation such as reading from and writing to them.
 */
public class FileIO {

    /*
     * This method reads a resource file and returns its contents as a String.
     * @param path The path where the resource file is located.
     * @return The contents of the file as a String.
     * @throws IOException if no file is found at the requested path.
     */
    public static String resourceFileToString(String path) throws IOException {
        File file = new ClassPathResource(path).getFile();
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];

        fis.read(data);
        fis.close();

        return new String(data, StandardCharsets.UTF_8);
    }

    /*
     * Ths method writes in the exerciseCompilation/Main.java file so it can be later used for compilation.
     * @param code The code that needs to written inside the file.
     * @throws IOException if the Main.java file is not found for some reason.
     */
    public static void writeFileForCompilation(String code) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("exerciseCompilation/Main.java"));
        writer.write(code);
        writer.close();
    }
}
