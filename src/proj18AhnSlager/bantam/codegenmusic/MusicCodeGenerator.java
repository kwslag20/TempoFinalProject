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
import proj18AhnSlager.bantam.parser.NodeGeneratorAndChecker;
import proj18AhnSlager.bantam.util.CompilationException;
import proj18AhnSlager.bantam.util.Error;
import proj18AhnSlager.bantam.util.ErrorHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
     * Print stream for the output of the music player
     */
    private PrintStream musicPlayerOut;

    /**
     * outfile
     */
    private String outFile;

    /**
     * Name of author
     */
    private String authorName;

    /**
     * MipsCodeGenerator constructor
     *
     * @param errorHandler ErrorHandler to record all errors that occur
     */
    public MusicCodeGenerator(ErrorHandler errorHandler, String authorName) {
        this.errorHandler = errorHandler;
        this.authorName = authorName;
    }

    /**
     * Generate java file for jfugue
     * @param piece   root of piece hierarchy tree
     * @param outFile filename of the assembly output file
     */
    public void generate(Piece piece, String outFile, String type) {
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

        if(type.equals("GenAndPlay")) {
            genProlog(piece.getName());
            MusicCodeGenVisitor musicCodeGenVisitor = new MusicCodeGenVisitor(out);
            musicCodeGenVisitor.visit(piece);
            genEpilog();
        }
        else if(type.equals("OpenPlayer")){
            genPlayerProlog(piece.getName());
            MusicCodeGenVisitor musicCodeGenVisitor = new MusicCodeGenVisitor(out);
            musicCodeGenVisitor.visit(piece);
            genPlayerEpilog();
        }
    }

    public void generatePlayerFile(String outFile){
        try{
            this.musicPlayerOut = new PrintStream(new FileOutputStream(outFile));
        }
        catch (IOException e1) {
            System.out.println(e1);
        }
        genMusicPlayerFile();
    }

    private void genMusicPlayerFile(){
        String type = this.outFile.replace(" ", "");
        type = type.substring(type.lastIndexOf('/')+1, type.lastIndexOf('.'));
        musicPlayerOut.println("package MusicPlayer;");
        musicPlayerOut.println("import MusicPlayer.outputs." +type+";");
        musicPlayerOut.println("public class IntermediaryPlayer {\npublic void play(){\n" +
                type + " name = new " + type + "();\n" +
                "name.notplay();" +
                "    }\n" +
                "    public void pause(){\n" +
                type + " name = new " + type + "();\n" +
                "name.notpause();" +
                "    }\n" +
                "   public void resume(){\n" +
                type + " name = new " + type + "();\n" +
                "name.notresume();" +
                "}\n"+
                "}");
    }

    /**
     * Gets the current date and time at which the file is generated
     * @return
     */
    private String getDate(){
        String curDate;
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        curDate = formatter.format(date);
        return curDate;
    }

    /**
     *
     * @param name
     */
    private void genProlog(String name){
        out.println("/** File Generated from Tempo file: " + name + ".mus");
        out.println("Author: " + this.authorName);
        out.println("Date Generated: " + getDate() + " **/");
        out.println("package proj18AhnSlager.outputs;");
        out.println("import org.jfugue.player.*;");
        out.println("import org.jfugue.theory.*;");
        out.println("import org.jfugue.pattern.*;");
        out.println();
        out.println("public class " + name + "{");
        out.println();
        out.println("\tpublic static void main(String[] args) {");
        out.println();
        out.println("\t\tPattern pattern0 = new Pattern(\"V0 I[VOICE_OOHS] T[Andantino] \");");
        out.println("\t\tPattern pattern1 = new Pattern(\"V1 I[Flute] T[Andantino] \");");
    }

    private void genPlayerProlog(String name){
        out.println("/** File Generated from Tempo file: " + name + ".mus\n" +
                "Date Generated: 2019-05-15 at 21:39:24 EDT **/\n" +
                "package MusicPlayer.outputs;\n" +
                "import org.jfugue.player.*;\n" +
                "import org.jfugue.theory.*;\n" +
                "import org.jfugue.pattern.*;\n" +
                "\n" +
                "import javax.sound.midi.InvalidMidiDataException;\n" +
                "import javax.sound.midi.MidiUnavailableException;\n" +
                "import javax.sound.midi.Sequence;\n" +
                "\n" +
                "public class " + name+ "{\n" +
                "\n" +
                "\tpublic Player player = new Player();\n" +
                "\tpublic ManagedPlayer managedPlayer = new ManagedPlayer();\n" +
                "\tpublic Pattern pattern0 = createPattern();\n" +
                "\n" +
                "\tpublic Pattern createPattern(){\n" +
                "\t\tpattern0 = new Pattern(\"V0 I[VOICE_OOHS] T[Andantino] \");\n" +
                "\t\tPattern pattern1 = new Pattern(\"V1 I[Flute] T[Andantino] \");");
    }

    private void genPlayerEpilog(){
        out.println("return pattern0;\n}\n" +
                "\n" +
                "\tpublic void notplay(){\n" +
                "\t\tthis.createPattern();\n" +
                "\t\tSequence seq = player.getSequence(pattern0);\n" +
                "\t\ttry{\n" +
                "\t\t\tmanagedPlayer.start(seq);\n" +
                "\t\t}\n" +
                "\t\tcatch (InvalidMidiDataException | MidiUnavailableException e){\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\n" +
                "\tpublic void notpause(){\n" +
                "\t\tmanagedPlayer.pause();\n" +
                "\t}" + "\n" +
                "\tpublic void notresume(){\n" +
                "\t\tmanagedPlayer.resume();\n" +
                "\t}\n"+
                "}");
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
    (Piece) to the generate method of the class. Catches problems with parsing, analyzing,
    or compiling the mips code
     */
    public static void main(String[] args) {
        ErrorHandler errorHandler = new ErrorHandler();
        NodeGeneratorAndChecker nodeGeneratorAndChecker = new NodeGeneratorAndChecker(errorHandler);
        try{
            Piece piece = nodeGeneratorAndChecker.parse("TempoExample.mus");
            MusicCodeGenerator musicCodeGenerator = new MusicCodeGenerator(errorHandler, nodeGeneratorAndChecker.getAuthorName());
            musicCodeGenerator.generate(piece, piece.getName().replace(" ", "") + ".java", "GenAndPlay");
            //musicCodeGenerator.generatePlayerFile("src/MusicPlayer/IntermediaryPlayer.java");
        }
        catch (CompilationException ex) {
            System.out.print(ex);
        }
    }
}