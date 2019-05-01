/*
 * File: ToolBarController.java
 * F18 CS361 Project 6
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 10/27/2018
 * This file contains the ToolBarController class, handling Toolbar related actions.
 */

package proj18AhnSlagerZhao;

import javafx.application.Platform;
import javafx.event.Event;
import org.fxmisc.richtext.StyleClassedTextArea;

import javafx.scene.control.*;

import java.io.*;


import java.util.concurrent.*;

import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;
import javafx.concurrent.Service;
import proj18AhnSlagerZhao.FileController;
import proj18AhnSlagerZhao.bantam.util.ErrorHandler;
import proj18AhnSlagerZhao.bantam.codegenmips.MipsCodeGenerator;
import proj18AhnSlagerZhao.bantam.util.ErrorHandler;


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


    private FutureTask<Boolean> curFutureTask;
    private Button stopButton;
    private Button compileButton;
    private Button compileRunButton;
    private TabPane tabPane;
    private boolean receivedCommand = false;

    public ToolBarController(Console console, FileController fileController){
        this.fileController = fileController;
        this.console = console;
        this.mutex = new Semaphore(1);
        //this.compileWorker = new CompileWorker();
       // this.compileRunWorker = new CompileRunWorker();
    }
    /**
     * Console defined in Main.fxml
     */
    private Console console;

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
   // private CompileWorker compileWorker;
    /**
     * A CompileRunWorker object compiles and runs a Java file in a separate thread.
     */

    /**
     *  Compiles the code currently open, assuming it has been saved.
     *
     */
    public void handleCompile(){
        Thread compileThread = new Thread(()->compileFile(fileController.getFilePath()));
        compileThread.start();
    }

    /**
     * Calls compile and runs the code
     *
     */
    public void handleCompileAndRun(){
        Thread compileRunThread = new Thread(() -> compileRunFile(fileController.getFilePath()));
        compileRunThread.start();
    }

    /**
     * Compiles the specified file using the javac command
     * @param filename the name of the file to compile
     * @return whether or not compilation was successful
     */
    private boolean compileFile(String filename) {

        // create and run the compile process
        List<String> processBuilderArgs = new ArrayList<>();
        processBuilderArgs.add("javac");
        processBuilderArgs.add("-cp");
        processBuilderArgs.add("/Users/kwslager/Desktop/project16AhnSlagerZhao/src/proj18AhnSlagerZhao/resources/jfugue-5.0.9.jar");
        processBuilderArgs.add(filename);
        System.out.println(processBuilderArgs);
        ProcessBuilder pb = new ProcessBuilder(processBuilderArgs);
        CompileOrRunTask compileTask = new CompileOrRunTask(this.console, pb);
        this.curFutureTask = new FutureTask<Boolean>(compileTask);
        ExecutorService compileExecutor = Executors.newFixedThreadPool(1);
        compileExecutor.execute(curFutureTask);


        // Check if compile was successful, and if so, indicate this in the console
        Boolean compSuccessful = false;
        try {
            compSuccessful = curFutureTask.get();
            if (compSuccessful) {
                Platform.runLater(() ->
                        this.console.appendText("Compilation was Successful.\n"));
            }
            compileExecutor.shutdown();
        } catch (ExecutionException | InterruptedException | CancellationException e) {
            compileTask.stop();
        }

        return compSuccessful;
    }


//    /**
//     * Compiles and runs the specified file using the java command
//     * @param fileNameWithPath the file name, including its path
//     */
//    private void compileRunFile(String fileNameWithPath){
//
//        // Try to compile
//        boolean compSuccessful = compileFile(fileNameWithPath);
//        if(!compSuccessful){
//            return;
//        }
//        // Disable appropriate compile buttons
//        //disableCompileAndRunButtons();
//        List<String> processBuilderArgs = new ArrayList<>();
//        processBuilderArgs = new ArrayList<>();
//        processBuilderArgs.add("java");
//        processBuilderArgs.add("-jar");
//        processBuilderArgs.add("jfugue-5.0.9.jar");
//        // set up the necessary file path elements
//        int pathLength = fileNameWithPath.length();
//        File file = new File(fileNameWithPath);
//        String filename = file.getName();
//        String filepath = fileNameWithPath.substring(0,pathLength-filename.length());
//        int nameLength = filename.length();
//        String classFilename = filename.substring(0, nameLength - 5);
//
//        processBuilderArgs.add(filepath);
//        //processBuilderArgs.add(classFilename);
//
//        // Run the java program
//        ProcessBuilder pb = new ProcessBuilder(processBuilderArgs);
//        CompileOrRunTask runTask = new CompileOrRunTask(console,pb);
//        this.curFutureTask = new FutureTask<Boolean>(runTask);
//        ExecutorService curExecutor = Executors.newFixedThreadPool(1);
//        curExecutor.execute(this.curFutureTask);
//
//        try{
//            curExecutor.shutdown();
//        }
//        // if the program is interrupted, stop running
//        catch (CancellationException e){
//            runTask.stop();
//        }
//
//    }
    /**
     * Helper method for running Mips Programs.
     */
    public boolean compileRunFile(String fileName) {
        boolean compSuccessful = compileFile(fileName);
        if(!compSuccessful){
            return compSuccessful;
        }
        try {
            Platform.runLater(() -> {
                this.console.clear();
                consoleLength = 0;
            });
            // again adds in needed commands into the PB list
            List<String> processBuilderArgs = new ArrayList<>();
            processBuilderArgs = new ArrayList<>();
            processBuilderArgs.add("java");
            processBuilderArgs.add("-cp");
            processBuilderArgs.add(".:/Users/kwslager/Desktop/project16AhnSlagerZhao/src/proj18AhnSlagerZhao/resources/jfugue-5.0.9.jar");
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
     * An inner class used for a thread to execute the run task
     * Designed to be used for compilation or running.
     * Writes the input/output error to the console.
     */
    private class CompileOrRunTask implements Callable{
        private Process curProcess;
        private Console console;
        private ProcessBuilder pb;

        /**
         * Initializes this compile/run task
         * @param console where to write output to
         * @param pb the ProcessBuilder we have used to call javac/java
         */
        CompileOrRunTask(Console console, ProcessBuilder pb){
            this.console = console;
            this.pb = pb;
        }

        /**
         * Starts the process
         * @return will return false if there is an error, true otherwise.
         * @throws IOException error reading input/output to/from console
         */
        @Override
        public Boolean call() throws IOException{
            this.curProcess = pb.start();
            BufferedReader stdInput, stdError;
            BufferedWriter stdOutput;
            stdInput = new BufferedReader(new InputStreamReader(this.curProcess.getInputStream()));
            stdError = new BufferedReader(new InputStreamReader(this.curProcess.getErrorStream()));
            stdOutput = new BufferedWriter((new OutputStreamWriter(this.curProcess.getOutputStream())));

            // Input to the console from the program
            String inputLine;

            // Errors from the executing task
            String errorLine = null;

            // True if there are no errors
            Boolean taskSuccessful = true;

            // While there is some input to the console, or errors that have occurred,
            // append them to the console for the user to see.
            while ((inputLine = stdInput.readLine()) != null || (errorLine = stdError.readLine()) != null){

                final String finalInputLine = inputLine;
                final String finalErrorLine = errorLine;
                if (finalInputLine != null) {
                    Platform.runLater(() -> this.console.appendText(" " + finalInputLine + " INPUT\n"));
                }
                if(finalErrorLine != null) {
                    taskSuccessful = false;
                    Platform.runLater(() -> this.console.appendText(" " + finalErrorLine + " ERROR\n"));
                }
                try {
                    Thread.sleep(50);
                }catch (InterruptedException e){
                    this.stop();
                    return taskSuccessful;
                }
            }
            stdError.close();
            stdInput.close();
            stdOutput.close();
            return taskSuccessful;
        }

        /**
         * Stop the current process
         */
        public void stop(){
            if(this.curProcess != null){
                curProcess.destroyForcibly();
            }
        }
    }

    /**
     * Disables the Compile and Compile and Run buttons, enables the Stop button.
     */
    public void disableCompileAndRunButtons() {
        this.compileButton.setDisable(true);
        this.compileRunButton.setDisable(true);
        this.stopButton.setDisable(false);
    }

    /**
     * Enables the Compile and Compile and Run buttons, disables the Stop button.
     */
    public void enableCompileAndRunButtons() {
        this.compileButton.setDisable(false);
        this.compileRunButton.setDisable(false);
        this.stopButton.setDisable(true);
    }

}
