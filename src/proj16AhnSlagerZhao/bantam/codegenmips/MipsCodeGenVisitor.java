/*
 * File: MipsCodeGenVisitor.java
 * Names: Kevin Ahn, Kyle Slager, and Danqing Zhou
 * Class: CS461
 * Project 17
 * Date: April 17, 2019
 */

package proj16AhnSlagerZhao.bantam.codegenmips;

import org.reactfx.util.Lists;
import proj16AhnSlagerZhao.bantam.ast.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import proj16AhnSlagerZhao.bantam.ast.*;
import proj16AhnSlagerZhao.bantam.util.ClassTreeNode;
import proj16AhnSlagerZhao.bantam.codegenmips.Location;
import proj16AhnSlagerZhao.bantam.util.CompilationException;
import proj16AhnSlagerZhao.bantam.util.Error;
import proj16AhnSlagerZhao.bantam.util.SymbolTable;
import proj16AhnSlagerZhao.bantam.visitor.Visitor;

import java.io.PrintStream;

public class MipsCodeGenVisitor extends Visitor {

    private MipsSupport assemblySupport;
    private SymbolTable symbolTable;
    private static final String[] registers = new String[]{
            "$a0", "$a1","$a2","$a3","$t0","$t1","$t2","$t3",
            "$t4","$t5","$t6","$t7","$v0","$v1"
    };

    /**
     * constructor for the class
     */
    public MipsCodeGenVisitor(MipsSupport assemblySupport){
        this.symbolTable = new SymbolTable();
        this.assemblySupport = assemblySupport;
    }

    /**
     * Method caller just before calling the method:
     * - save on the stack any $a, $v and $t registers with info to be saved (including $a0)
     * - compute the obj ref and temporarily push it on the stack
     * - compute & push the params (start of the callee's stack frame)
     * - load the obj ref into $a0
     * - compute the location of the method to call (using $a0's dispatch table) & put it in a
     *   register, say $t0
     * - call method using jalr $t0
     */
    private void generateProlog(int numLocalVars){
        for (String reg : registers){
            assemblySupport.genAdd("$sp", "$sp", -4);
            assemblySupport.genStoreWord(reg, 0,"$sp");
        }
        assemblySupport.genAdd("$fp", "$fp",-4*numLocalVars);
        assemblySupport.genRetn();
    }

    /**
     * Method caller after the called method returns:
     * - the return value, if any, is now in $v0
     * - pop & throw away the obj ref (recall that the called method is responsible for
     *   removing any parameters from the stack)
     * - restore any $v, $a and $t registers that it pushed on the stack
     */
    private void generateEpilog(int numLocalVars){
        assemblySupport.genAdd("$sp", "$sp", 4*numLocalVars);
        for (int i = -1; i < registers.length ; i--){
            assemblySupport.genLoadWord(registers[i], 0,"$sp");
            assemblySupport.genAdd("$sp", "$sp", 4);
        }
    }

    /**
     * Visit a program node
     *
     * @param node the program node
     * @return result of the visit
     */
    public Object visit(Program node) {
        node.getClassList().accept(this);
        return null;
    }

    /**
     * Visit a list node of classes
     *
     * @param node the class list node
     * @return result of the visit
     */
    public Object visit(ClassList node) {
        for (ASTNode aNode : node)
            aNode.accept(this);
        return null;
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return result of the visit
     */
    public Object visit(Class_ node) {
        symbolTable.enterScope();
        node.getMemberList().accept(this);
        symbolTable.exitScope();
        return null;
    }

    /**
     * Visit a list node of members
     *
     * @param node the member list node
     * @return result of the visit
     */
    public Object visit(MemberList node) {
        for (ASTNode child : node)
            child.accept(this);
        return null;
    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return result of the visit
     */
    public Object visit(Field node) {
        if (node.getInit() != null) {
            node.getInit().accept(this);
            this.assemblySupport.genComment("storing field " + node.getName() + " at " + symbolTable.getCurrScopeSize()*4);
            this.assemblySupport.genStoreWord("$v0", symbolTable.getCurrScopeSize()*4, "$a0");
        }
        Location location = new Location("$a0", symbolTable.getCurrScopeSize() * 4);
        symbolTable.add(node.getName(), location);
        return null;
    }

    /**
     * TODO Danqing
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Method node) {
        symbolTable.enterScope();
        node.getFormalList().accept(this);
        node.getStmtList().accept(this);
        symbolTable.exitScope();
        return null;
    }

    /**
     * Visit a list node of formals
     *
     * @param node the formal list node
     * @return result of the visit
     */
    public Object visit(FormalList node) {
        for (Iterator it = node.iterator(); it.hasNext(); ) {
            Formal param = (Formal) it.next();
            Location location = new Location("$fp", symbolTable.getCurrScopeSize() * 4);
            symbolTable.add(param.getName(), location);
        }
        return null;
    }

    /**
     * Visit a formal node
     *
     * @param node the formal node
     * @return result of the visit
     */
    public Object visit(Formal node) {
        return null;
    }

    /**
     * Visit a list node of statements
     *
     * @param node the statement list node
     * @return result of the visit
     */
    public Object visit(StmtList node) {
        for (Iterator it = node.iterator(); it.hasNext(); )
            ((Stmt) it.next()).accept(this);
        return null;
    }

    /**
     * TODO Kyle
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return result of the visit
     */
    public Object visit(DeclStmt node) {
        node.getInit().accept(this);
        Location location = new Location("$fp", symbolTable.getCurrScopeSize()*4);
        symbolTable.add(node.getName(), location);
        return null;
    }

    /**
     * Visit an expression statement node
     *
     * @param node the expression statement node
     * @return result of the visit
     */
    public Object visit(ExprStmt node) {
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return result of the visit
     */
    public Object visit(IfStmt node) {
        symbolTable.enterScope();
        node.getPredExpr().accept(this);
        node.getThenStmt().accept(this);
        if (node.getElseStmt() != null) {
            node.getElseStmt().accept(this);
        }
        symbolTable.exitScope();
        return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return result of the visit
     */
    public Object visit(WhileStmt node) {
        symbolTable.enterScope();
        node.getPredExpr().accept(this);
        node.getBodyStmt().accept(this);
        symbolTable.exitScope();
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {
        symbolTable.enterScope();
        if (node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }
        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
        }
        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }
        node.getBodyStmt().accept(this);
        symbolTable.exitScope();
        return null;
    }

    /**
     * TODO Kevin
     * Visit a break statement node
     *
     * @param node the break statement node
     * @return result of the visit
     */
    public Object visit(BreakStmt node) {
        assemblySupport.genComment("Generating a break");
        assemblySupport.genRetn();
        return null;
    }

    /**
     * Visit a block statement node
     *
     * @param node the block statement node
     * @return result of the visit
     */
    public Object visit(BlockStmt node) {
        node.getStmtList().accept(this);
        return null;
    }

    /**
     * Visit a return statement node
     *
     * @param node the return statement node
     * @return result of the visit
     */
    public Object visit(ReturnStmt node) {
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
        }
        return null;
    }

    /**
     * Visit a list node of expressions
     *
     * @param node the expression list node
     * @return result of the visit
     */
    public Object visit(ExprList node) {
        for (Iterator it = node.iterator(); it.hasNext(); )
            ((Expr) it.next()).accept(this);
        return null;
    }

    /**
     * TODO Danqing
     * Visit a dispatch expression node
     *
     * @param node the dispatch expression node
     * @return result of the visit
     */
    public Object visit(DispatchExpr node) {
        if(node.getRefExpr() != null)
            node.getRefExpr().accept(this);
        node.getActualList().accept(this);
        return null;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return result of the visit
     */
    public Object visit(NewExpr node) {
        return null;
    }

    /**
     * Visit an instanceof expression node
     *
     * @param node the instanceof expression node
     * @return result of the visit
     */
    public Object visit(InstanceofExpr node) {
        node.getExpr().accept(this);
        return null;
    }

    /**
     * TODO Kyle
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return result of the visit
     */
    public Object visit(CastExpr node) {
        node.getExpr().accept(this);
        return null;
    }

    /**
     * TODO Kevin
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return result of the visit
     */
    public Object visit(AssignExpr node) {
        this.assemblySupport.genStoreWord("$a0", -4, "$sp");
        node.getExpr().accept(this);
        this.assemblySupport.genComment("GENERATING AN ASSIGN EXPRESSION");
        Location local = (Location) symbolTable.lookup(node.getName());
        this.assemblySupport.genStoreWord("$v0",local.getOffset(),local.getBaseReg());
        this.assemblySupport.genLoadWord("$a0", -4, "$sp");
        return null;
    }

    /**
     * Visit a binary comparison equals expression node
     *
     * @param node the binary comparison equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompEqExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary comparison not equals expression node
     *
     * @param node the binary comparison not equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompNeExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;    }

    /**
     * Visit a binary comparison less than expression node
     *
     * @param node the binary comparison less than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLtExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;    }

    /**
     * Visit a binary comparison less than or equal to expression node
     *
     * @param node the binary comparison less than or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLeqExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary comparison greater than expression node
     *
     * @param node the binary comparison greater than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGtExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary comparison greater than or equal to expression node
     *
     * @param node the binary comparison greater to or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGeqExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic plus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithPlusExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary arithmetic minus expression node
     *
     * @param node the binary arithmetic minus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithMinusExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary arithmetic times expression node
     *
     * @param node the binary arithmetic times expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithTimesExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary arithmetic divide expression node
     *
     * @param node the binary arithmetic divide expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithDivideExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;    }

    /**
     * Visit a binary arithmetic modulus expression node
     *
     * @param node the binary arithmetic modulus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithModulusExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary logical AND expression node
     *
     * @param node the binary logical AND expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicAndExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicOrExpr node) {
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a unary negation expression node
     *
     * @param node the unary negation expression node
     * @return result of the visit
     */
    public Object visit(UnaryNegExpr node) {
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return result of the visit
     */
    public Object visit(UnaryNotExpr node) {
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return result of the visit
     */
    public Object visit(UnaryIncrExpr node) {
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return result of the visit
     */
    public Object visit(UnaryDecrExpr node) {
        node.getExpr().accept(this);
        return null;
    }

    /**
     * TODO Kyle
     * Visit a variable expression node
     *
     * @param node the variable expression node
     * @return result of the visit
     */
    public Object visit(VarExpr node) {
        if(node.getRef() != null){
            node.getRef().accept(this);
        }
        Location location = (Location)symbolTable.lookup(node.getName());
        if(location != null){
            assemblySupport.genLoadWord("$v0", location.getOffset(), location.getBaseReg());
        }
        return null;
    }

    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return result of the visit
     */
    public Object visit(ConstIntExpr node) {
        assemblySupport.genComment("Generating a Constant Int Expression");
        assemblySupport.genLoadImm("$v0",node.getIntConstant());
        return null;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return result of the visit
     */
    public Object visit(ConstBooleanExpr node) {
        assemblySupport.genComment("Generating Constant Boolean Expression");
        if (node.getConstant().equals("true")){
            assemblySupport.genComment("Generating TRUE Constant Boolean Expression");
            assemblySupport.genLoadImm("$v0", 1);
        }
        else {
            assemblySupport.genComment("Generating FALSE Constant Boolean Expression");
            assemblySupport.genLoadImm("$v0", 0);
        }
        return null;
    }

    /**
     * Visit a string constant expression node
     *
     * @param node the string constant expression node
     * @return result of the visit
     */
    public Object visit(ConstStringExpr node) {
        return null;
    }
}