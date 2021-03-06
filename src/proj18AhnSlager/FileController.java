/*
 * File: FileController.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 7
 * Date: November 2, 2018
 *
 * --------------------------------------
 *
 * Modified by Jackie Hang, Kyle Slager
 * Project 11
 * Date: February 13, 2019
 */

package proj18AhnSlager;

import javafx.event.Event;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;


import javafx.application.Platform;

import javafx.scene.control.Tab;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import proj18AhnSlager.bantam.ast.Piece;
import proj18AhnSlager.bantam.lexer.StructureCheckingTokenizer;
import proj18AhnSlager.bantam.lexer.Token;
import proj18AhnSlager.bantam.parser.NodeGeneratorAndChecker;
import proj18AhnSlager.bantam.treedrawer.Drawer;
import proj18AhnSlager.bantam.util.CompilationException;
import proj18AhnSlager.bantam.util.ErrorHandler;
import proj18AhnSlager.bantam.util.Error;


/**
 * This class contains the handlers for each of the menu options in the IDE.
 *
 * Keeps track of the tab pane, the current tab, the index of the current tab
 * within the pane, and the File objects of the current tabs.
 *
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou (Project 5)
 * @author  Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 3.0
 * @since   11-2-2018
 */
public class FileController {

    private TempoTabPane tempoTabPane;
    private String extension;
    private HashMap<Tab, String> tabFilepathMap;
    private VBox vBox;
    private StructureCheckingTokenizer structureCheckingTokenizer;
    private NodeGeneratorAndChecker nodeGeneratorAndChecker;
    private ErrorHandler errorHandler;
    private ErrorHandler analysisErrors;

    /**
     * ContextMenuController handling context menu actions
     */
    private ContextMenuController contextMenuController;

    /**
     * Constructor for the class. Intializes the save status
     * and the tabFilepathMap in a HashMap
     */
    public FileController(VBox vBox, TempoTabPane tempoTabPane) {
        this.vBox = vBox;
        this.extension = "";
        this.tempoTabPane = tempoTabPane;
        this.tabFilepathMap = new HashMap<>();
    }

    /**
     * Sets the contextMenuController.
     *
     * @param contextMenuController ContextMenuController handling context menu actions
     */
    public void setContextMenuController(ContextMenuController contextMenuController) {
        this.contextMenuController = contextMenuController;
    }

    /**
     * Returns the name of the file open in the current tab.
     * @return The name of the currently open file
     */
    protected String getFilePath(){
        Tab curTab = this.tempoTabPane.getSelectionModel().getSelectedItem();
        return tabFilepathMap.get(curTab);
    }

    /**
     * Handler for the "About" menu item in the "File" menu.
     * Creates an Information alert dialog to display author and information of this program
     */
    public void handleAbout() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("About");
        dialog.setHeaderText(null);
        dialog.setContentText("V3 Authors: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager\n" +
                "Version 2 Authors: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou\n" +
                "Version 1 Authors: Kevin Ahn, Matt Jones, Jackie Hang, Kevin Zhou\n" +
                "This application is a basic IDE with syntax highlighting.");
        dialog.showAndWait();
    }

    /**
     * Creates a pop-up window which allows the user to select whether they wish to save
     * the current file or not.
     * Used by handleClose.
     *
     * @param event the tab closing event that may be consumed
     */
    private String askSaveAndScan(Event event) {
        ShowSaveOptionAlert saveOptions = new ShowSaveOptionAlert();
        Optional<ButtonType> result = saveOptions.getUserSaveDecision();

        if (result.isPresent()) {
            if (result.get() == saveOptions.getCancelButton()) {
                event.consume();
                return "cancel";
            }
            else if (result.get() == saveOptions.getYesButton()){
                boolean saved = this.handleSave();
                if (saved) return "yes";
                event.consume();
                return null;
            }
            else {
                event.consume();
                return "no";
            }
        }
        return null;
    }

    /**
     * this method is called when the Scan button is pressed
     * if the file is not saved it prompts the user to save before scanning
     * it will scan the file and display the tokens in a new tab
     * @param event press of the Scan button triggering the handleScan method
     */
    public void handleScan(Event event) {
        scanOrParseHelper(event, "SCAN_ONLY" );
    }

    /**
     * this method is called when the Scan&Parse button is pressed
     * if the file is not saved it prompts the user to save before scanning
     * it will scan and parse the file and display an AST if parse
     * was successful
     * @param event press of the Scan button triggering the handleScan method
     */
    public void handleScanAndParse (Event event) {
        scanOrParseHelper(event, "SCAN_AND_PARSE" );
    }

    /**
     * Assists with calling just scan or scanning and parsing the
     * file
     * @param event press of the Scan button triggering the handleScan and Parse method
     * @param scanOrParse string "SCAN_ONLY" or "SCAN_AND_PARSE" or "PARSE_NO_TREE_DRAWN"
     */
    public Piece scanOrParseHelper(Event event, String scanOrParse ){

        // Grabs the current tab open to be scanned or parsed
        TempoTab curTab = (TempoTab) this.tempoTabPane.getSelectionModel().getSelectedItem();
        if (this.tempoTabPane.tabIsSaved(curTab)) { // checks if the user needs to save the currently open tab
            String filename = this.tabFilepathMap.get(curTab);
            try {
                this.errorHandler = new ErrorHandler();
                if(scanOrParse.equals("SCAN_ONLY")) { // user clicked Scan Button
                    this.structureCheckingTokenizer = new StructureCheckingTokenizer(filename, this.errorHandler);
                }
                else{
                    this.nodeGeneratorAndChecker = new NodeGeneratorAndChecker(this.errorHandler);
                }

            }
            catch(CompilationException e){
                System.out.println(e);
            }

            // section for SCAN ONLY
            if(scanOrParse.equals("SCAN_ONLY")) {
                this.handleNew(null);
                curTab = (TempoTab) this.tempoTabPane.getSelectionModel().getSelectedItem();
                Token nextToken; // the next token to be scanned
                // loops until the end of file
                while ( (nextToken = structureCheckingTokenizer.scan()).kind != Token.Kind.EOF ) {
                    if(nextToken.kind != Token.Kind.NOTWORD) { // adds in the next token to the string
                        curTab.getCodeArea().appendText(nextToken.toString() + "\n");
                    }
                }
                return null;
            }

            // section for SCAN AND PARSE
            else{
                Piece root = this.nodeGeneratorAndChecker.parse(filename);
                if(scanOrParse.equals("SCAN_AND_PARSE")) { // user clicked Scan and Parse
                    Drawer drawer = new Drawer(); // creates the drawer
                    drawer.draw(filename, root);
                }
                return root;
            }

        }

        // used for saving of tabs and user response
        String saveStatus = this.askSaveAndScan(event);
        if (saveStatus == "cancel") {
            this.structureCheckingTokenizer = null;
            return null;
        }
        else if (saveStatus == "no") {
            if (tabFilepathMap.get(curTab) == null) {
                return null;
            }
        }
        else if (saveStatus == "yes"){
            scanOrParseHelper(event, scanOrParse);
        }
        return null;
    }

    /**
     * @return the list of errors from the most recent scan performed on a file
     * return value will be null if there is no valid file open to scan
     */
    public List<Error> getErrors() {
        if (this.structureCheckingTokenizer == null) return null;
            return this.errorHandler.getErrorList();
    }
    /**
     *
     * @return the list of errors from the most recent scan performed on a file
     * return value will be null if there is no valid file open to scan
     */
    public List<Error> getScanningErrors() {
        if (this.structureCheckingTokenizer == null) return null;
        return this.structureCheckingTokenizer.getErrors();
    }

    public List<Error> getParsingErrors() {
        if (this.nodeGeneratorAndChecker == null) return null;
        return this.nodeGeneratorAndChecker.getParseErrors();
    }

    /**
     * Handler for the "New" menu item in the "File" menu.
     * Adds a new Tab to the TabPane, adds null to the tabFilepathMap HashMap,
     * and false to the saveStatus HashMap
     */
    public void handleNew(File file) {
        this.tempoTabPane.createNewTab(this, contextMenuController, file);
        TempoTab t = (TempoTab)this.tempoTabPane.getSelectionModel().getSelectedItem();
        if (file == null) this.tabFilepathMap.put(t, null);
        else {
            this.tabFilepathMap.put(t, file.getPath());
            String name = file.getName();
            this.extension = name.substring(name.lastIndexOf("."));
        }

    }

    /**
     * Handler for the "Open" menu item in the "File" menu.
     * Creates a FileChooser to select a file
     * Use scanner to read the file and write it into a new tab.
     */
    public void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        Window stage = this.vBox.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null){
            handleNew(file);
        }
    }

    /**
     * Handler for the "Close" menu item in the "File" menu.
     * Checks to see if the file has been changed since the last save.
     * If changes have been made, redirect to askSaveAndClose and then close the tab.
     * Otherwise, just close the tab.
     */
    public void handleClose(Event event) {

        TempoTab curTab = (TempoTab)this.tempoTabPane.getSelectionModel().getSelectedItem();

        if (tabFilepathMap.get(curTab) != null) {
            // check if any changes were made
            if (this.tempoTabPane.tabIsSaved(curTab))
                this.closeTab();
            else
                this.askSaveAndClose(event);
        } else {
            if(!tabFilepathMap.isEmpty()) {
                this.askSaveAndClose(event);
            }
        }
    }

    /**
     * Handler for the "Save" menu item in the "File" menu.
     * If the current tab has been saved before, writes out the content to its corresponding
     * file in storage.
     * Else if the file has never been saved, opens a pop-up window that allows the user to
     * choose a filename and directory and then store the content of the tab to storage.
     */
    public boolean handleSave() {
        TempoTab curTab = (TempoTab)this.tempoTabPane.getSelectionModel().getSelectedItem();

        if (tabFilepathMap.get(curTab) != null){
            File file = new File(tabFilepathMap.get(curTab));    // this is what gets the path
            String name = file.getName();
            this.extension = name.substring(name.lastIndexOf("."));
            writeFile(file);
            this.tempoTabPane.updateTabSavedStatus(curTab, true);
            return true;
        }
        else
            return this.handleSaveAs();
    }

    /**
     * Handler for the "Save as..." menu item in the "File" menu.
     * Opens a pop-up window that allows the user to choose a filename and directory.
     * Calls writeFile to save the file to memory.
     * Changes the name of the current tab to match the newly saved file's name.
     */
    public boolean handleSaveAs() {
        TempoTab curTab = (TempoTab)this.tempoTabPane.getSelectionModel().getSelectedItem();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as...");
        Window stage = this.vBox.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        String name = file.getName();
        this.extension = name.substring(name.lastIndexOf("."));
        if (file == null){
            return false;
        }
        else{
            writeFile(file);
            tabFilepathMap.replace(curTab,file.getPath());
            this.tempoTabPane.updateTabSavedStatus(curTab, true);
        }
        curTab.setText(file.getName());
        return true;
    }

    /**
     * Handler for the "Exit" menu item in the "File" menu.
     * Closes all the tabs using handleClose()
     * Returns when the user cancels exiting any tab.
     */
    public void handleExit(Event event) {
        int numTabs = tabFilepathMap.size();
        // Close each tab using handleClose()
        // Check if current number of tabs decreased by one to know if the user cancelled.
        for (int i = 0; i < numTabs; i++ ) {
            this.handleClose(event);
            if (tabFilepathMap.size() == (numTabs - i)) return;

        }
        Platform.exit();
    }

    /**
     * Creates a pop-up window which allows the user to select whether they wish to save
     * the current file or not.
     * Used by handleClose.
     *
     * @param event the tab closing event that may be consumed
     */
    private void askSaveAndClose(Event event) {
        ShowSaveOptionAlert saveOptions = new ShowSaveOptionAlert();
        Optional<ButtonType> result = saveOptions.getUserSaveDecision();

        if (result.isPresent()) {
            if (result.get() == saveOptions.getCancelButton()) {
                event.consume();
                return;
            } else if (result.get() == saveOptions.getYesButton()) {

                boolean isNotCancelled = this.handleSave();

                if(isNotCancelled) {
                    this.closeTab();
                } else {
                    event.consume();
                }
                return;
            }
            this.closeTab();
        }
    }

    /**
     * Saves the text present in the current tab to a given filename.
     * Used by handleSave, handleSaveAs.
     *
     * @param file The file object to which the text is written to.
     */
    private void writeFile(File file) {
        TempoTab curTab = (TempoTab)this.tempoTabPane.getSelectionModel().getSelectedItem();
        VirtualizedScrollPane<CodeArea> scrollPane = (VirtualizedScrollPane<CodeArea>) curTab.getContent();
        CodeArea codeArea = scrollPane.getContent();
        String text = codeArea.getText();

        // use a BufferedWriter object to write out the string to a file
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(text);
            writer.close();
        }
        catch (IOException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText("File Error");
            alert.setContentText("Cannot find file or file is Read-only. Please select a new file.");
            alert.showAndWait();
            return;
        }

        this.tempoTabPane.updateTabSavedStatus(curTab, true);
    }

    /**
     * Executes process for when a tab is closed, which is to remove the filename and saveStatus at
     * the corresponding HashMaps, and then remove the Tab object from TabPane
     *
     */
    private void closeTab() {
        //NOTE: the following three lines has to be in this order removing the tab first would
        //result in calling handleUpdateCurrentTab() because the currently selected tab will
        //change, and thus the wrong File will be removed from the HashMaps
        TempoTab curTab = (TempoTab)this.tempoTabPane.getSelectionModel().getSelectedItem();
        tabFilepathMap.remove(curTab);
        tempoTabPane.removeTab(curTab);
    }
}