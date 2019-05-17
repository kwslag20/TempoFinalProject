/*
 * File: TempoTab.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 */

package proj18AhnSlager;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This is the TempoTab class which stores a JavaCodeArea
 *
 * @author Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 1.0
 * @since 11-20-2018
 */
public class TempoTab extends Tab {
    private TempoCodeArea codeArea;

    /**
     * Constructor for a TempoTab
     *
     * @param fileController
     * @param contextMenuController
     * @param tabPane
     * @param filename
     * @param file
     */
    public TempoTab(FileController fileController, ContextMenuController contextMenuController,
                    TempoTabPane tabPane, String filename, File file) {

        super(filename);
        String extension = "";
        if (file != null){
            String name = file.getName();
            extension = name.substring(name.lastIndexOf("."));
        }

        if(extension.equals(".java")) {
            codeArea = new TempoCodeArea(contextMenuController, extension);;
        }
        else if(extension.equals(".asm") || extension.equals(".s")){
            codeArea = new TempoCodeArea(contextMenuController, extension);
        }
        else if(extension.equals(".mus") || extension.equals(".txt")){
            codeArea = new TempoCodeArea(contextMenuController, extension);
        }
        else{
            codeArea = new TempoCodeArea(contextMenuController, extension);
        }


        // bind code area to method updating its saved status in the tabSavedStatusMap of the TabPane
        codeArea.setOnKeyPressed(
                (event) -> tabPane.updateTabSavedStatus(this, false));
        this.setContent(new VirtualizedScrollPane<>(codeArea,
                ScrollPane.ScrollBarPolicy.ALWAYS,
                ScrollPane.ScrollBarPolicy.ALWAYS));

        if (file != null) {
            String fileText = getFileContents(file);
            codeArea.replaceText(fileText);
        }

        this.setOnCloseRequest( (event) -> fileController.handleClose(event) );

        // enable the tab's right-click menu
        contextMenuController.setupTabContextMenuHandler(this);
    }

    /**
     * Grabs the content from the file
     *
     * @param file the file tha
     * @return string
     */
    public String getFileContents(File file) {

        String content = "";
        if (file != null){

            try {
                Scanner scanner = new Scanner(file).useDelimiter("\\Z");
                if (scanner.hasNext())
                    content = scanner.next();
            }
            catch (FileNotFoundException | NullPointerException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("File Error");
                alert.setContentText("File not Found: Please select a new file.");
                alert.showAndWait();
            }
        }
        return content;
    }

    public CodeArea getCodeArea(){
        return this.codeArea;
    }



}
