/*
 * File: MasterController.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 * ---------------------------
 * Edited From: Zena Abulhab, Paige Hanssen, Kyle Slager, Kevin Zhou
 * Project 5
 * Date: October 12, 2018
 *
 */

package proj18AhnSlager;


import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.Event;

import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Desktop;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import proj18AhnSlager.bantam.util.CompilationException;
import proj18AhnSlager.bantam.util.Error;
import proj18AhnSlager.bantam.util.ErrorHandler;

/**
 * This is the master controller for the program. it references
 * the other controllers for proper menu functionality.
 *
 /**
 * This class contains the handlers for each of the menu options in the IDE.
 *
 * Keeps track of the tab pane, the current tab, the index of the current tab
 * within the pane, and the File objects of the current tabs.
 *
 * @author  Zena Abulhab, Paige Hanssen, Kyle Slager Kevin Zhou (Project 5)
 * @author  Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 4.0
 * @since   11-2-2018
 */
public class MasterController {
    @FXML private Menu editMenu;
    @FXML private TempoTabPane tempoTabPane;
    @FXML private VBox vBox;
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem saveAsMenuItem;
    @FXML private MenuItem closeMenuItem;
    @FXML private MenuItem darkModeMenuItem;
    @FXML private MenuItem normalModeMenuItem;
    @FXML private MenuItem funModeMenuItem;
    @FXML private MenuItem hallowThemeItem;
    @FXML private Console console;
    @FXML private TextField findTextEntry;
    @FXML private Button findPrevBtn;
    @FXML private Button findNextBtn;
    @FXML private Menu prefMenu;
    @FXML private Button stopButton;
    @FXML private Button scanButton;
    @FXML private Button parseButton;
    @FXML private Button compileAndRunButton;

    private EditController editController;
    private FileController fileController;
    private ToolBarController toolBarController;
    private ErrorHandler errorHandler;
    private ContextMenuController contextMenuController;

    @FXML
    public void initialize(){

        // initializes necessary controllers
        editController = new EditController(tempoTabPane, findTextEntry, findPrevBtn, findNextBtn);
        this.fileController = new FileController(vBox, tempoTabPane);
        this.toolBarController = new ToolBarController(console, fileController);
        this.errorHandler = new ErrorHandler();

        // sets up menu items and buttons to be disabled when nothing is open
        SimpleListProperty<Tab> listProperty = new SimpleListProperty<> (tempoTabPane.getTabs());
        editMenu.disableProperty().bind(listProperty.emptyProperty());
        saveMenuItem.disableProperty().bind(listProperty.emptyProperty());
        saveAsMenuItem.disableProperty().bind(listProperty.emptyProperty());
        closeMenuItem.disableProperty().bind(listProperty.emptyProperty());
        scanButton.disableProperty().bind(listProperty.emptyProperty());
        parseButton.disableProperty().bind(listProperty.emptyProperty());
        stopButton.disableProperty().bind(listProperty.emptyProperty());
        compileAndRunButton.disableProperty().bind(listProperty.emptyProperty());

        this.setupContextMenuController();

    }

    /**
     * Creates a reference to the ContextMenuController and passes in window items and other sub Controllers when necessary.
     * this method is from JianQuanMarcello project 6
     */
    private void setupContextMenuController() {
        this.contextMenuController = new ContextMenuController();
        this.contextMenuController.setFileMenuController(this.fileController);
        this.contextMenuController.setEditMenuController(this.editController);

        this.fileController.setContextMenuController(this.contextMenuController);
    }


    /**
     * Helper method that calls either Compile or Compile and Run
     * @throws InterruptedException
     */
    @FXML
    public void handleCompileAndRun() throws InterruptedException{
        this.toolBarController.handleCompileAndRun();
    }

    /**
     * handles the stopping of a program
     */
    @FXML public void handleStop(){
        this.toolBarController.handleStopButtonAction();
        this.toolBarController = new ToolBarController(this.console, this.fileController);
    }

    /**
     * This method clears the console, tries to scan
     * and will write any errors to the console
     * @param event press of the Scan button triggering this method
     */
    @FXML
    public void handleScan(Event event) {
        this.console.clear();
        try {
            // calls the handleScan method in file controller
            this.fileController.handleScan(event);
        } catch (CompilationException e) { // catches a compilation exception
            this.console.writeLine(e.toString() + "\n", "ERROR");
            return;
        }

        // gets the list of scanning errors
        List<Error> scanningErrors = fileController.getScanningErrors();
        if (scanningErrors != null) {
            // loops through the errors in scanningErrors and prints them to the console
            for (Error e : scanningErrors)
                this.console.writeLine(e.toString() + "\n", "ERROR");
            this.console.writeLine(scanningErrors.size() + " illegal tokens were found.", "ERROR");
        }
    }

    /**
     * handles the parsing of a program, catches any compilationExceptions
     * from scanning
     * @param event
     */
    @FXML
    public void handleParse(Event event){
        this.console.clear();
        List<Error> parsingErrors = new ArrayList<>();
        try {
            this.fileController.handleScanAndParse(event);
            parsingErrors = fileController.getParsingErrors();
        } catch (CompilationException e) { // catches a scanning error and does not allow piece to be parsed
            this.console.writeLine("Error Found while scanning: " + e.getMessage() + "\n", "ERROR");
            for(Error error: parsingErrors){
                this.console.writeLine(error.toString(), "ERROR");
            }
            return;
        }
    }

    /**
     * Calls toggleSingleComment from the Edit Controller
     *
     */
    @FXML public void handleCommenting() {
        editController.handleCommenting();
    }

    /**
     * Calls handleTabbing from the Edit Controller
     *
     */
    @FXML public void handleTabbing() {
        editController.handleTabbing();
    }

    /**
     * Calls handleUnTabbing from the Edit Controller
     */
    @FXML public void handleUnTabbing() {
        editController.handleUnTabbing();
    }


    /**
     * error helper method to find illegal tokens and report to them to the console
     * @param scanningErrors
     */
    private void errorHelper(List<Error> scanningErrors){
        for (Error e : scanningErrors)
            this.console.writeLine(e.toString() + "\n", "ERROR");

        this.console.writeLine(scanningErrors.size() +
                " illegal tokens were found.", "ERROR");

    }

    /**
     * Handler for the "About" menu item in the "File" menu.
     * Creates an Information alert dialog to display author and information of this program
     */
    @FXML public void handleAbout() {
        fileController.handleAbout();
    }

    /**
     * Handler for the "New" menu item in the "File" menu.
     * Adds a new Tab to the TabPane, and also adds null to the HashMap
     * Also sets the current tab for both the file and edit controllers.
     */
    @FXML public void handleNew() {
        fileController.handleNew( null ); // TODO: decide whether to create a new File object or not here
    }

    /**
     * Handler for the "Open" menu item in the "File" menu.
     * Creates a FileChooser to select a file
     * Use scanner to read the file and write it into a new tab.
     * Also sets the current tab for both the file and edit controllers.
     */
    @FXML public void handleOpen() {
        fileController.handleOpen();
    }

    /**
     * Handler for the "Close" menu item in the "File" menu.
     * Checks to see if the file has been changed since the last save.
     * If changes have been made, redirect to askSave and then close the tab.
     * Otherwise, just close the tab.
     */
    @FXML public void handleClose(Event event) {
        fileController.handleClose(event);
    }

    /**
     * Handler for the "Save" menu item in the "File" menu.
     * If the current tab has been saved before, writes out the content to its corresponding
     * file in storage.
     * Else if the file has never been saved, opens a pop-up window that allows the user to
     * choose a filename and directory and then store the content of the tab to storage.
     */
    @FXML public void handleSave() {
        fileController.handleSave();
    }

    /**
     * Handler for the "Save as..." menu item in the "File" menu.
     * Opens a pop-up window that allows the user to choose a filename and directory.
     * Calls writeFile to save the file to memory.
     * Changes the name of the current tab to match the newly saved file's name.
     */
    @FXML public void handleSaveAs( ) {
        fileController.handleSaveAs();
    }

    /**
     * Handler for the "Exit" menu item in the "File" menu.
     * Closes all the tabs using handleClose()
     * Returns when the user cancels exiting any tab.
     */
    @FXML public void handleExit(Event event) {
        this.fileController.handleExit(event);
    }

    /**
     * Handler for the "Undo" menu item in the "Edit" menu.
     */
    @FXML
    public void handleUndo() {
        editController.handleUndo();
    }

    /**
     * Handler for the "Redo" menu item in the "Edit" menu.
     */
    @FXML
    public void handleRedo() {
        editController.handleRedo(); }

    /**
     * Handler for the "Cut" menu item in the "Edit" menu.
     */
    @FXML
    public void handleCut() {
        editController.handleCut(); }

    /**
     * Handler for the "Copy" menu item in the "Edit" menu.
     */
    @FXML
    public void handleCopy() {
        editController.handleCopy();}

    /**
     * Handler for the "Paste" menu item in the "Edit" menu.
     */
    @FXML
    public void handlePaste() {
        editController.handlePaste(); }

    @FXML
    public void handleMeasureLine() {
        editController.handleMeasureLine();
    }

    /**
     * Handler for the "SelectAll" menu item in the "Edit" menu.
     */
    @FXML
    public void handleSelectAll() {
        editController.handleSelectAll();
    }

    /**
     * Changes the theme of the IDE to Dark
     */
    @FXML
    public void handleDarkMode(){
       handleThemeChange("proj18AhnSlager/resources/DarkMode.css", darkModeMenuItem);
    }

    /**
     * Changes the theme of the IDE back to normal
     */
    @FXML
    public void handleNormalMode(){
        vBox.getStylesheets().remove(vBox.getStylesheets().size()-1);
        enableUnselectedThemes(normalModeMenuItem);
    }

    /**
     * Changes the theme of the IDE to Fun Mode
     */
    @FXML
    public void handleFunMode(){
        handleThemeChange("proj18AhnSlager/resources/FunMode.css", funModeMenuItem);
    }

    /**
     * Changes the theme of the IDE to HallowTheme--
     * a fun Halloween extra!
     */
    @FXML
    public void handleHallowThemeMode(){
        handleThemeChange("proj18AhnSlager/resources/HallowTheme.css", hallowThemeItem);
    }

    /**
     * Helper method to change the theme
     * @param themeCSS
     */
    private void handleThemeChange(String themeCSS, MenuItem menuItem){
        if(vBox.getStylesheets().size() > 1){
            vBox.getStylesheets().remove(vBox.getStylesheets().size()-1);
        }
        vBox.getStylesheets().add(themeCSS);
        enableUnselectedThemes(menuItem);
    }

    /**
     * Enables the menu items of themes that aren't currently used and
     * disables the menu item of the theme that is currently on
     * display
     *
     * @param menuItem the menu item that needs to be disabled
     */
    private void enableUnselectedThemes(MenuItem menuItem){
        for(MenuItem item: prefMenu.getItems()){
            if(!item.equals(menuItem)){
                item.setDisable(false);
            }
            else{
                item.setDisable(true);
            }
        }
    }

    /**
     * Code for handleOpenJavaTutorial obtained from Li-Lian-KeithHardy-Zhou
     * Handler for the "Java Tutorial" menu item in the "Help" Menu.
     * When the item is clicked, a Java tutorial will be opened in a browser.
     */
    @FXML
    public void handleOpenMusicTutorial(){
        try {
            URI url = new URI("https://docs.google.com/document/d/1iL1nmyoy1NovxdhRcHcG10QKRLv1RV8dGyJ4DvsmK9s/edit?usp=sharing");
            Desktop.getDesktop().browse(url);
        } catch (IOException|URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calls handleMatchBracketOrParen() of the editController
     */
    @FXML
    public void handleMatchBracketOrParen() {
        editController.handleMatchBracketOrParen();
    }

    /**
     * Calls handleFindText() of the editController
     */
    @FXML
    public void handleFindText() {
        editController.handleFindText(true);
    }

    /**
     * Calls handleHighlightPrevMatch() of the editController
     */
    @FXML
    public void handleHighlightPrevMatch() {
        editController.handleHighlightPrevMatch();
    }

    /**
     * Calls handleHighlightNextMatch() of the editController
     */
    @FXML
    public void handleHighlightNextMatch() {
        editController.handleHighlightNextMatch();
    }

    /**
     * Focuses on the Find Text Entry Box
     */
    @FXML
    public void handleFocusOnFindTextEntry() {
        this.findTextEntry.requestFocus();
    }

}