/* Bantam Java Compiler and Language Toolset.

   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and 
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.

   The Bantam Java toolset is distributed under the following 
   conditions:

     You may make copies of the toolset for your own use and 
     modify those copies.

     All copies of the toolset must retain the author names and 
     copyright notice.

     You may not sell the toolset or distribute it in 
     conjunction with a commerical product or service without 
     the expressed written consent of the authors.

   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS 
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE 
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
   PARTICULAR PURPOSE. 
*/


/*
 * File: MipsCodeGenerator.java
 * Names: Kevin Ahn, Kyle Slager, and Danqing Zhao
 * Class: CS461
 * Project 17
 * Date: April 17, 2019
 */


package proj18AhnSlager.bantam.codegenmusic;

import proj18AhnSlager.bantam.ast.*;
import proj18AhnSlager.bantam.parser.Parser;
import proj18AhnSlager.bantam.util.CompilationException;
import proj18AhnSlager.bantam.util.Error;
import proj18AhnSlager.bantam.util.ErrorHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * The <tt>MipsCodeGenerator</tt> class generates mips assembly code
 * targeted for the SPIM or Mars emulators.
 * <p/>
 * This class is incomplete and will need to be implemented by the student.
 */
public class MusicCodeGenerator {
    /**
     * AST node for visitor methods implemented
     */
    private Piece piece;

    /**
     * Print stream for output assembly file
     */
    private PrintStream out;

    /**
     * for recording any errors that occur.
     */
    private ErrorHandler errorHandler;

    /**
     * outfile
     */
    private String outFile;

    /**
     * MipsCodeGenerator constructor
     *
     * @param errorHandler ErrorHandler to record all errors that occur
     */
    public MusicCodeGenerator(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Generate java file for jfugue
     * @param piece   root of piece hierarchy tree
     * @param outFile filename of the assembly output file
     */
    public void generate(Piece piece, String outFile) {
        this.piece = piece;
        this.outFile = outFile;
        // set up the PrintStream for writing the assembly file.
        try {
            this.out = new PrintStream(new FileOutputStream(outFile));
        } catch (IOException e) {
            errorHandler.register(Error.Kind.CODEGEN_ERROR, "IOException when writing " +
                    "to file: " + outFile);
            throw new CompilationException("Couldn't write to output file.");
        }

        genProlog(piece.getName());

        MusicCodeGenVisitor musicCodeGenVisitor = new MusicCodeGenVisitor(out);
        musicCodeGenVisitor.visit(piece);

        genEpilog();
    }

    private void genProlog(String name){
        out.println("import org.jfugue.player.*;");
        out.println("import org.jfugue.theory.*;");
        out.println("import org.jfugue.pattern.*;");
        out.println("import org.jfugue.rhythm.Rhythm;");
        out.println();
        out.println("public class " + name + "{");
        out.println();
        out.println("\tpublic static void main(String[] args) {");
        out.println();
        out.println("\t\tPattern pattern0 = new Pattern(\"V0 I[VOICE_OOHS] T[Adagio] \");");
        out.println("\t\tPattern pattern1 = new Pattern(\"V1 I[Flute] T[Adagio] \");");
    }

    private void genEpilog(){
        out.println("\t\tPlayer player = new Player();");
        out.println("\t\tpattern0.add(pattern1);");
        out.println("\t\tplayer.play(pattern0);");
        out.println("\t}");
        out.println("}");
    }

    /*
    Main Function used for testing purposes, executes parsing and analyzing before passing the results of those
    (Program and ClassTreeNode) to the generate method of the class. Catches problems with parsing, analyzing,
    or compiling the mips code
     */
    public static void main(String[] args) {
        ErrorHandler errorHandler = new ErrorHandler();
        Parser parser = new Parser(errorHandler);
        try{
            errorHandler.clear();
            Piece piece = parser.parse("test1.txt");
            MusicCodeGenerator musicCodeGenerator = new MusicCodeGenerator(errorHandler);
            musicCodeGenerator.generate(piece, piece.getName().replace(" ", "") + ".java");
        }
        catch (CompilationException ex){
            System.out.print(ex);
        }

//        for (String fileNames : args) {
//            try {
//                errorHandler.clear();
//                Program program = parser.parse(fileNames);
//                ClassTreeNode classTreeNode = analyzer.analyze(program);
//                MipsCodeGenerator mipsCodeGenerator = new MipsCodeGenerator(errorHandler, false, false);
//                mipsCodeGenerator.generate(classTreeNode, fileNames.replace(".btm", ".asm"), program);
//                System.out.println("MIPS code generation was successful.");
//            } catch (CompilationException ex) {
//                System.out.println("MIPS code generation was not successful:");
//                List<Error> errorList = errorHandler.getErrorList();
//                for (Error error : errorList) {
//                    System.out.println(" ,  " + error.toString());
//                }
//            }
//        }
    }
}