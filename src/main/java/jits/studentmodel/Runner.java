package jits.studentmodel;

import jits.model.Lesson;
import jits.model.Problem;
import jits.util.FileIO;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Runner {

    public static Map<String, String> run(String code) {
        HashMap<String, String> map = new HashMap<>();
        StringBuilder message = new StringBuilder();

        try {
            FileIO.writeFileForCompilation(code);
            File exerciseFile = new File("exerciseCompilation/Main.java");
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(exerciseFile));
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

            if (!task.call()) {
                //Create the error messages to be displayed
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
                    message.append("Error on line ").append(diagnostic.getLineNumber()).append(": ").
                            append(diagnostic.getMessage(Locale.ENGLISH)).append("\n");
                map.put("type", "error");
            }
            else {
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{exerciseFile.getParentFile().toURI().toURL()});
                Class<?> loadedClass = Class.forName("Main", true, classLoader);
                Method method = loadedClass.getMethod("main", String[].class);
                String[] args = new String[0];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(out);

                // Change the output stream destination to the printStream in order to capture
                // what is being printed when executing the method
                System.setOut(printStream);
                method.invoke(null, (Object) args);  // invoke the method
                //close the stream
                printStream.flush();
                printStream.close();

                message = new StringBuilder(out.toString());
                //Reset output stream to the standard terminal
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                fileManager.close();
                map.put("type", "success");
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException exception) {
            message = new StringBuilder("Internal Server Error");
            map.put("type", "error");
            exception.printStackTrace();
        }

        map.put("message", message.toString());
        return map;
    }
}
