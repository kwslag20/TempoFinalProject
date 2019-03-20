/*
 * File: Refactor.java
 * Names: Kevin Ahn, Kyle Slager
 * Class: CS461
 * Project 13
 */

package proj15AhnSlagerZhou;

import javafx.scene.control.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.NavigationActions;
import proj15AhnSlagerZhou.bantam.ast.Program;
import proj15AhnSlagerZhou.bantam.semant.*;

import java.util.*;

/**
 * @author Kevin Ahn, Kyle Slager
 * A refactoring class that sets up a window that sets up the UI to display the
 * classes, methods ands fields of a class to refactor them if the user decides to
 */
public class Refactor {

    private EditController editController;
    private Program parseRoot;

    private Iterator<int[]> indices = new ArrayList<int[]>().iterator();
    private String target, textToSearch;

    /**
     *
     * @return the target
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * sets the target
     * @param target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     *
     * @return the text to search
     */
    public String getTextToSearch() {
        return this.textToSearch;
    }

    /**
     * sets the text to search
     * @param text
     */
    public void setTextToSearch(String text) {
        this.textToSearch = text;
    }

    /**
     * @return a boolean that indicates whether or not the FindAndReplaceWidget's "indices" field, which
     * is an iterator, is empty or not.
     */
    public boolean isEmpty() {
        return !indices.hasNext();
    }


    /**
     * constructor for the class
     * @param editController editController to be used by the class
     * @param parseRoot the program ast
     */
    public Refactor(EditController editController, Program parseRoot){
        this.editController = editController;
        this.parseRoot = parseRoot;
    }

    /**
     * a method that initializes the window that will hold the information from the AST.
     * Creates a set of buttons that will redirect the user to the list of classes, methods,
     * or fields
     */
    public void initializeRefactor(String type){
        if(type.equals("class")){
            getClasses("Refactor");
        }
        else if(type.equals("method")){
            getMethods("Refactor");
        }
        else{
            getFields("Refactor");
        }
    }

    /**
     * intializes the jumpTo based on what type is passed into the method
     * @param type the type being jumped to
     */
    public void initializeJumpTo(String type){
        if(type.equals("class")){
            getClasses("JumpTo");
        }
        else if(type.equals("method")){
            getMethods("JumpTo");
        }
        else{
            getFields("JumpTo");
        }
    }

    /**
     * initialez what info to get
     * @param type
     */
    public void initializeDependencies(String type){
        if(type.equals("class")){
            getClasses("AnDep");
        }
        else if(type.equals("method")){
            getMethods("AnDep");
        }
        else{
            getFields("AnDep");
        }
    }

    /**
     * method to get the list of classes in the current file, creates a new ClassVisitor
     * to get the list of name and adds them to an arrayList.
     */
    public void getClasses(String type){
        ClassVisitor classVisitor = new ClassVisitor(); // creates the visitor
        ArrayList<String> names = classVisitor.getClasses(this.parseRoot, "null", "null");
        if(type.equals("Refactor")) {
            this.getHelper(names, "Classes");
        }
        else if(type.equals("AnDep")){
            this.analyzeDependencies(names, "Classes");
        }
        else {
            this.selectNext(names, "Classes");
        }
    }

    /**
     * method to get the list of methods in the current file, creates a new MethodVisitor
     * to get the list of names and adds them to an arrayList.
     */
    public void getMethods(String type){
        MethodVisitor methodVisitor = new MethodVisitor(); // creates the method visitor
        ArrayList<String> names = methodVisitor.getMethods(this.parseRoot,"null", "null");
        if(type.equals("Refactor")) {
            this.getHelper(names, "Methods");
        }
        else if(type.equals("AnDep")){
            this.analyzeDependencies(names, "Methods");
        }
        else {
            this.selectNext(names, "Methods");
        }
    }

    /**
     * method to get the list of fields in the current file, creates a new FieldVisitor
     * to get the list of names and adds them to an arrayList.
     */
    public void getFields(String type){
        FieldVisitor fieldVisitor = new FieldVisitor();
        ArrayList<String> names = fieldVisitor.getFields(this.parseRoot, "null", "null");
        if(type.equals("Refactor")) {
            this.getHelper(names, "Fields");
        }
        else if(type.equals("AnDep")){
            this.analyzeDependencies(names, "Fields");
        }
        else {
            this.selectNext(names, "Fields");
        }
    }

    /**
     * sets up the dialog created
     * @param names
     * @param type
     */
    public void getHelper(ArrayList names, String type) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(type, names);
        dialog.setTitle("Refactor");
        dialog.setHeaderText("Welcome to Refactoring Helper");
        dialog.setContentText("Choose what you would like to refactor:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            getNewName(result.get(), type);
        }
    }

    /**
     * gets the new name set from the text input
     * @param oldName the old name
     * @param type method, field, or class
     */
    public void getNewName(String oldName, String type){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Choose New Name");
        dialog.setHeaderText("Welcome to Refactoring Helper");
        dialog.setContentText("Choose a new name");

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            String newName = result.get();
            refactorAll(oldName, newName, type);
        }
    }

    /**
     * method that handles the refactoring using the visitor pattern
     * @param oldName
     * @param newName
     * @param type class, method, or field
     */
    public void refactorAll(String oldName, String newName, String type){

        String source = editController.getCurJavaCodeArea().getText();
        String newText     = source.replace(oldName, newName);

        editController.handleSelectAll();
        editController.getCurJavaCodeArea().replaceSelection(newText);
        if(type.equals("Classes")){
            ClassVisitor classVisitor = new ClassVisitor();
            classVisitor.getClasses(this.parseRoot, newName, oldName);
        }
        else if(type.equals("Methods")){
            MethodVisitor methodVisitor = new MethodVisitor();
            methodVisitor.getMethods(this.parseRoot, newName, oldName);
        }
        else{
            FieldVisitor fieldVisitor = new FieldVisitor();
            fieldVisitor.getFields(this.parseRoot, newName, oldName);
        }
    }

    /**
     * this method is used for finding all of the locations of a given substring in a given
     * source string
     * @param source the source text which is being examined for the given substring
     * @param substring the substring that is being searched for in the given source String
     * @return a list of int arrays, where each int array represents the start and end location
     *         of a target substring in the original source String
     */
    private List<int[]> findAllIndices(String source, String substring) {
        List<int[]> result = new ArrayList<>();
        return findAllIndicesHelper(result, 0, source, substring);
    }

    /**
     * helper method to create an iterator and store it in the indices field
     * @param source the source text that is being searched
     *               (usually all of the text contained in the current text area)
     * @param target the target string to be searched for
     */
    private void createIteratorFrom(String source, String target) {
        this.indices = findAllIndices(source, target).iterator();
    }

    /**
     * Helper method.
     * @return the next int[] in the object's "indices" iterator. Returns null if the iterator is empty.
     */
    private int[] getNextRange() {
        if(!this.isEmpty()) {
            return indices.next();
        }
        return null;
    }

    /**
     * @param source the string that is being searched for a given substring
     * @param substring the substring being searched for in the source string
     * @return a two element int[] array that contains the start and end indices of the target string
     * in the source, where the first element of the array is the start location, and the second element
     * is the end location. If the given source string does not contain the given substring,
     * the returned array will contain the values [-1, -1]
     */
    private int[] indicesOf(String source, String substring) {
        int startOfSubstring = source.indexOf(substring);
        if(startOfSubstring == -1 || "".equals(substring)) {
            return new int[] {-1, -1};
        }
        return new int[] {startOfSubstring, startOfSubstring + substring.length()};
    }

    /**
     * A recursive helper method for the findAllIndices method. Returns a
     * @param idxAccumulator a List where the int[]s are accumulated
     * @param numPreviousChars the numbers of previous characters that have been examined
     *                         in search of the target substring
     * @param source the source that is being examined for the target substring
     * @param substring the target substring which is being searched for
     * @return a list of int arrays, where each int array represents the start and end location
     * of a target substring in the original source String
     */
    private List<int[]> findAllIndicesHelper(
            List<int[]> idxAccumulator, int numPreviousChars,
            String source, String substring) {

        int[] indicesOfSubstring = indicesOf(source, substring);
        int start = indicesOfSubstring[0];
        int end   = indicesOfSubstring[1];

        if (start == -1) {
            return idxAccumulator;
        }

        idxAccumulator.add(new int[] {start + numPreviousChars, end + numPreviousChars});
        String unsearchedText = source.substring(end, source.length());
        int numCharsSearched  = numPreviousChars + end;

        return findAllIndicesHelper(idxAccumulator, numCharsSearched, unsearchedText, substring);
    }

    /**
     * Deals with selecting the first instance of of the target
     * @param names
     * @param type
     */
    private void selectNext(ArrayList names, String type) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(type, names);
        dialog.setTitle("Jump To");
        dialog.setHeaderText("Welcome to Jump To");
        dialog.setContentText("Choose where you would like to jump:");

        Optional<String> result = dialog.showAndWait();

        CodeArea currentCodeArea   = editController.getCurJavaCodeArea();
        String   currentText       = currentCodeArea.getText();
        String   currentTarget     = "";
        if(result.isPresent()){
            if (type.equals("Fields")) {
                currentTarget = result.get() + ";";
            }
            else if (type.equals("Methods")){
                currentTarget = result.get() + "(";
            }
            else{
                currentTarget = result.get() + "{";
            }
        }
        boolean  targetHasChanged  = !Objects.equals(this.getTarget(), currentTarget);
        boolean  srcTextHasChanged = !Objects.equals(this.getTextToSearch(), currentText);

        if(isEmpty() || targetHasChanged || srcTextHasChanged) {
            createIteratorFrom(currentText, currentTarget);
            setTarget(currentTarget);
            setTextToSearch(currentText);
        }
        int[] range = getNextRange();
        if(range != null) {
            // select the text in in the given range of the current code area
            currentCodeArea.moveTo(range[0]);
            currentCodeArea.moveTo(range[1], NavigationActions.SelectionPolicy.EXTEND);
        }
    }

    /**
     * method for creating the dialog to handle the dependency analyzer
     * @param names
     * @param type
     */
    private void analyzeDependencies(ArrayList names, String type){
        ArrayList dependenciesList = new ArrayList();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(type, names);
        dialog.setTitle("Analyze Dependencies");
        dialog.setHeaderText("Welcome to Analyze Dependencies");
        dialog.setContentText("Choose where you would like to jump");
        DependencyVisitor dependencyVisitor = new DependencyVisitor();
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            String choice = result.get();
            dependenciesList = dependencyVisitor.initialize(this.parseRoot, choice);
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Analyze Dependencies");
        alert.setHeaderText("The following variables are dependent on " + result.get());
        String content = "";
        for(Object a: dependenciesList){
            content = content + a.toString() + "\n";
        }
        alert.setContentText(content);
        alert.showAndWait();




    }
}
