package prj.runner;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RunnerTest {

    @Test
    public void successfulCallTest() {
        Runner runner = new Runner("public class Main {\n \n public static void main(String[] args) {\n System.out.println(\"Hello World!\");\n }\n }\n");

        Map<String, String> result = runner.call();

        assertEquals("success", result.get("type"));
    }

    @Test
    public void errorCallTest() {
        Runner runner = new Runner("publi class Main {\n \n public static void main(String[] args) {\n System.out.println(\"Hello World!\");\n }\n }\n");

        Map<String, String> result = runner.call();

        assertEquals("error", result.get("type"));
        assertEquals("Error on line 1: class, interface, or enum expected\n", result.get("message"));
    }

    @Test
    public void exceptionCallTest() {
        Runner runner = new Runner("public class Main {\n \n public static void test(String[] args) {\n System.out.println(\"Hello World!\");\n }\n }\n");

        Map<String, String> result = runner.call();

        assertEquals("error", result.get("type"));
        assertEquals("Internal Server Error", result.get("message"));
    }
}