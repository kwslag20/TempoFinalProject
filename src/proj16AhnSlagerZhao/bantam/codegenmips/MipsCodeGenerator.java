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

package proj16AhnSlagerZhao.bantam.codegenmips;

import proj16AhnSlagerZhao.bantam.ast.Field;
import proj16AhnSlagerZhao.bantam.ast.Member;
import proj16AhnSlagerZhao.bantam.ast.MemberList;
import proj16AhnSlagerZhao.bantam.ast.Program;
import proj16AhnSlagerZhao.bantam.semant.StringConstantsVisitor;
import proj16AhnSlagerZhao.bantam.util.ClassTreeNode;
import proj16AhnSlagerZhao.bantam.util.CompilationException;
import proj16AhnSlagerZhao.bantam.util.Error;
import proj16AhnSlagerZhao.bantam.util.ErrorHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * The <tt>MipsCodeGenerator</tt> class generates mips assembly code
 * targeted for the SPIM or Mars emulators.
 * <p/>
 * This class is incomplete and will need to be implemented by the student.
 */
public class MipsCodeGenerator
{
    /**
     * Root of the class hierarchy tree
     */
    private ClassTreeNode root;

    /**
     * Print stream for output assembly file
     */
    private PrintStream out;

    /**
     * Assembly support object (using Mips assembly support)
     */
    private MipsSupport assemblySupport;

    /**
     * Boolean indicating whether garbage collection is enabled
     */
    private boolean gc = false;

    /**
     * Boolean indicating whether optimization is enabled
     */
    private boolean opt = false;

    /**
     * Boolean indicating whether debugging is enabled
     */
    private boolean debug = false;

    /**
     * for recording any errors that occur.
     */
    private ErrorHandler errorHandler;

    /**
     * MipsCodeGenerator constructor
     *
     * @param errorHandler ErrorHandler to record all errors that occur
     * @param gc      boolean indicating whether garbage collection is enabled
     * @param opt     boolean indicating whether optimization is enabled
     */
    public MipsCodeGenerator(ErrorHandler errorHandler, boolean gc, boolean opt) {
        this.gc = gc;
        this.opt = opt;
        this.errorHandler = errorHandler;
    }

    /**
     * Generate assembly file
     * <p/>
     * In particular, you will need to do the following:
     * 1 - start the data section
     * 2 - generate data for the garbage collector
     * 3 - generate string constants
     * 4 - generate class name table
     * 5 - generate object templates
     * 6 - generate dispatch tables
     * 7 - start the text section
     * 8 - generate initialization subroutines
     * 9 - generate user-defined methods
     * See the lab manual for the details of each of these steps.
     *
     * @param root    root of the class hierarchy tree
     * @param outFile filename of the assembly output file
     */
    public void generate(ClassTreeNode root, String outFile) {
        this.root = root;

        StringConstantsVisitor stringConstantsVisitor = new StringConstantsVisitor();
        Map<String, String> stringMap = stringConstantsVisitor.getStringConstants(this.root);

        // set up the PrintStream for writing the assembly file.
        try {
            this.out = new PrintStream(new FileOutputStream(outFile));
            this.assemblySupport = new MipsSupport(out);
        } catch (IOException e) {
            // if don't have permission to write to file then throw an exception
            errorHandler.register(Error.Kind.CODEGEN_ERROR, "IOException when writing " +
                    "to file: " + outFile);
            throw new CompilationException("Couldn't write to output file.");
        }

        // comment out
        // throw new RuntimeException("MIPS code generator unimplemented");

        // add code here...
        // STEP 1 START THE DATA SECTION
        this.assemblySupport.genDataStart();

        // STEP 2 GENERATE DATA FOR GC
        this.assemblySupport.genLabel("gc_flag");
        if (this.gc){
            this.assemblySupport.genWord("1");
        }
        else{
            this.assemblySupport.genWord("0");
        }

        this.genStringConstants(stringMap);
        // STEP 3 GENERATE STRING CONSTANTS

    }

    /**
     * a method to add a string constant object to a map to keep track of them
     * @param stringMap map containing the string objects
     */
    private void genStringConstants(Map<String,String> stringMap){
        Map<String, String> builtIns = new HashMap<>();
        builtIns.put("Object", "class_name_0");
        builtIns.put("String", "class_name_1");
        builtIns.put("Sys", "class_name_2");
        builtIns.put("Main", "class_name_3");
        builtIns.put("TextIO", "class_name_4");

        Set<Map.Entry<String,String>> stringEntrySet = stringMap.entrySet();
        Iterator<Map.Entry<String,String>> stringIterator = stringEntrySet.iterator();
        while(stringIterator.hasNext()){
            String label = stringIterator.next().getValue();
            String str = stringIterator.next().getKey();
            generateStringConstantSupport(label,str);
        }
        Set<Map.Entry<String,String>> builtInEntrySet = builtIns.entrySet();
        Iterator<Map.Entry<String,String>> builtInIterator = builtInEntrySet.iterator();
        Set<String> filenames = new HashSet<>();
        int fileNum = 0;
        while(builtInIterator.hasNext()){
            String label = builtInIterator.next().getValue();
            String str = builtInIterator.next().getKey();
            generateStringConstantSupport(label,str);
            String filename = root.getClassMap().get(str).getName();
            if (!filenames.contains(filename)) {
                filenames.add(filename);
                generateStringConstantSupport("file_name_"+fileNum, filename);
                fileNum++;
            }
        }
    }

    private void generateStringConstantSupport(String label, String str){
        assemblySupport.genLabel(label);
        assemblySupport.genWord("1");
        assemblySupport.genWord(Integer.toString(16 + (int)Math.ceil((str.length() + 1)/4)*4));
        assemblySupport.genWord("String_dispatch_table");
        assemblySupport.genWord(Integer.toString(str.length()));
        assemblySupport.genAscii(str);
        assemblySupport.genAlign();
    }


    private void genClassTableNames(){
        assemblySupport.genLabel("class_name_table");
        int numClasses = this.root.getClassMap().values().size();
        for (int i = 0 ; i < numClasses ; i++){
            assemblySupport.genLabel("class_name_" + i);
        }
    }
    public void genObjectTemplate(){
        assemblySupport.genLabel("Object_template");
        assemblySupport.genWord("0");
        assemblySupport.genWord("12");
        assemblySupport.genWord("Object_dispatch_table");

        int numClasses = this.root.getClassMap().values().size();
        String className;
        ClassTreeNode classTreeNode;
        MemberList memberList;
        List<Field> fieldList;
        for (int i = 0 ; i < numClasses ; i++){
            className = this.root.getClassMap().keys().nextElement();
            classTreeNode = this.root.getClassMap().get(className);
            assemblySupport.genLabel(className + "_template");
            memberList = classTreeNode.getASTNode().getMemberList();
            fieldList = new ArrayList<Field>();
            for(Iterator iter = memberList.iterator(); iter.hasNext();){
                Member member = (Member)iter.next();
                if(member instanceof Field) {
                    fieldList.add((Field)member);
                }
            }

            assemblySupport.genWord((i+1)+"");
            assemblySupport.genWord(12+fieldList.size()*4+"");
            assemblySupport.genWord(className+"_dispatch_table");
            for(Iterator iter = fieldList.iterator();iter.hasNext();){
                Field field = (Field)iter.next();
                assemblySupport.genWord("0");
            }
        }
    }
    public static void main(String[] args) {
        // ... add testing code here ...
    }
}