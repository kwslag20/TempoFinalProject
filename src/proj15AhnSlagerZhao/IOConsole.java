/*
 * File: IOConsole.java
 * Author: djskrien
 * Date: 7/7/18
 */
package proj15AhnSlagerZhao;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class IOConsole extends StyleClassedTextArea
{
    /**
     * set up the console for user input
     *
     * @param prompt - the prompt to the user for input
     */
    public void enableInput(final String prompt) {
        Platform.runLater(()-> {
            appendText(prompt);
            setEditable(true);
            requestFocus();
        });
    }

    /**
     * set up the console for displaying output
     */
    public void disableInput() {
        this.setEditable(false);
    }

    public void appendLine(String text) {
        this.appendText(text + "\n");
    }

    /**
     * gets input from the Console and sends it to the given OutputStream
     *
     * @param outputStream the OutputStream where the Console input is sent
     */
    void sendInputTo(OutputStream outputStream) {
        this.setOnKeyTyped(new EventHandler<>()
        {
            String result = "";

            @Override
            public void handle(KeyEvent event) {
                if (!IOConsole.this.isEditable()) {
                    return;
                }
                result += event.getCharacter();
                if (event.getCharacter().equals("\r")) { //(System.getProperty("line.separator"))) {
                    try {
                        for (char c : result.toCharArray()) {
                            outputStream.write(c);
                        }
                        outputStream.flush();
                        result = "";
                    } catch (IOException e) {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR,
                                "Could not send input to the process" + "."));
                    }
                }
            }

        });
        this.enableInput("");
    }

    /**
     * writes the input from an InputStream continuously to the ioConsole.
     * Blocks the current thread while it waits for more input.
     *
     * @param inputStream the InputStream providing the data to be written
     */
    void writeDataFrom(InputStream inputStream) throws IOException {
        // see
        // stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
        //  for a discussion of how to convert inputStream data to strings.
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            String result = new String(buffer, 0, length);
            Platform.runLater(() -> {
                appendText(result);
                moveTo(getLength());
                requestFollowCaret();
            });
            //this next code is apparently not necessary.
            try {
                //the read(buffer) method above blocks until the next input is available
                //so we need to sleep occasionally to avoid the whole thread from freezing.
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.NONE, "Thread " +
                        "sleep " + "was interrupted", ButtonType.OK).showAndWait());
                return;
            }
        }
    }
}
