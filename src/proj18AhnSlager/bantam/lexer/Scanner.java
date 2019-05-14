/*
 * File: MUSICScanner.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 * ---------------------------
 * Edited From: Dale Skrien
 */

package proj18AhnSlager.bantam.lexer;

import proj18AhnSlager.bantam.util.CompilationException;
import proj18AhnSlager.bantam.util.ErrorHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import proj18AhnSlager.bantam.util.Error;


/**
 * This is the scanner class whose main responsibility is to
 * produce tokens from a Source File Object
 * as well as return
 *
 * @author Dale Skrien
 * @author Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * @version 1.0
 * @since 11-20-2018
 */
public class Scanner
{
    private SourceFile sourceFile;
    private ErrorHandler errorHandler;
    private Character currentChar;
    private Boolean isNote, isLayout, isSequences, wasSequences;
    private String curString;
    private Token tempToken;
    private ArrayList<String> sections;
    private String[] layoutArray;
    private int layoutCheck;
    private int loopCount;
    private ArrayList<String> seqList;

    /**
     *
     * @param handler an ErrorHandler
     */
    public Scanner(ErrorHandler handler) {
        errorHandler = handler;
        currentChar = ' ';
        sourceFile = null;
        curString = "";
    }

    /**
     *
     * @param filename the name of the file that will be passed to the SourceFile
     * @param handler an ErrorHandler
     */
    public Scanner(String filename, ErrorHandler handler) {
        errorHandler = handler;
        currentChar = ' ';
        curString = "";
        isNote = false;
        isLayout = false;
        isSequences = false;
        wasSequences = false;
        sections = new ArrayList<>();
        seqList = new ArrayList<>();
        try {
            sourceFile = new SourceFile(filename);
        }
        catch (CompilationException e){
            throw e;

        }
    }

    /**
     *
     * @param reader a Reader linked to existing File to be passed to the SourceFile
     * @param handler an ErrorHandler
     */
    public Scanner(Reader reader, ErrorHandler handler) {
        errorHandler = handler;
        sourceFile = new SourceFile(reader);
    }


    /** Each call of this method builds the next Token from the contents
     * of the file being scanned and returns it. When it reaches the end of the
     * file, any calls to scan() result in a Token of kind EOF.
     */
    public Token scan() {
        Character tempChar = currentChar;
        if (currentChar.equals(SourceFile.eof)) return new Token(Token.Kind.EOF,
                currentChar.toString(), this.sourceFile.getCurrentLineNumber());

        //gets rid of whitespace
        else {
            while(currentChar.equals('\t') || currentChar.equals('\r')
                    || currentChar.equals('\n') || currentChar.equals('\f') || currentChar.equals(' ')) {

                currentChar = this.sourceFile.getNextChar();
                tempChar = currentChar;
            }
        }

        if(isLayout){
            while(layoutCheck != layoutArray.length - 1){
                String layoutItem = layoutArray[layoutCheck];
                if(layoutItem.contains("instrument")){
                    layoutItem.replace("instrument: ", "");
                    layoutCheck++;
                    return new Token(Token.Kind.INSTRUMENT, layoutItem, this.sourceFile.getCurrentLineNumber());
                }
                else if(layoutItem.contains("writer")){
                    layoutItem.replace("writer: ", "");
                    layoutCheck++;
                    return new Token(Token.Kind.WRITER, layoutItem, this.sourceFile.getCurrentLineNumber());
                }
                else if(layoutItem.contains("tempo")){
                    layoutItem.replace("tempo: ", "");
                    layoutCheck++;
                    return new Token(Token.Kind.TEMPO, layoutItem, this.sourceFile.getCurrentLineNumber());
                }
                else {
                    for (String section : sections) {
                        if (layoutItem.contains(section)) {
                            layoutCheck++;
                            return new Token(Token.Kind.ORDEROBJ, section, this.sourceFile.getCurrentLineNumber());
                        }
                    }
                }
                loopCount++;
                if(loopCount > layoutArray.length - 1){
                    layoutCheck = layoutArray.length - 1;
                    errorHandler.register(Error.Kind.PARSE_ERROR, "Improper Layout Token Found");
                    return new Token(Token.Kind.ERROR, "Improper Layout Token Found", this.sourceFile.getCurrentLineNumber());
                }
            }
            isLayout = false;
            return new Token(Token.Kind.NOTWORD, "nothing", this.sourceFile.getCurrentLineNumber());
        }
        else if(isSequences){
            String sequenceName = "";
            while (currentChar != '[' && currentChar != '}') {
                sequenceName += currentChar;
                currentChar = this.sourceFile.getNextChar();
            }
            isSequences = false;
            if(sequenceName == ""){
                return new Token(Token.Kind.NOTWORD, sequenceName, this.sourceFile.getCurrentLineNumber());
            }
            seqList.add(sequenceName);
            return new Token(Token.Kind.SEQ, sequenceName, this.sourceFile.getCurrentLineNumber());
        }
        else {
            switch (tempChar) {
                case (SourceFile.eof):
                    return new Token(Token.Kind.EOF,
                            currentChar.toString(), this.sourceFile.getCurrentLineNumber());
                case ('/'): return this.getCommentToken();
                case ('g'):
                case ('f'):
                case ('e'):
                case ('d'):
                case ('c'):
                case ('b'):
                case ('a'):
                    tempToken = checkCurString(tempChar);
                    currentChar = this.sourceFile.getNextChar();
                    if (isNote && currentChar != 'h' && currentChar != '(') {
                        if(currentChar == '#'){
                            currentChar = this.sourceFile.getNextChar();
                            return new Token(Token.Kind.PITCH, tempChar.toString() + "#", this.sourceFile.getCurrentLineNumber());
                        }
                        return new Token(Token.Kind.PITCH, tempChar.toString(), this.sourceFile.getCurrentLineNumber());
                    } else {
                        return tempToken;
                    }
                case ('w'):
                case ('h'):
                case ('q'):
                case ('i'):
                case ('s'):
                case ('t'):
                case ('x'):
                    tempToken = checkCurString(tempChar);
                    currentChar = this.sourceFile.getNextChar();
                    if (currentChar.equals('n') && tempToken.kind == Token.Kind.NOTWORD) {
                        currentChar = sourceFile.getNextChar();
                        isNote = true;
                        curString = "";
                        return new Token(Token.Kind.NOTE, tempChar.toString() + 'n', this.sourceFile.getCurrentLineNumber());
                    }
                    else if (currentChar.equals('r') && tempToken.kind == Token.Kind.NOTWORD) {
                        currentChar = sourceFile.getNextChar();
                        curString = "";
                        return new Token(Token.Kind.REST, tempChar.toString() + 'r', this.sourceFile.getCurrentLineNumber());
                    }
                    else{
                        return tempToken;

                    }


                case ('-'):
                    return this.getMinusToken();

                case ('{'):
                    tempToken = checkCurString(tempChar);
                    currentChar = sourceFile.getNextChar();
                    String invalidString = curString;
                    curString = "";
                    isNote = false;
                    if (tempToken.kind != Token.Kind.NOTWORD) {
                        return tempToken;
                    } else {
                        errorHandler.register(Error.Kind.LEX_ERROR, "Invalid usage of " + invalidString);
                        return new Token(Token.Kind.ERROR, "Invalid Usage of " + invalidString, this.sourceFile.getCurrentLineNumber());
                    }

                case ('}'):
                    currentChar = sourceFile.getNextChar();
                    wasSequences = false;
                    isSequences = false;
                    curString = "";
                    return new Token(Token.Kind.RCURLY,
                            tempChar.toString(), this.sourceFile.getCurrentLineNumber());

                case ('('):
                    tempToken = checkCurString(tempChar);
                    currentChar = sourceFile.getNextChar();
                    String invalidString2 = curString;
                    curString = "";
                    if (tempToken.kind != Token.Kind.NOTWORD) {
                        return tempToken;
                    } else {
                        errorHandler.register(Error.Kind.LEX_ERROR, "Invalid usage of " + invalidString2);
                        return new Token(Token.Kind.ERROR, "Invalid Usage of " + curString, this.sourceFile.getCurrentLineNumber());
                    }

                case (')'):
                    currentChar = sourceFile.getNextChar();
                    curString = "";
                    return new Token(Token.Kind.RPAREN,
                            tempChar.toString(), this.sourceFile.getCurrentLineNumber());

                case ('['):
                    currentChar = sourceFile.getNextChar();
                    return new Token(Token.Kind.NOTWORD,
                            tempChar.toString(), this.sourceFile.getCurrentLineNumber());

                case (']'):
                    currentChar = sourceFile.getNextChar();
                    curString = "";
                    if(wasSequences) isSequences = true;
                    return new Token(Token.Kind.RBRACKET, tempChar.toString(), this.sourceFile.getCurrentLineNumber());

                case (':'):
                    currentChar = sourceFile.getNextChar();
                    return new Token(Token.Kind.NOTWORD, tempChar.toString(), this.sourceFile.getCurrentLineNumber());

                case (';'):
                    currentChar = sourceFile.getNextChar();
                    curString = "";
                    isNote = false;
                    return new Token(Token.Kind.SEMICOLON,
                            tempChar.toString(), this.sourceFile.getCurrentLineNumber());

                case ('.'):
                    currentChar = sourceFile.getNextChar();
                    curString = "";
                    isNote = false;
                    return new Token(Token.Kind.DOT,
                            tempChar.toString(), this.sourceFile.getCurrentLineNumber());

                case (','):
                    currentChar = sourceFile.getNextChar();
                    curString = "";
                    isNote = false;
                    return new Token(Token.Kind.COMMA,
                            tempChar.toString(), this.sourceFile.getCurrentLineNumber());

                default:

                    if (Character.isDigit(currentChar)) return getIntConstToken();
                    else if (Character.isLetter(currentChar)) {
                        tempToken = checkCurString(tempChar);
                        currentChar = this.sourceFile.getNextChar();
                        return tempToken;
                    } else {
                        currentChar = sourceFile.getNextChar();
                        this.errorHandler.register(Error.Kind.LEX_ERROR,
                                this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                                "TOKEN ERROR");
                        return new Token(Token.Kind.ERROR, tempChar.toString(),
                                this.sourceFile.getCurrentLineNumber());
                    }
            }
        }
    }

    private Token checkCurString(Character tempChar) {
        if (curString != null) {
            for(String seq: seqList){
                if(curString.equals(seq)){
                    String seqDef = "";
                    int bailOutCount = 0;
                    while (!currentChar.equals(')')){
                        seqDef += currentChar;
                        currentChar = this.sourceFile.getNextChar();
                        bailOutCount++;
                        if(bailOutCount > 25){
                            this.errorHandler.register(Error.Kind.LEX_ERROR,
                                    this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                                    "Invalid Sequence Call");
                            return new Token(Token.Kind.ERROR, "Missing Right Parenthesis", this.sourceFile.getCurrentLineNumber());
                        }
                    }
                    return new Token(Token.Kind.SEQOBJ, curString + seqDef, this.sourceFile.getCurrentLineNumber());
                }
            }
            switch (curString) {

                case ("chord"):
                    String chordInfo = "";
                    while (!currentChar.equals(')')) {
                        chordInfo += currentChar;
                        currentChar = this.sourceFile.getNextChar();
                    }
                    chordInfo += currentChar;
                    currentChar = this.sourceFile.getNextChar();
                    curString = "";
                    return new Token(Token.Kind.CHORD, chordInfo, this.sourceFile.getCurrentLineNumber());

                case ("piece"):
                    String pieceName = "";
                    while (!currentChar.equals('{')) {
                        pieceName += currentChar;
                        currentChar = this.sourceFile.getNextChar();
                    }
                    curString = "";
                    return new Token(Token.Kind.PIECE, pieceName, this.sourceFile.getCurrentLineNumber());

                case ("sequences"):
                    if(sections.contains("sequences")){
                        errorHandler.register(Error.Kind.PARSE_ERROR, this.sourceFile.getFilename(),
                                this.sourceFile.getCurrentLineNumber(), "Sequences already exists");
                    }
                    sections.add("sequences");
                    currentChar = this.sourceFile.getNextChar();
                    curString = "";
                    isSequences = true;
                    wasSequences = true;
                    return new Token(Token.Kind.SEQUENCES, "sequences", this.sourceFile.getCurrentLineNumber());

                case ("layout"):
                    String layoutText = "";
                    currentChar = this.sourceFile.getNextChar();
                    while(!currentChar.equals('}')){
                        layoutText += currentChar;
                        currentChar = this.sourceFile.getNextChar();
                    }
                    layoutCheck = 0;
                    loopCount = 0;
                    layoutArray = layoutText.split(";");
                    curString = "";
                    if(sections.contains("layout")){
                        errorHandler.register(Error.Kind.SEMANT_ERROR, this.sourceFile.getFilename(),
                                this.sourceFile.getCurrentLineNumber(),  "Layout already exists.");
                    }
                    sections.add("layout");
                    currentChar = this.sourceFile.getNextChar();
                    isLayout = true;
                    return new Token(Token.Kind.LAYOUT, "layout", this.sourceFile.getCurrentLineNumber());

                case ("chorus"):
                    curString = "";
                    if(sections.contains("chorus")){
                        errorHandler.register(Error.Kind.SEMANT_ERROR, this.sourceFile.getFilename(),
                                this.sourceFile.getCurrentLineNumber(),  "Chorus already exists.");
                    }
                    sections.add("chorus");
                    currentChar = this.sourceFile.getNextChar();
                    return new Token(Token.Kind.CHORUS, "chorus", this.sourceFile.getCurrentLineNumber());

                case ("verse"):
                    String verseName = "";
                    while (!currentChar.equals('{')){
                        verseName += currentChar;
                        currentChar = this.sourceFile.getNextChar();
                    }
                    curString = "";
                    if(sections.contains(verseName)){
                        errorHandler.register(Error.Kind.SEMANT_ERROR, this.sourceFile.getFilename(),
                                this.sourceFile.getCurrentLineNumber(), verseName + " already exists.");
                    }
                    sections.add(verseName);
                    return new Token(Token.Kind.VERSE, verseName, this.sourceFile.getCurrentLineNumber());

                case ("righthand"):
                    currentChar = this.sourceFile.getNextChar();
                    curString = "";
                    return new Token(Token.Kind.RIGHTHAND, "righthand", this.sourceFile.getCurrentLineNumber());

                case ("lefthand"):
                    currentChar = this.sourceFile.getNextChar();
                    curString = "";
                    return new Token(Token.Kind.LEFTHAND, "lefthand", this.sourceFile.getCurrentLineNumber());
            }
        }
            curString += tempChar;
            return new Token(Token.Kind.NOTWORD, "doesn't matter", this.sourceFile.getCurrentLineNumber());
    }

    /**
     * Creates and returns a minus token or a unary decrement token
     *
     * @return a token of Kind.PLUSMINUS(-) or UNARYDECR(---)
     */
    private Token getMinusToken() {

        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();
        return new Token(Token.Kind.MINUS, prevChar.toString(),
                    this.sourceFile.getCurrentLineNumber());
    }

    /**
     * Returns an integer constant token, where the integer
     * value does not exceed (2^31 - 1)
     * @return token of Kind.INTCONST or Kind.ERROR
     */
    private Token getIntConstToken() {
        String tempChar = currentChar.toString();
        currentChar = this.sourceFile.getNextChar();
        if(isNote){
            return new Token(Token.Kind.OCTAVE, tempChar, this.sourceFile.getCurrentLineNumber());
        }
        else return new Token(Token.Kind.INTCONST, tempChar, this.sourceFile.getCurrentLineNumber());
    }


    public Token createErrorToken(String spelling){
        return new Token(Token.Kind.ERROR, spelling,
                this.sourceFile.getCurrentLineNumber());
    }

    /**
     *
     * @return a list of errors from the ErrorHandler instance of this class
     */
    public List<Error> getErrors() {
        return this.errorHandler.getErrorList();
    }


    /**
     *
     * @return a token of Kind.COMMENT, Kind.MULDIV or Kind.ERROR
     */
    private Token getCommentToken() {
        Character prevChar = currentChar;
        currentChar = this.sourceFile.getNextChar();
        switch(currentChar) {

            case('/'): return this.getSingleLineCommentToken();

            default: return this.getBlockCommentToken(prevChar);

        }
    }

    /**
     * Creates and returns a single line comment token
     * @return a token of Kind.COMMENT
     */
    private Token getSingleLineCommentToken() {

        String commentBody = "//";
        currentChar = this.sourceFile.getNextChar();    // move to first char after //
        while (!( currentChar.equals(SourceFile.eol) ||
                currentChar.equals(SourceFile.eof) )) {

            commentBody = commentBody.concat(currentChar.toString());
            currentChar = this.sourceFile.getNextChar();
        }

        if(currentChar.equals(SourceFile.eol)){currentChar = sourceFile.getNextChar();}

        return new Token(Token.Kind.COMMENT, commentBody,
                this.sourceFile.getCurrentLineNumber());
    }

    /**
     * Creates and returns a multi-line comment token
     * @return a token of Kind.COMMENT or Kind.ERROR if it was unclosed
     */
    private Token getBlockCommentToken(Character prevChar) {

        String commentBody = "/*";

        // move prevChar and currentChar past the "/*"
        for (int i = 0; i < 2; i++) {
            prevChar = currentChar;
            currentChar = this.sourceFile.getNextChar();
        }

        boolean commentTerminated = false;

        while (!commentTerminated) {

            commentBody = commentBody.concat(prevChar.toString());
            if (currentChar.equals(SourceFile.eof)) {

                this.errorHandler.register(Error.Kind.LEX_ERROR,
                        this.sourceFile.getFilename(),
                        this.sourceFile.getCurrentLineNumber(),
                        "UNTERMINATED BLOCK COMMENT");

                return new Token(Token.Kind.ERROR,
                        commentBody.concat(currentChar.toString()),
                        this.sourceFile.getCurrentLineNumber());
            }

            else if (prevChar.equals('*') && currentChar.equals('/'))
                commentTerminated = true;


            prevChar = currentChar;
            currentChar = this.sourceFile.getNextChar();
        }
        currentChar = sourceFile.getNextChar();
        return new Token(Token.Kind.COMMENT, commentBody.concat(prevChar.toString()),
                this.sourceFile.getCurrentLineNumber());
    }

    /**
     * Tester Method for the Scanner class.
     * Prints all the tokens of a file
     *
     * @param args command line file arguments
     */
    public static void main (String[] args){
        if(true){
            for(int i = 0; i < 1; i ++){
                Scanner scanner;
                ErrorHandler errorHandler = new ErrorHandler();
                try {

                    scanner = new Scanner("test3.txt", errorHandler);

                }
                catch(CompilationException e){
                    System.out.println(e);
                    continue;
                }

                Token nextToken;
                nextToken = scanner.scan();
                while (nextToken.kind != Token.Kind.EOF) {
                    if(nextToken.kind != Token.Kind.NOTWORD) {
                        System.out.println(nextToken);
                    }
                    nextToken = scanner.scan();
                }

                if(errorHandler.getErrorList().size() > 0){
                    System.out.println("Scanning of " + "test1.txt" + " was not successful. "+
                            errorHandler.getErrorList().size() +" errors were found.\n\n");
                }
                else{

                    System.out.println("Scanning of " + "test1.txt" + " was successful. " +
                            "No errors were found.\n\n");
                }

            }
        }

    }

    public String getFilename(){
        return sourceFile.getFilename();
    }

}