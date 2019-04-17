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

import java.util.*;

import proj16AhnSlagerZhao.bantam.ast.*;
import proj16AhnSlagerZhao.bantam.util.ClassTreeNode;
import proj16AhnSlagerZhao.bantam.codegenmips.Location;
import proj16AhnSlagerZhao.bantam.util.SymbolTable;
import proj16AhnSlagerZhao.bantam.visitor.Visitor;

import java.io.PrintStream;

public class MipsCodeGenVisitor extends Visitor {

    private MipsSupport genSupport;
    private static final String[] registers = new String[]{
            "$a0", "$a1","$a2","$a3","$t0","$t1","$t2","$t3",
            "$t4","$t5","$t6","$t7","$v0","$v1"
    };

    /**
     * constructor for the class
     */
    public MipsCodeGenVisitor(){

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
            genSupport.genAdd("$sp", "$sp", -4);
            genSupport.genStoreWord(reg, 0,"$sp");
        }
        genSupport.genAdd("$fp", "$fp",-4*numLocalVars);
    }

    /**
     * Method caller after the called method returns:
     * - the return value, if any, is now in $v0
     * - pop & throw away the obj ref (recall that the called method is responsible for
     *   removing any parameters from the stack)
     * - restore any $v, $a and $t registers that it pushed on the stack
     */
    private void generateEpilog(int numLocalVars){
        genSupport.genAdd("$sp", "$sp", 4*numLocalVars);
        for (int i = -1; i < registers.length ; i--){
            genSupport.genLoadWord(registers[i], 0,"$sp");
            genSupport.genAdd("$sp", "$sp", 4);
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
        return null;
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return result of the visit
     */
    public Object visit(Class_ node) {
        return null;
    }

    /**
     * Visit a list node of members
     *
     * @param node the member list node
     * @return result of the visit
     */
    public Object visit(MemberList node) {
        return null;
    }

    /**
     * Visit a member node (should never be calle)
     *
     * @param node the member node
     * @return result of the visit
     */
    public Object visit(Member node) {
        return null;
    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return result of the visit
     */
    public Object visit(Field node) {
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
        return null;
    }

    /**
     * Visit a list node of formals
     *
     * @param node the formal list node
     * @return result of the visit
     */
    public Object visit(FormalList node) {
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
        return null;
    }

    /**
     * Visit a statement node (should never be calle)
     *
     * @param node the statement node
     * @return result of the visit
     */
    public Object visit(Stmt node) {
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
        return null;
    }

    /**
     * Visit an expression statement node
     *
     * @param node the expression statement node
     * @return result of the visit
     */
    public Object visit(ExprStmt node) {
        return null;
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return result of the visit
     */
    public Object visit(IfStmt node) {
        return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return result of the visit
     */
    public Object visit(WhileStmt node) {
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {
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
        return null;
    }

    /**
     * Visit a block statement node
     *
     * @param node the block statement node
     * @return result of the visit
     */
    public Object visit(BlockStmt node) {
        return null;
    }

    /**
     * Visit a return statement node
     *
     * @param node the return statement node
     * @return result of the visit
     */
    public Object visit(ReturnStmt node) {
        return null;
    }

    /**
     * Visit a list node of expressions
     *
     * @param node the expression list node
     * @return result of the visit
     */
    public Object visit(ExprList node) {
        return null;
    }

    /**
     * Visit an expression node (should never be called)
     *
     * @param node the expression node
     * @return result of the visit
     */
    public Object visit(Expr node) {
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
     * Visit a new array expression node
     *
     * @param node the new array expression node
     * @return result of the visit
     */
    public Object visit(NewArrayExpr node) {
        return null;
    }

    /**
     * Visit an instanceof expression node
     *
     * @param node the instanceof expression node
     * @return result of the visit
     */
    public Object visit(InstanceofExpr node) {
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
        return null;
    }

    /**
     * Visit an array assignment expression node
     *
     * @param node the array assignment expression node
     * @return result of the visit
     */
    public Object visit(ArrayAssignExpr node) {
        return null;
    }

    /**
     * Visit a binary expression node (should never be called)
     *
     * @param node the binary expression node
     * @return result of the visit
     */
    public Object visit(BinaryExpr node) {
        return null;
    }

    /**
     * Visit a binary comparison expression node (should never be called)
     *
     * @param node the binary comparison expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompExpr node) {
        return null;
    }

    /**
     * Visit a binary comparison equals expression node
     *
     * @param node the binary comparison equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompEqExpr node) {
        return null;
    }

    /**
     * Visit a binary comparison not equals expression node
     *
     * @param node the binary comparison not equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompNeExpr node) {
        return null;
    }

    /**
     * Visit a binary comparison less than expression node
     *
     * @param node the binary comparison less than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLtExpr node) {
        return null;
    }

    /**
     * Visit a binary comparison less than or equal to expression node
     *
     * @param node the binary comparison less than or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLeqExpr node) {
        return null;
    }

    /**
     * Visit a binary comparison greater than expression node
     *
     * @param node the binary comparison greater than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGtExpr node) {
        return null;
    }

    /**
     * Visit a binary comparison greater than or equal to expression node
     *
     * @param node the binary comparison greater to or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGeqExpr node) {
        return null;
    }

    /**
     * Visit a binary arithmetic expression node (should never be called)
     *
     * @param node the binary arithmetic expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithExpr node) {
        return null;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic plus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithPlusExpr node) {
        return null;
    }

    /**
     * Visit a binary arithmetic minus expression node
     *
     * @param node the binary arithmetic minus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithMinusExpr node) {
        return null;
    }

    /**
     * Visit a binary arithmetic times expression node
     *
     * @param node the binary arithmetic times expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithTimesExpr node) {
        return null;
    }

    /**
     * Visit a binary arithmetic divide expression node
     *
     * @param node the binary arithmetic divide expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithDivideExpr node) {
        return null;
    }

    /**
     * Visit a binary arithmetic modulus expression node
     *
     * @param node the binary arithmetic modulus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithModulusExpr node) {
        return null;
    }

    /**
     * Visit a binary logical expression node (should never be called)
     *
     * @param node the binary logical expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicExpr node) {
        return null;
    }

    /**
     * Visit a binary logical AND expression node
     *
     * @param node the binary logical AND expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicAndExpr node) {
        return null;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicOrExpr node) {
        return null;
    }

    /**
     * Visit a unary expression node
     *
     * @param node the unary expression node
     * @return result of the visit
     */
    public Object visit(UnaryExpr node) {
        return null;
    }

    /**
     * Visit a unary negation expression node
     *
     * @param node the unary negation expression node
     * @return result of the visit
     */
    public Object visit(UnaryNegExpr node) {
        return null;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return result of the visit
     */
    public Object visit(UnaryNotExpr node) {
        return null;
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return result of the visit
     */
    public Object visit(UnaryIncrExpr node) {
        return null;
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return result of the visit
     */
    public Object visit(UnaryDecrExpr node) {
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
        return null;
    }

    /**
     * Visit an array expression node
     *
     * @param node the array expression node
     * @return result of the visit
     */
    public Object visit(ArrayExpr node) {
        return null;
    }

    /**
     * Visit a constant expression node (should never be called)
     *
     * @param node the constant expression node
     * @return result of the visit
     */
    public Object visit(ConstExpr node) {
        return null;
    }

    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return result of the visit
     */
    public Object visit(ConstIntExpr node) {
        return null;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return result of the visit
     */
    public Object visit(ConstBooleanExpr node) {
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
