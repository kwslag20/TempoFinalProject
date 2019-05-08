/*
 * Parser.java												2.0 1999/08/11
 *
 * Copyright (C) 1999 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 *
 *
 * Modified by Haoyu Song for a REVISED LL(1) version of Bantam Java
 * The parser is completely recursive descending
 * 		1)Give precedence to operators
 * 		2)Simplify the AST structures
 * 		3)Support more operations and special symbols
 * 		4)Add more keywords to the beginning of expressions and statements and thus make
 * 		the grammar mostly LL(1)
 *
 * Modified by Dale Skrien to clean up the code
 *
 * In the grammar below, the variables are enclosed in angle brackets and
 * "::=" is used instead of "-->" to separate a variable from its rules.
 * The special character "|" is used to separate the rules for each variable.
 * EMPTY indicates a rule with an empty right hand side.
 * All other symbols in the rules are terminals.
 *
 * --------------------------------------
 *
 * Modified by Jackie Hang, Kyle Slager
 * Project 11
 * Date: February 13, 2019
 */


package proj18AhnSlagerZhao.bantam.parser;

import proj18AhnSlagerZhao.bantam.ast.*;
import proj18AhnSlagerZhao.bantam.lexer.Scanner;
import proj18AhnSlagerZhao.bantam.lexer.Token;
import proj18AhnSlagerZhao.bantam.util.CompilationException;
import proj18AhnSlagerZhao.bantam.util.Error;
import proj18AhnSlagerZhao.bantam.util.ErrorHandler;

import static proj18AhnSlagerZhao.bantam.lexer.Token.Kind.*;

/*
 * Authors: Haoyu Song and Dale Skrien
 * Date: Spring and Summer, 2018
 *
 * In the grammar below, the variables are enclosed in angle brackets.
 * The notation "::=" is used instead of "-->" to separate a variable from its rules.
 * The special character "|" is used to separate the rules for each variable.
 * All other symbols in the rules are terminals.
 * EMPTY indicates a rule with an empty right hand side.
 * All other terminal symbols that are in all caps correspond to keywords.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * This class constructs an AST from a legal Bantam Java program.  If the
 * program is illegal, then one or more error messages are displayed.
 */
public class Parser
{
    // instance variables
    private Scanner scanner;
    private Token currentToken; // the lookahead token
    private ErrorHandler errorHandler;
    private String filename;

    // constructor
    public Parser(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * parse the given file and return the root node of the AST
     * @param filename The name of the Bantam Java file to be parsed
     * @return The Program node forming the root of the AST generated by the parser
     */
    public Piece parse(String filename) {
        this.scanner=new Scanner(filename, this.errorHandler);
        this.filename=filename;
        return this.parsePiece();
    }

    /*
     * <Piece> ::= <Verse> | <Chorus> | <Layout>
     */
    private Piece parsePiece(){
        updateCurrentToken();
        int position = currentToken.position;
        String name = currentToken.spelling;
        PieceList pieceList = new PieceList(position);
        updateCurrentToken();
        while (currentToken.kind != EOF) {
            if(currentToken.kind == VERSE){
                Verse verse = parseVerse();
                pieceList.addElement(verse);
            }
            if(currentToken.kind == CHORUS) {
                Chorus chorus = parseChorus();
                pieceList.addElement(chorus);
            }
            if(currentToken.kind == LAYOUT){
                Layout layout = parseLayout();
                pieceList.addElement(layout);
            }
            updateCurrentToken();
        }


        return new Piece(position, name, pieceList);
    }


    /*
     * <Verse> ::= VERSE <Identifier> { <MemberList> }
     * <MemberList> ::= EMPTY | <Member> <MemberList>
     */
    private Verse parseVerse() {
        int position = currentToken.position;
        MemberList memberList= new MemberList(position);
        String name = currentToken.spelling;
        this.checkToken(VERSE,"When parsing verse, verse expected." );
        memberList.addElement(parseRightHand());
        memberList.addElement(parseLeftHand());
        return new Verse(position, name, memberList);
    }

    /*
     * <RightHand> ::= RIGHTHAND { <MemberList> }
     * <MemberList> ::= EMPTY | <Member> <MemberList>
     */
    private RightHand parseRightHand() {
        int position = currentToken.position;
        NotesList notesList= new NotesList(position);
        this.checkToken(RIGHTHAND,"When parsing right hand, right hand expected." );
        while (currentToken.kind!= RCURLY){
            if (currentToken.kind == EOF){
                this.registerError("When parsing right hand, left hand expected",
                        "Missing Left Hand");
            }
            notesList.addElement(parseMember());
        }
        updateCurrentToken();
        return new RightHand(position, notesList);
    }

    /*
     * <LeftHand> ::= LEFTHAND { <MemberList> }
     * <MemberList> ::= EMPTY | <Member> <MemberList>
     */
    private LeftHand parseLeftHand() {
        int position = currentToken.position;
        NotesList notesList= new NotesList(position);

        this.checkToken(LEFTHAND,"When parsing left hand, left hand expected." );
        while (currentToken.kind!= RCURLY){
            if (currentToken.kind == EOF){
                this.registerError("When parsing left hand, layout expected",
                        "Missing Layout");
            }
            notesList.addElement(parseMember());
        }
        updateCurrentToken();
        return new LeftHand(position, notesList);
    }

    /*
     * <Chorus> ::= CHORUS { <MemberList> }
     * <MemberList> ::= EMPTY | <Member> <MemberList>
     */
    private Chorus parseChorus() {
        int position = currentToken.position;
        MemberList memberList= new MemberList(position);
        this.checkToken(CHORUS,"When parsing chorus, chorus expected." );
        memberList.addElement(parseRightHand());
        memberList.addElement(parseLeftHand());
        return new Chorus(position, memberList);
    }

    /*
     * <Layout> ::= LAYOUT { <MemberList> }
     * <MemberList> ::= EMPTY | <Member> <MemberList>
     */
    private Layout parseLayout(){
        int position = currentToken.position;
        LayoutList layoutList = new LayoutList(position);
        updateCurrentToken();
        while(currentToken.kind != RCURLY){
            layoutList.addElement(parseLayoutMember());
        }
        return new Layout(position, layoutList);
    }

    private Member parseLayoutMember(){
        int position = currentToken.position;
        String spelling = currentToken.spelling;
        if(currentToken.kind == INSTRUMENT){
            updateCurrentToken();
            return new Instrument(position, spelling);
        }
        else if(currentToken.kind == TEMPO){
            updateCurrentToken();
            return new Tempo(position, spelling);
        }
        else if(currentToken.kind == ORDEROBJ){
            updateCurrentToken();
            return new OrderObj(position, spelling);
        }
        else if(currentToken.kind == WRITER){
            updateCurrentToken();
            return new Writer(position, spelling);
        }
        else {
            return null;
        }
    }


    /* Fields and Methods
     * <Member> ::= <Note> | <Rest> | <Baseseq>
     * <Note> ::= <Length> <Pitch> <Octave>
     * <Rest> ::= <Length>
     * <Baseseq> ::=  <TODO>
     */
    private Member parseMember() {
        int position= currentToken.position;
        String length = currentToken.spelling;
        if(currentToken.kind == NOTE){
            String pitch = parsePitch();
            int octave = parseOctave();
            updateCurrentToken();
            return new Note(position, length, pitch, octave);
        }
        if(currentToken.kind == REST){
            updateCurrentToken();
            return new Rest(position, length);
        }
        if(currentToken.kind == CHORD){

        }
        else {
            this.registerError("When parsing field, \"(\", \"=\", or \";\" expected.",
                    "Unexpected Token");
        }
        return null;
    }

    private String parsePitch(){
        updateCurrentToken();
        String pitch = currentToken.spelling;
        if(currentToken.kind==PITCH || currentToken.kind==CHORD){
            return pitch;
        }
        else{
            this.registerError("When parsing note, [note] [pitch] [octave];",
                    "Expected Pitch Token");
        }
        return null;
    }

    private int parseOctave(){
        updateCurrentToken();
        String octave = currentToken.spelling;
        if(currentToken.kind == OCTAVE){
            return Integer.parseInt(octave);
        }
        else{
            this.registerError("When parsing note, [note] [pitch] [octave];",
                    "Expected Octave Token");
        }
        return 4;
    }

    /**
     * Updates the current Token
     * Ignores comment tokens
     */
    private void updateCurrentToken(){
        this.currentToken = scanner.scan();
        while(this.currentToken.kind == COMMENT || this.currentToken.kind == NOTWORD ||
                this.currentToken.kind == SEMICOLON || this.currentToken.kind == RPAREN
        || this.currentToken.kind == COLON || this.currentToken.kind == COMMA){
            this.currentToken = scanner.scan();
        }
    }

    /**
     * Throws errors
     * @param errorMessage message passed to errorHandler
     * @param compilationMessage message passed to CompilationException
     */
    private void registerError(String errorMessage,String compilationMessage){
        this.errorHandler.register(Error.Kind.PARSE_ERROR,this.filename,
                this.currentToken.position,errorMessage);
        throw new CompilationException(compilationMessage);
    }

    /**
     * checks if the current token is of the correct type and registers an error if not. Moves past the token
     * @param kind kind of the token to match
     * @param errorMessage error message to be sent to the errorHandler
     */
    private void checkToken(Token.Kind kind,String errorMessage){
        if(this.currentToken.kind != kind){
            this.registerError(errorMessage, "Unexpected Token");
        }
        updateCurrentToken();
    }

    /**
     * main method used for testing
     * @param argv
     */
    public static void main(String[] argv){
//        if(argv.length == 0){
//            System.out.println("Please Provide Test Files");
//            return;
//        }
        String[] filenames = new String[]{"test1.txt"};
        for(String filename: filenames) {
            ErrorHandler errorHandler = new ErrorHandler();
            Parser parser = new Parser(errorHandler);

            try {
                parser.parse(filename);
                System.out.println("Parsing Successful.");
            }catch(CompilationException e){
                if(errorHandler.errorsFound()){
                    System.out.println(filename + ": Parsing Failed");
                    List<Error> errorList= errorHandler.getErrorList();
                    for(Error error:errorList ){
                        System.out.println(error.toString() + "\n");
                    }
                }else{
                    System.out.println("Invalid filename: "+filename);
                }
            }
        }
    }

}

