/*
 * File: MUSICScanner.java
 * Names: Kevin Ahn, Lucas DeGraw, Jackie Hang, Kyle Slager
 * Class: CS 361
 * Project 9
 * Date: November 20, 2018
 * ---------------------------
 * Edited From: Dale Skrien
 */

package proj18AhnSlagerZhao.bantam.lexer;

import proj18AhnSlagerZhao.bantam.util.CompilationException;
import proj18AhnSlagerZhao.bantam.util.ErrorHandler;

import java.io.*;
import java.util.List;
import java.util.Set;

import proj18AhnSlagerZhao.bantam.util.Error;


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
    private Boolean isNote, isChord;
    private String curString;
    private Token tempToken;


    private final Set<Character> charsEndingIdentifierOrKeyword =
            Set.of('"', '/', '+', '-', '>', '<', '=', '&', '{',
                    '}', '[', ']', '(', ')', ';', ':', '!', ' ',
                    '.', ',', '\r', '\n', '*', '%');


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
        isChord = false;
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

        switch(tempChar) {
            case(SourceFile.eof):
                return new Token(Token.Kind.EOF,
                        currentChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('"'): return this.getStringConstToken();

            case('g'):
            case('f'):
            case('e'):
            case('d'):
            case('c'):
            case('b'):
            case('a'):
                checkCurString(tempChar);
                currentChar = this.sourceFile.getNextChar();
                if(isNote && currentChar != 'h' && currentChar != '('){
                    return new Token(Token.Kind.PITCH, tempChar.toString(), this.sourceFile.getCurrentLineNumber());
                }
                else {
                    return new Token(Token.Kind.NOTWORD, "doesnt matter", this.sourceFile.getCurrentLineNumber());
                }
            case('w'):
            case('h'):
            case('q'):
            case('i'):
            case('s'):
            case('t'):
            case('x'):
            case('o'):
                checkCurString(tempChar);
                currentChar = this.sourceFile.getNextChar();
                if(currentChar.equals('n')){
                    currentChar = sourceFile.getNextChar();
                    isNote = true;
                    curString = "";
                    return new Token(Token.Kind.NOTE, tempChar.toString()+'n', this.sourceFile.getCurrentLineNumber());
                }
                else{
                    return new Token(Token.Kind.NOTWORD, "doesnt matter", this.sourceFile.getCurrentLineNumber());
                }

            case('-'): return this.getMinusToken();

            case('{'):
                tempToken = checkCurString(tempChar);
                currentChar = sourceFile.getNextChar();
                curString = "";
                isNote = false;
                isChord = false;
                if(tempToken.kind != Token.Kind.NOTWORD) {
                    return tempToken;
                }
                else{
                    return new Token(Token.Kind.ERROR, "Invalid Usage of " + curString, this.sourceFile.getCurrentLineNumber());
                }

            case('}'):
                currentChar = sourceFile.getNextChar();
                curString = "";
                return new Token(Token.Kind.RCURLY,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('('):
                tempToken = checkCurString(tempChar);
                currentChar = sourceFile.getNextChar();
                curString = "";
                if(tempToken.kind != Token.Kind.NOTWORD) {
                    return tempToken;
                }
                else{
                    return new Token(Token.Kind.ERROR, "Invalid Usage of " + curString, this.sourceFile.getCurrentLineNumber());
                }

            case(')'):
                currentChar = sourceFile.getNextChar();
                curString = "";
                return new Token(Token.Kind.RPAREN,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(':'):
                currentChar = sourceFile.getNextChar();
                return new Token(Token.Kind.NOTWORD, tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(';'):
                currentChar = sourceFile.getNextChar();
                curString = "";
                isNote = false;
                isChord = false;
                return new Token(Token.Kind.SEMICOLON,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case('.'):
                currentChar = sourceFile.getNextChar();
                curString = "";
                isNote = false;
                isChord = false;
                return new Token(Token.Kind.DOT,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            case(','):
                currentChar = sourceFile.getNextChar();
                curString = "";
                isNote = false;
                isChord = false;
                return new Token(Token.Kind.COMMA,
                    tempChar.toString(), this.sourceFile.getCurrentLineNumber());

            default:

                if (Character.isDigit(currentChar)) return getIntConstToken();
                else if (Character.isLetter(currentChar)){
                    tempToken = checkCurString(tempChar);
                    currentChar = this.sourceFile.getNextChar();
                    return tempToken;
                }
                else {
                    currentChar = sourceFile.getNextChar();
                    this.errorHandler.register(Error.Kind.LEX_ERROR,
                            this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                            "TOKEN ERROR");
                    return new Token(Token.Kind.ERROR, tempChar.toString(),
                            this.sourceFile.getCurrentLineNumber());
                }
         }
    }

    private Token checkCurString(Character tempChar) {
        if (curString != null) {
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
                    isChord = true;
                    return new Token(Token.Kind.CHORD, chordInfo, this.sourceFile.getCurrentLineNumber());

                case ("piece"):
                    String pieceName = "";
                    while (!currentChar.equals('{')) {
                        pieceName += currentChar;
                        currentChar = this.sourceFile.getNextChar();
                    }
                    curString = "";
                    return new Token(Token.Kind.PIECE, pieceName, this.sourceFile.getCurrentLineNumber());

                case ("baseseq"):
                    String baseInfo = "";
                    while (!currentChar.equals(')')) {
                        baseInfo += currentChar;
                        currentChar = this.sourceFile.getNextChar();
                    }
                    baseInfo += currentChar;
                    currentChar = this.sourceFile.getNextChar();
                    curString = "";
                    return new Token(Token.Kind.BASELINE, baseInfo, this.sourceFile.getCurrentLineNumber());

                case ("layout"):
                    currentChar = this.sourceFile.getNextChar();
                    return new Token(Token.Kind.LAYOUT, "layout", this.sourceFile.getCurrentLineNumber());

                case ("chorus"):
                    String chorusName = "";
                    while (!currentChar.equals('{')){
                        chorusName += currentChar;
                        currentChar = this.sourceFile.getNextChar();
                    }
                    curString = "";
                    return new Token(Token.Kind.CHORUS, chorusName, this.sourceFile.getCurrentLineNumber());

                case ("verse"):
                    String verseName = "";
                    while (!currentChar.equals('{')){
                        verseName += currentChar;
                        currentChar = this.sourceFile.getNextChar();
                    }
                    curString = "";
                    return new Token(Token.Kind.VERSE, verseName, this.sourceFile.getCurrentLineNumber());

                case ("righthand"):
                    currentChar = this.sourceFile.getNextChar();
                    curString = "";
                    return new Token(Token.Kind.RIGHTHAND, "righthand", this.sourceFile.getCurrentLineNumber());

                case ("lefthand"):
                    currentChar = this.sourceFile.getNextChar();
                    curString = "";
                    return new Token(Token.Kind.LEFTHAND, "lefthand", this.sourceFile.getCurrentLineNumber());

                case ("writer"):
                    String authorName = "";
                    while (!currentChar.equals(';')){
                        authorName += currentChar;
                        currentChar = this.sourceFile.getNextChar();
                    }
                    curString = "";
                    return new Token(Token.Kind.WRITER, authorName, this.sourceFile.getCurrentLineNumber());
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

    /**
     * Returns a string constant token ensuring that
     * no strings are over 5000 characters
     *
     * @return string constant token
     */
    private Token getStringConstToken() {

        String spelling = "";
        spelling = spelling.concat(currentChar.toString());
        currentChar = this.sourceFile.getNextChar();

        //while the quote is unmatched continue getting chars
        while(!currentChar.equals('"')){

            //if you've reached an eof or a new line in a string, throws error
            if(currentChar.equals(SourceFile.eof) || currentChar.equals('\n')){
                this.errorHandler.register(Error.Kind.LEX_ERROR,
                        this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                        "UNCLOSED QUOTE");
                return createErrorToken(spelling);
            }
            else if (currentChar.equals('\\')){
                spelling = spelling.concat(currentChar.toString());
                currentChar = this.sourceFile.getNextChar();
            }
            //otherwise add on to the string
            spelling = spelling.concat(currentChar.toString());
            currentChar = this.sourceFile.getNextChar();
        }

        //add on end quote
        spelling = spelling.concat(currentChar.toString());
        currentChar = sourceFile.getNextChar();

        //makes sure the string is less than 5000 chars
        if(spelling.length()<5000) {
            return new Token(Token.Kind.STRCONST, spelling, this.sourceFile.getCurrentLineNumber());
        }
        else{
            this.errorHandler.register(Error.Kind.LEX_ERROR,
                    this.sourceFile.getFilename(), this.sourceFile.getCurrentLineNumber(),
                    "STRING EXCEEDS MAX CHAR LENGTH 5000");
            return createErrorToken(spelling);
        }
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

                    scanner = new Scanner("test1.txt", errorHandler);

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