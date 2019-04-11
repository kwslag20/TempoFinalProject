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

import proj16AhnSlagerZhao.bantam.ast.*;
import proj16AhnSlagerZhao.bantam.parser.Parser;
import proj16AhnSlagerZhao.bantam.semant.SemanticAnalyzer;
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
public class MipsCodeGenerator {
    /**
     * Root of the class hierarchy tree
     */
    private ClassTreeNode root;

    /**
     * AST node for visitor methods implemented
     */
    private Program ast;

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
     * hash map
     */
    private HashMap<String, ArrayList<String>> altMethodsMap;

    /**
     * outfile
     */
    private String outFile;

    /**
     * MipsCodeGenerator constructor
     *
     * @param errorHandler ErrorHandler to record all errors that occur
     * @param gc           boolean indicating whether garbage collection is enabled
     * @param opt          boolean indicating whether optimization is enabled
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
    public void generate(ClassTreeNode root, String outFile, Program ast) {
        this.root = root;
        this.ast = ast;
        this.outFile = outFile;

        StringConstantsVisitor stringConstantsVisitor = new StringConstantsVisitor();
        Map<String, String> stringMap = stringConstantsVisitor.getStringConstants(this.ast);

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
        if (this.gc) {
            this.assemblySupport.genWord("1");
        } else {
            this.assemblySupport.genWord("0");
        }

        this.genStringConstants(stringMap);

        this.genClassTableNames();

        this.genObjectTemplate();
        // STEP 3 GENERATE STRING CONSTANTS
        this.genClassTableNames();

        this.genObjectTemplate();

        this.altMethodsMap = new HashMap<>();

        MethodsVisitor methodsVisitor = new MethodsVisitor();
        //HashMap<String, ArrayList<String>> methodsMap = methodsVisitor.getMethodsMap(this.ast);
        this.generateDispatchTables();

        this.assemblySupport.genTextStart();
        this.genStubs();

    }

    /**
     * a method to add a string constant object to a map to keep track of them
     *
     * @param stringMap map containing the string objects
     */
    private void genStringConstants(Map<String, String> stringMap) {
        Map<String, String> builtIns = new HashMap<>();
        builtIns.put("Object", "class_name_0");
        builtIns.put("String", "class_name_1");
        builtIns.put("Sys", "class_name_2");
        builtIns.put("Main", "class_name_3");
        builtIns.put("TextIO", "class_name_4");

        Set<Map.Entry<String, String>> stringEntrySet = stringMap.entrySet();
        Iterator<Map.Entry<String, String>> stringIterator = stringEntrySet.iterator();
        while (stringIterator.hasNext()) {
            String label = stringIterator.next().getValue();
            String str = stringIterator.next().getKey();
            generateStringConstantSupport(label, str);
        }
        Set<Map.Entry<String, String>> builtInEntrySet = builtIns.entrySet();
        Iterator<Map.Entry<String, String>> builtInIterator = builtInEntrySet.iterator();
        Set<String> filenames = new HashSet<>();
        int fileNum = 0;
        while (builtInIterator.hasNext()) {
            Map.Entry<String, String> current = builtInIterator.next();
            String label = current.getValue();
            String str = current.getKey();
            generateStringConstantSupport(label, str);
        }
        generateStringConstantSupport("label0", this.outFile);
        this.out.println("\n");

    }

    private void generateStringConstantSupport(String label, String str) {
        assemblySupport.genLabel(label);
        assemblySupport.genWord("1");
        assemblySupport.genWord(Integer.toString(16 + (int) Math.ceil((str.length() + 1) / 4) * 4));
        assemblySupport.genWord("String_dispatch_table");
        assemblySupport.genWord(Integer.toString(str.length()));
        assemblySupport.genAscii(str);
    }


    private void genClassTableNames() {
        assemblySupport.genLabel("class_name_table");
        int numClasses = this.root.getClassMap().values().size();
        for (int i = 0; i < numClasses; i++) {
            assemblySupport.genWord("class_name_" + i);
        }
    }

    private void genObjectTemplate() {

        int numClasses = this.root.getClassMap().values().size();
        String className;
        ClassTreeNode classTreeNode;
        MemberList memberList;
        List<Field> fieldList;
        ArrayList<String> classList = new ArrayList<>();
        classList.addAll(this.root.getClassMap().keySet());
        for (int i = 0; i < numClasses; i++) {
            className = classList.get(i);
            System.out.println(className);
            classTreeNode = this.root.getClassMap().get(className);
            assemblySupport.genLabel(className + "_template");
            memberList = classTreeNode.getASTNode().getMemberList();
            fieldList = new ArrayList<Field>();
            for (Iterator iter = memberList.iterator(); iter.hasNext(); ) {
                Member member = (Member) iter.next();
                if (member instanceof Field) {
                    fieldList.add((Field) member);
                }
            }

            assemblySupport.genWord((i + 1) + "");
            assemblySupport.genWord(12 + fieldList.size() * 4 + "");
            assemblySupport.genWord(className + "_dispatch_table");
            for (Iterator iter = fieldList.iterator(); iter.hasNext(); ) {
                Field field = (Field) iter.next();
                assemblySupport.genWord("0");
            }
        }
    }

    /**
     * Generator for method stubs in the text section
     */
    private void genStubs() {
        ArrayList<String> classList = new ArrayList<>();
        classList.addAll(this.root.getClassMap().keySet());
        this.out.print("\n");
        for (String className : classList) {
            this.assemblySupport.genLabel(className + "_init");
        }
        this.out.print("\n");

        MethodsVisitor methodsVisitor = new MethodsVisitor();
        Map<Class_, ArrayList<String>> methodsMap = methodsVisitor.getMethodsMap(this.ast);

        for (Map.Entry<Class_, ArrayList<String>> methodList : methodsMap.entrySet()) {
            for (String methodName : methodList.getValue()) {
                assemblySupport.genLabel(methodList.getKey().getName() + "." + methodName);
            }
        }

        this.out.println("\njr $ra");

    }


    private ArrayList<String> generateDispatchTable(String className, LinkedHashMap<String, String> methodClassMap, ArrayList<String> methodNameList){

        MemberList members;
        String methodName;
        ClassTreeNode curNode;
        Map classMap = this.root.getClassMap();

        String curName;

            curNode =  (ClassTreeNode)classMap.get(className);

            this.assemblySupport.genLabel("\n"+className+"_dispatch_table");
            while (curNode != null) {

                curName = curNode.getName();

                // get each one's methods & fields (Members)
                members = curNode.getASTNode().getMemberList();

                int numMems = members.getSize();

                // loop backwards through the members to add them in the order declared in .btm file
                for (int i = numMems-1; i >= 0; i--) {

                    // get current member
                    Member member = (Member)members.get(i);

                    // if it's a method
                    if (member instanceof Method) {
                        methodName = ((Method) member).getName();   // save method name

                        if (methodClassMap.containsKey(methodName)) {   // if method already declared
                            methodClassMap.remove(methodName);  // remove existing declaration
                            methodClassMap.put(methodName, className);  // add declaration
                        }
                        else {  // method is not already declared
                            methodClassMap.put(methodName, curName);   // add new declaration
                        }
                    }
                }
                curNode = curNode.getParent();    // reset node
            }

            // save list of keys
            methodNameList = new ArrayList<>(methodClassMap.keySet());

            // loop backwards through list of method keys to write in order declared in file
            for (int i = methodNameList.size()-1; i >= 0; i--) {

                methodName = methodNameList.get(i);
                curName = methodClassMap.get(methodName);
                this.assemblySupport.genWord(curName+"."+methodName); // pop & write value of stack
            }
            methodClassMap.clear();
        return methodNameList;

    }

    private void generateDispatchTables(){
        ArrayList<String> classList = new ArrayList<>();
        classList.addAll(this.root.getClassMap().keySet());

        ArrayList<String> methodNameList = new ArrayList<>();
        LinkedHashMap<String, String> methodClassMap = new LinkedHashMap();


        MethodsVisitor methodsVisitor = new MethodsVisitor();
        Map<Class_, ArrayList<String>> methodsMap = methodsVisitor.getMethodsMap(this.ast);
        this.assemblySupport.genComment("Dispatch Tables:");

        for(Map.Entry<Class_, ArrayList<String>> entry : methodsMap.entrySet()){
            this.assemblySupport.genGlobal(entry.getKey() + "_dispatch_table");
        }

        for(String className : classList){
            methodNameList = generateDispatchTable(className,methodClassMap, methodNameList);

        }

        this.out.print("\n");

    }




    public static void main(String[] args) {
        ErrorHandler errorHandler = new ErrorHandler();
        Parser parser = new Parser(errorHandler);
        SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);

        for (String inFile : args) {
            System.out.println("\n========== MIPS Code Generation results for " + inFile + " =============");
            try {
                errorHandler.clear();
                Program program = parser.parse(inFile);
                ClassTreeNode classTreeNode = analyzer.analyze(program);
                System.out.println(" Semantic Analysis was successful.");
                MipsCodeGenerator mipsCodeGenerator = new MipsCodeGenerator(errorHandler, false, false);
                mipsCodeGenerator.generate(classTreeNode, inFile.replace(".btm", ".asm"), program);
                System.out.println(" Generation of "+ inFile.replace(".btm", ".asm") + " was successful.");
            } catch (CompilationException ex) {
                System.out.println(" There were errors in generation:");
                List<Error> errors = errorHandler.getErrorList();
                for (Error error : errors) {
                    System.out.println("\t" + error.toString());
                }
            }
        }
    }
}