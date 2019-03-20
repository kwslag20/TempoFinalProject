/*
 * File: MethodVisitor.java
 * Names: Kevin Ahn, Kyle Slager
 * Class: CS461
 * Project 13
 */

package proj15AhnSlagerZhou.bantam.semant;

import proj15AhnSlagerZhou.bantam.ast.*;
import proj15AhnSlagerZhou.bantam.visitor.Visitor;

import java.util.ArrayList;

/**
 * @author Kevin Ahn and Kyle Slager
 * MethodVisitor class that extends the Visitor class to get the list of methods from
 * an AST and add it to an arrayList
 */
public class MethodVisitor extends Visitor {

    public ArrayList methodList;
    public String option;
    public String input;


    /**
     * method that creates a new method list and calls the parseRoots accepts method
     * @param parseRoot
     * @return an ArrayList of method names
     */
    public ArrayList<String> getMethods(Program parseRoot, String option, String input){
        this.input = input;
        this.option = option;
        methodList = new ArrayList();
        parseRoot.accept(this);
        return methodList;
    }

    /**
     * overrides the Class_ visit method to add in the names of each method in the AST
     * @param node the method node
     * @return
     */
    public Object visit(Method node){
        if(node.getName().equals(this.input)){
            node.setName(this.option);
        }
        methodList.add(node.getName());
        node.getFormalList().accept(this);
        node.getStmtList().accept(this);
        return null;
    }
}
