/*
 * File: DependencyVisitor.java
 * Names: Kevin Ahn, Kyle Slager
 * Class: CS461
 * Project 13
 */
package proj17AhnSlagerZhao.bantam.semant;

import proj17AhnSlagerZhao.bantam.ast.*;
import proj17AhnSlagerZhao.bantam.visitor.Visitor;

import java.util.ArrayList;

/**
 * @author Kevin Ahn, Kyle Slager
 *
 * a DependencyVisitor class to create a list that contains
 * the list of dependencies
 */
public class DependencyVisitor extends Visitor {

    public ArrayList dependencyList;
    public String name;

    /**
     * initializes the arrayList of dependencies
     * @param parseRoot ast
     * @param name the name
     * @return the dependencyList arrayList
     */
    public ArrayList initialize(Program parseRoot, String name){
        this.name = name;
        dependencyList = new ArrayList();
        parseRoot.accept(this);
        return dependencyList;
    }

    /**
     * overrides the visit method for the DeclStmt node
     * @param node the declaration statement node
     * @return
     */
    public Object visit(DeclStmt node){
        Expr expr = node.getInit();
            if(expr instanceof VarExpr) {
                if(((VarExpr) expr).getName().equals(this.name)) {
                    dependencyList.add(((VarExpr) expr).getName());
                }
            }
            expr.accept(this);
        return null;
    }

    /**
     * checks if the left expression is an instance of the right expression
     * if it is not, it then also checks if it is an instance of VarExpr, otherwise it
     * begins to check the second expression
     * @param expr
     * @return
     */
    public Boolean checkExpr(Expr expr){
        Expr expr1 = ((BinaryExpr) expr).getLeftExpr();
        Expr expr2 = ((BinaryExpr) expr).getRightExpr();
        if(expr1 instanceof VarExpr){
            return ((VarExpr) expr1).getName().equals(this.name);
        }
        else{
            checkExpr(expr1);
        }
        if(expr1 instanceof VarExpr){
            return ((VarExpr) expr2).getName().equals(this.name);
        }
        else{
            checkExpr(expr2);
        }
        return false;

    }

    /**
     * overrides the visit method for the AssignExpr node
     * @param node the assignment expression node
     * @return
     */
    public Object visit(AssignExpr node){
        Expr expr = node.getExpr();
        if(expr instanceof BinaryExpr){
            if(checkExpr(expr)){
                dependencyList.add(node.getName());
            }
        }
        if(expr instanceof VarExpr){
            if(((VarExpr) expr).getName().equals(this.name)){
                dependencyList.add(node.getName());
            }
        }
        node.getExpr().accept(this);
        return null;
    }

    /**
     * overrides the visit method for the Field node
     * @param node the field node
     * @return
     */
     public Object visit(Field node){
        Expr expr = node.getInit();
        if(expr instanceof  VarExpr) {
            if(((VarExpr) expr).getName().equals(this.name)) {
                dependencyList.add(node.getName());
            }
        }
         if (expr != null) {
             expr.accept(this);
         }
        return null;
     }
}
