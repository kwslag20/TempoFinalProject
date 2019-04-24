/*
 * File: ToolBarController.java
 * F18 CS361 Project 6
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 10/27/2018
 * This file contains the ToolBarController class, handling Toolbar related actions.
 */

package proj17AhnSlagerZhao;

import javafx.application.Platform;
import javafx.event.Event;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.io.*;

import javafx.concurrent.Task;
import javafx.concurrent.Service;
import proj17AhnSlagerZhao.bantam.codegenmips.MipsCodeGenerator;
import proj17AhnSlagerZhao.bantam.util.ErrorHandler;

/**
 * ToolbarController handles Toolbar related actions.
 *
 * updated to handle both the running of mips files in the ide
 *
 * @author Evan Savillo
 * @author Yi Feng
 * @author Zena Abulhab
 * @author Melody Mao
 */
public class ToolBarController {

    public ToolBarController(Console console, FileController fileController){
        this.fileController = fileController;
        this.console = console;
        this.mutex = new Semaphore(1);
        this.compileWorker = new CompileWorker();
        this.compileRunWorker = new CompileRunWorker();
    }
    /**
     * Console defined in Main.fxml
     */
    private StyleClassedTextArea console;
    /**
     * Process currently compiling or running a Java file
     */
    private Process curProcess;
    /**
     * Thread representing the Java program input stream
     */
    private Thread inThread;
    /**
     * Thread representing the Java program output stream
     */
    private Thread outThread;

    /**
     * Mutex lock to control input and output threads' access to console
     */
    private Semaphore mutex;
    /**
     * The consoleLength of the output on the console
     */
    private int consoleLength;
    /**
     * The FileMenuController
     */
    private FileController fileController;
    /**
     * A CompileWorker object compiles a Java file in a separate thread.
     */
    private CompileWorker compileWorker;
    /**
     * A CompileRunWorker object compiles and runs a Java file in a separate thread.
     */
    private CompileRunWorker compileRunWorker;

    /**
     * Helper method for assembling Mips files.
     */
    public boolean assembleMIPSFile(String fileName) {
        try {
            Platform.runLater(() -> {
                this.console.clear();
                this.consoleLength = 0;
            });
            // adds in the appropriate elements into the PB list
            List<String> processBuilderArgs = new ArrayList<>();
            processBuilderArgs.add("java");
            processBuilderArgs.add("-jar");
            processBuilderArgs.add("mars.jar");
            processBuilderArgs.add("a");
            processBuilderArgs.add(fileName);
            ProcessBuilder builder = new ProcessBuilder(processBuilderArgs);
            this.curProcess = builder.start();

            this.outputToConsole();

            // true if compiled without compile-time error, else false
            return this.curProcess.waitFor() == 0;
        } catch (Throwable e) {
            Platform.runLater(() -> {
            });
            return false;
        }
    }

    /**
     * Helper method for running Mips Programs.
     */
    public boolean runMIPSFile(String fileName) {
        try {
            Platform.runLater(() -> {
                this.console.clear();
                consoleLength = 0;
            });
            // again adds in needed commands into the PB list
            List<String> processBuilderArgs = new ArrayList<>();
            processBuilderArgs = new ArrayList<>();
            processBuilderArgs.add("java");
            processBuilderArgs.add("-jar");
            processBuilderArgs.add("mars.jar");
            processBuilderArgs.add(fileName);
            ProcessBuilder builder = new ProcessBuilder(processBuilderArgs);
            this.curProcess = builder.start();

            // Start output and input in different threads to avoid deadlock
            this.outThread = new Thread() {
                public void run() {
                    try {
                        // start output thread first
                        mutex.acquire();
                        outputToConsole();
                    } catch (Throwable e) {
                        Platform.runLater(() -> {
                            // print stop message if other thread hasn't
                            if (consoleLength == console.getLength()) {
                                console.appendText("\nProgram exited unexpectedly\n");
                                console.requestFollowCaret();
                            }
                        });
                    }
                }
            };
            outThread.start();
            inThread = new Thread() {
                public void run() {
                    try {
                        inputFromConsole();
                    } catch (Throwable e) {
                        Platform.runLater(() -> {
                            // print stop message if other thread hasn't
                            if (consoleLength == console.getLength()) {
                                console.appendText("\nProgram exited unexpectedly\n");
                                console.requestFollowCaret();
                            }
                        });
                    }
                }
            };
            inThread.start();

            // true if ran without error, else false
            return curProcess.waitFor() == 0;
        } catch (Throwable e) {
            Platform.runLater(() -> {
            });
            return false;
        }
    }

    public void handleCompile(Event event, ErrorHandler handler) {
        MipsCodeGenerator mipsCodeGenerator = new MipsCodeGenerator(handler, false, false);
        String[] files = new String[]{this.fileController.getFilePath()};
        mipsCodeGenerator.main(files);
    }

    /**
     * Helper method for getting program output
     */
    private void outputToConsole() throws java.io.IOException, java.lang.InterruptedException {
        InputStream stdout = this.curProcess.getInputStream();
        InputStream stderr = this.curProcess.getErrorStream();

        BufferedReader outputReader = new BufferedReader(new InputStreamReader(stdout));
        printOutput(outputReader);

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(stderr));
        printOutput(errorReader);
        outputReader.close();
        errorReader.close();
    }


    /**
     * Helper method for getting program input
     */
    public void inputFromConsole() throws java.io.IOException, java.lang.InterruptedException {
        OutputStream stdin = curProcess.getOutputStream();
        BufferedWriter inputWriter = new BufferedWriter(new OutputStreamWriter(stdin));

        while (curProcess.isAlive()) {
            // wait until signaled by output thread
            this.mutex.acquire();
            // write input to program
            writeInput(inputWriter);
            // signal output thread
            this.mutex.release();
            // wait for output to acquire mutex
            Thread.sleep(1);
        }
        inputWriter.close();
    }

    /**
     * Helper method for printing to console
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    private void printOutput(BufferedReader reader) throws java.io.IOException, java.lang.InterruptedException {
        // if the output stream is paused, signal the input thread
        if (!reader.ready()) {
            this.mutex.release();
        }

        int intch;
        // read in program output one character at a time
        while ((intch = reader.read()) != -1) {
            this.mutex.tryAcquire();
            char ch = (char) intch;
            String out = Character.toString(ch);
            Platform.runLater(() -> {
                // add output to console
                this.console.appendText(out);
                this.console.requestFollowCaret();
            });
            // update console length tracker to include output character
            this.consoleLength++;

            // if the output stream is paused, signal the input thread
            if (!reader.ready()) {
                this.mutex.release();
            }
            // wait for input thread to acquire mutex if necessary
            Thread.sleep(1);
        }
        this.mutex.release();
        reader.close();
    }


    /**
     * Helper function to write user input
     */
    public void writeInput(BufferedWriter writer) throws java.io.IOException {
        // wait for user to input line of text
        while (true) {
            if (this.console.getLength() > this.consoleLength) {
                // check if user has hit enter
                if (this.console.getText().substring(this.consoleLength).contains("\n")) {
                    break;
                }
            }
        }
        // write user-entered text to program input
        writer.write(this.console.getText().substring(this.consoleLength));
        writer.flush();
        // update console length to include user input
        this.consoleLength = this.console.getLength();
    }

    /**
     * Handles the Compile button action.
     *
     * @param event Event object
     * @param fileName  the Selected file
     */
    public void handleAssembleAction(Event event, String fileName) {
        // user select cancel button
        event.consume();
        compileWorker.setFileName(fileName);
        compileWorker.restart();
    }

    /**
     * Handles the CompileRun button action.
     *
     * @param event Event object
     * @param fileName  the Selected file
     */
    public void handleRunAction(Event event, String fileName) {
        // user select cancel button
        event.consume();
        compileRunWorker.setFileName(fileName);
        compileRunWorker.restart();
    }

    /**
     * Handles the Stop button action.
     */
    public void handleStopButtonAction() {
        try {
            System.out.println("FIRST");
            if (this.curProcess.isAlive()){
                this.inThread.interrupt(); // CURRENTLY BREAKS DOWN HERE
                this.outThread.interrupt();
                this.curProcess.destroy();
            }
        } catch (Throwable e) {
            console.appendText("\nProgram was Stopped by User\n");
        }
    }

    /**
     * A CompileWorker subclass handling Java program compiling in a separated thread in the background.
     * CompileWorker extends the javafx Service class.
     */
    protected class CompileWorker extends Service<Boolean> {
        /**
         * the file to be compiled.
         */
        private String fileName;

        /**
         * Sets the selected file.
         *
         * @param fileName the file to be compiled.
         */
        private void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * Overrides the createTask method in Service class.
         * Compiles the file embedded in the selected tab, if appropriate.
         *
         * @return true if the program compiles successfully;
         * false otherwise.
         */
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                /**
                 * Called when we execute the start() method of a CompileRunWorker object
                 * Compiles the file.
                 *
                 * @return true if the program compiles successfully;
                 *         false otherwise.
                 */
                @Override
                protected Boolean call() {
                    Boolean compileResult = assembleMIPSFile(fileName);
                    if (compileResult) {
                        Platform.runLater(() -> console.appendText("Assembly was successful!\n"));
                    }
                    return compileResult;
                }
            };
        }
    }

    /**
     * A CompileRunWorker subclass handling Java program compiling and running in a separated thread in the background.
     * CompileWorker extends the javafx Service class.
     */
    protected class CompileRunWorker extends Service<Boolean> {
        /**
         * the file to be compiled.
         */
        private String fileName;

        /**
         * Sets the selected file.
         *
         * @param fileName the file to be compiled.
         */
        private void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * Overrides the createTask method in Service class.
         * Compiles and runs the file embedded in the selected tab, if appropriate.
         *
         * @return true if the program runs successfully;
         * false otherwise.
         */
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                /**
                 * Called when we execute the start() method of a CompileRunWorker object.
                 * Compiles the file and runs it if compiles successfully.
                 *
                 * @return true if the program runs successfully;
                 *         false otherwise.
                 */
                @Override
                protected Boolean call() {
                    return runMIPSFile(fileName);
                }
            };
        }
    }


}
