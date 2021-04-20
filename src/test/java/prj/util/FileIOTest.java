package prj.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class FileIOTest {

    @Test
    public void resourceFileToStringTest() throws IOException {
        String fileString = FileIO.resourceFileToString("problems/Problem1.java");

        assertEquals("public class Main {\n\n    public static void main(String[] args) {\n        System.out.println(\"Hello World!\");\n    }\n}", fileString);
    }

    @Test
    public void resourceFileToStringExceptionTest() {
        assertThrows(IOException.class,
                () -> FileIO.resourceFileToString("noSuchFile.java"));
    }

    @Test
    public void writeFileForCompilationTest() throws IOException {
        FileIO.writeFileForCompilation("test");

        // Read the file
        File exerciseFile = new File("exerciseCompilation/Main.java");
        FileInputStream fis = new FileInputStream(exerciseFile);
        byte[] data = new byte[(int) exerciseFile.length()];

        fis.read(data);
        fis.close();

        assertEquals("test", new String(data, StandardCharsets.UTF_8));
    }

}