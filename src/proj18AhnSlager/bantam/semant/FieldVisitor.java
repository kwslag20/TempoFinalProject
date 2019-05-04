/*
 * File: FieldVisitor.java
 * Names: Kevin Ahn, Kyle Slager
 * Class: CS461
 * Project 13
 */

package proj18AhnSlager.bantam.semant;

import proj18AhnSlager.bantam.ast.*;
import proj18AhnSlager.bantam.visitor.Visitor;

import java.util.ArrayList;

/**
 * @author Kevin Ahn and Kyle Slager
 * FieldVisitor class that extends the Visitor class to get the list of fields from
 * an AST and add it to an arrayList
 */
public class FieldVisitor extends Visitor {

    public ArrayList fieldList;
    public String option;
    public String input;

    /**
     * method that creates a new field list and calls the parseRoots accepts method
     * @param parseRoot
     * @return an ArrayList of field names
     */
    public ArrayList<String> getFields(Program parseRoot, String option, String input){
        this.input = input;
        this.option = option;
        fieldList = new ArrayList();
        parseRoot.accept(this);
        return fieldList;
    }

    /**
     * overrides the Field visit method to add in the names of each field in the AST
     * @param node the field node
     * @return
     */
    public Object visit(Field node){
        if(node.getName().equals(this.input)){
            node.setName(this.option);
        }
        fieldList.add(node.getName());
        if (node.getInit() != null) {
            node.getInit().accept(this);
        }
        return null;
    }
}
