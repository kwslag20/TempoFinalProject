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

package proj16AhnSlagerZhao;


import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.Event;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Desktop;
import java.io.IOException;
import java.util.List;

import proj16AhnSlagerZhao.bantam.ast.Program;
import proj16AhnSlagerZhao.bantam.util.CompilationException;
import proj16AhnSlagerZhao.bantam.util.Error;
import proj16AhnSlagerZhao.bantam.util.ErrorHandler;

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
    @FXML private JavaTabPane javaTabPane;
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
    @FXML private TextField replaceTextEntry;
    @FXML private Menu prefMenu;
    @FXML private Button assembleButton;
    @FXML private Button assembleAndRunButton;
    @FXML private Button stopButton;
    @FXML private Button compileButton;


    private EditController editController;
    private FileController fileController;
    private ToolBarController toolBarController;
    private Refactor refactor;
    private ErrorHandler errorHandler;
    private Program parseRoot;


    // this line from JianQuanMarcello project 6
    private ContextMenuController contextMenuController;

    @FXML
    public void initialize(){

        editController = new EditController(javaTabPane, findTextEntry, findPrevBtn, findNextBtn, replaceTextEntry);
        this.fileController = new FileController(vBox,javaTabPane);
        this.toolBarController = new ToolBarController(console, fileController);
        this.errorHandler = new ErrorHandler();
        this.parseRoot = null;

        SimpleListProperty<Tab> listProperty = new SimpleListProperty<Tab> (javaTabPane.getTabs());
        editMenu.disableProperty().bind(listProperty.emptyProperty());
        saveMenuItem.disableProperty().bind(listProperty.emptyProperty());
        saveAsMenuItem.disableProperty().bind(listProperty.emptyProperty());
        closeMenuItem.disableProperty().bind(listProperty.emptyProperty());


        assembleButton.disableProperty().bind(listProperty.emptyProperty());
        assembleAndRunButton.disableProperty().bind(listProperty.emptyProperty());
        stopButton.disableProperty().bind(listProperty.emptyProperty());

        // this line from JianQuanMarcello project 6
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

    @FXML public void handleAssemble(Event event) throws InterruptedException{
        this.console.clear();
        try {
            this.toolBarController.handleAssembleAction(event, this.fileController.getFilePath());
        } catch (CompilationException e) {
            this.console.writeLine(e.toString() + "\n", "ERROR");
            return;
        }
        List<Error> scanningErrors = fileController.getErrors();

        if (scanningErrors != null) {
            errorHelper(scanningErrors);
        }
        else{
            this.console.writeLine("Assembly of file was successful.", "CONS");

        }
        this.toolBarController = new ToolBarController(this.console, this.fileController);
    }

    @FXML public void handleAssembleAndRun(Event event) throws InterruptedException{
        this.console.clear();
        try {

            this.toolBarController.handleRunAction(event, this.fileController.getFilePath());
        } catch (CompilationException e) {
            this.console.writeLine(e.toString() + "\n", "ERROR");
            return;
        }

        List<Error> scanningErrors = fileController.getErrors();

        if (scanningErrors != null) {
            errorHelper(scanningErrors);
        }
        else{
            this.console.writeLine("Assembly of file was successful.", "CONS");
        }
        this.toolBarController = new ToolBarController(this.console, this.fileController);
    }

    @FXML public void handleStop(){
        this.toolBarController.handleStopButtonAction();
        this.toolBarController = new ToolBarController(this.console, this.fileController);
    }


    /**
     *
     * @param e
     * @throws InterruptedException
     */
    @FXML public void handleCompile(Event e) throws InterruptedException{
        this.toolBarController.handleCompile(e, errorHandler);
        File file = new File(this.fileController.getFilePath().replace("btm", "asm"));
        this.fileController.handleNew(file);
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


    private void errorHelper(List<Error> scanningErrors){
        for (Error e : scanningErrors)
            this.console.writeLine(e.toString() + "\n", "ERROR");

        this.console.writeLine(scanningErrors.size() +
                " illegal tokens were found.", "ERROR");

    }

    @FXML public void handleScanParseAndCheck(Event event ) throws InterruptedException {
        this.console.clear();
        try {
            this.fileController.handleAnalyze(event);
        } catch (CompilationException e) {
            this.console.writeLine(e.toString() + "\n", "ERROR");

            return;
        }

        List<Error> scanningErrors = fileController.getAnalysisErrors();

        if (scanningErrors != null) {

            errorHelper(scanningErrors);
        }
        else{
            this.console.writeLine("Parse of file was successful.", "CONS");

        }
    }

    /**
     * handles the refactoring of a class
     * @param event
     */
    @FXML public void handleRefactorClass(Event event){
        if(this.parseRoot != null){
            this.editController.handleRefactor(this.parseRoot, "class");
        }
    }

    /**
     * handles the refactoring of a method
     * @param event
     */
    @FXML public void handleRefactorMethod(Event event){
        if(this.parseRoot != null){
            this.editController.handleRefactor(this.parseRoot, "method");
        }
    }

    /**
     * handles the refactoring of a field
     * @param event
     */
    @FXML public void handleRefactorField(Event event){
        if(this.parseRoot != null){
            this.editController.handleRefactor(this.parseRoot, "field");
        }
    }

    /**
     * handles the jumping to a class
     * @param event
     */
    @FXML public void handleJumpToClass(Event event){
        if(this.parseRoot != null){
            this.editController.handleJumpTo(this.parseRoot, "class");
        }
    }

    /**
     * handles the jumping to a method
     * @param event
     */
    @FXML public void handleJumpToMethod(Event event){
        if(this.parseRoot != null){
            this.editController.handleJumpTo(this.parseRoot, "method");
        }
    }

    /**
     * handles the jumping to a field
     * @param event
     */
    @FXML public void handleJumpToField(Event event){
        if(this.parseRoot != null){
            this.editController.handleJumpTo(this.parseRoot, "field");
        }
    }

    /**
     * handles the analyzing of dependencies of a class
     * @param event
     */
    @FXML public void handleAnDepClass(Event event){
        if(this.parseRoot != null){
            this.editController.handleAnDep(this.parseRoot, "class");
        }
    }

    /**
     * handles the analyzing of dependencies of a method
     * @param event
     */
    @FXML public void handleAnDepMethod(Event event){
        if(this.parseRoot != null){
            this.editController.handleAnDep(this.parseRoot, "method");
        }
    }

    /**
     * handles the analyzing of dependencies of a field
     * @param event
     */
    @FXML public void handleAnDepField(Event event){
        if(this.parseRoot != null){
            this.editController.handleAnDep(this.parseRoot, "field");
        }
    }
    
    /**
     * Handles the find and replace button action.
     * Opens a find and replace popup window.
     */
    @FXML
    private void handleFindAndReplace() { editController.handleFindAndReplace(); }

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
        assembleButton.disableProperty().bind(this.fileController.isMipsFile());
        assembleAndRunButton.disableProperty().bind(this.fileController.isMipsFile());
        stopButton.disableProperty().bind(this.fileController.isMipsFile());
    }

    /**
     * Handler for the "Open" menu item in the "File" menu.
     * Creates a FileChooser to select a file
     * Use scanner to read the file and write it into a new tab.
     * Also sets the current tab for both the file and edit controllers.
     */
    @FXML public void handleOpen() {
        fileController.handleOpen();
        assembleButton.disableProperty().bind(this.fileController.isMipsFile());
        assembleAndRunButton.disableProperty().bind(this.fileController.isMipsFile());
        stopButton.disableProperty().bind(this.fileController.isMipsFile());

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
        assembleButton.disableProperty().bind(this.fileController.isMipsFile());
        assembleAndRunButton.disableProperty().bind(this.fileController.isMipsFile());
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
    public void handleUndo() { editController.handleUndo(); }

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

    /**
     * Handler for the "SelectAll" menu item in the "Edit" menu.
     */
    @FXML
    public void handleSelectAll() {
        editController.handleSelectAll(); }

    /**
     * Changes the theme of the IDE to Dark
     */
    @FXML
    public void handleDarkMode(){
       handleThemeChange("proj16AhnSlagerZhao/resources/DarkMode.css", darkModeMenuItem);
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
        handleThemeChange("proj16AhnSlagerZhao/resources/FunMode.css", funModeMenuItem);
    }

    /**
     * Changes the theme of the IDE to HallowTheme--
     * a fun Halloween extra!
     */
    @FXML
    public void handleHallowThemeMode(){
        handleThemeChange("proj16AhnSlagerZhao/resources/HallowTheme.css", hallowThemeItem);
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
    public void handleOpenJavaTutorial(){
        try {
            URI url = new URI("https://docs.oracle.com/javase/tutorial/");
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
     * Calls handleReplaceText() of the editController
     */
    @FXML
    public void handleReplaceText() {editController.handleReplaceText(); }

    /**
     * Focuses on the Find Text Entry Box
     */
    @FXML
    public void handleFocusOnFindTextEntry() {
        this.findTextEntry.requestFocus();
    }

    /**
     * Focuses on the Replace Text Extry Box
     */
    @FXML
    public void handleFocusOnReplaceTextEntry() {
        this.replaceTextEntry.requestFocus();
    }
}