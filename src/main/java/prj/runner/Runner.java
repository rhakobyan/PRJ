package prj.runner;

import prj.util.FileIO;

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
import java.util.concurrent.Callable;

/*
 * The runner class implements the functionality of compiling and executing the user written code.
 * It implements the Callable interface so it can be created in a new thread and get called from there.
 */
public class Runner implements Callable<Map<String,String>> {
    // The user code to be executed.
    private String code;
    // The id of the thread this class is being run at.
    private long threadId;

    /*
     * The Constructor.
     * @param code The user code to be executed.
     */
    public Runner(String code) {
        this.code = code;
    }

    /*
     * The call method is called when a user-written code needs to be compiler and executed.
     * This method builds a map about the execution status of the code, which includes console messages, etc.
     * This method writes the code in a directory and using the JavaCompiler library compiler
     * it to produce a class file in the same directory.
     * DiagnosticCollector is used for getting any compilation errors which are then added to the map.
     * If the class was successfully compiler, then it is loaded and its main method is called.
     * The default output destination is changed to the printStream variable to obtain the contents of the console messages.
     * Tese messages are then added to the map.
     * @return The produced map with information about compilation and execution.
     */
    @Override
    public Map<String, String> call() {
        threadId = Thread.currentThread().getId();
        Map<String, String> map = new HashMap<>();
        StringBuilder message = new StringBuilder();

        try {
            // Write the code in a file called Main.java
            FileIO.writeFileForCompilation(code);
            File exerciseFile = new File("exerciseCompilation/Main.java");
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(exerciseFile));
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

            // Call the task. If there are compilation error, then the if statement is executed
            if (!task.call()) {
                // Create the error messages to be displayed
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
                    message.append("Error on line ").append(diagnostic.getLineNumber()).append(": ").
                            append(diagnostic.getMessage(Locale.ENGLISH)).append("\n");
                // A compilation error was produced, so inform about this in the map
                map.put("type", "error");
            }
            else { // No compilation errors were produced
                // Load the class file
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{exerciseFile.getParentFile().toURI().toURL()});
                Class<?> loadedClass = Class.forName("Main", true, classLoader);
                // Obtain the main method of the class
                Method method = loadedClass.getMethod("main", String[].class);
                String[] args = new String[0];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(out);

                // Change the output stream destination to the printStream in order to capture
                // what is being printed when executing the method
                System.setOut(printStream);
                method.invoke(null, (Object) args);  // invoke the method

                // Close the stream
                printStream.flush();
                printStream.close();

                message = new StringBuilder(out.toString());
                // Reset output stream to the standard terminal
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

    /*
     * @return the thread id field.
     */
    public long getThreadId() {
        return threadId;
    }
}
