/*
 * File: ClassVisitor.java
 * Names: Kevin Ahn, Kyle Slager
 * Class: CS461
 * Project 13
 */

package proj17AhnSlagerZhao.bantam.semant;

import proj17AhnSlagerZhao.bantam.ast.Class_;
import proj17AhnSlagerZhao.bantam.ast.Program;
import proj17AhnSlagerZhao.bantam.visitor.Visitor;

import java.util.ArrayList;

/**
 * @author Kevin Ahn and Kyle Slager
 * ClassVisitor class that extends the Visitor class to get the list of classes from
 * an AST and add it to an arrayList
 */
public class ClassVisitor extends Visitor {

    public ArrayList classList;
    public String option;
    public String input;

    /**
     * method that creates a new class list and calls the parseRoots accepts method
     * @param parseRoot
     * @return an ArrayList of class names
     */
    public ArrayList<String> getClasses(Program parseRoot, String option, String input){
        this.input = input;
        this.option = option;
        classList = new ArrayList();
        parseRoot.accept(this);
        return classList;
    }

    /**
     * overrides the Class_ visit method to add in the names of each class in the AST
     * @param node the class node
     * @return
     */
    @Override
    public Object visit(Class_ node){
        if(node.getName().equals(this.input)){
            node.setName(this.option);
        }
        classList.add(node.getName()); // adds it to the class list
        node.getMemberList().accept(this);
        if(!this.option.equals("null")){
            node.setName(this.option);
        }
        return null;
    }
}
