package proj18AhnSlagerZhao.bantam.visitor;

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

//package proj18AhnSlagerZhao.bantam.visitor;

import proj18AhnSlagerZhao.bantam.ast.*;

import java.util.Iterator;

/**
 * Abstract visitor class for traversing the AST
 */
public abstract class Visitor {
    /**
     * Visit an Piece node (should never be called)
     *
     * @param node the AST node
     * @return result of the visit
     */

    public Object visit(Piece node){
        node.getPieceList().accept(this);
        return null;
    }

    /**
     * Visit a PieceList node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(PieceList node){
        for(ASTNode musicSection : node){
            musicSection.accept(this);
        }
        return null;
    }

    /**
     * Visit a Verse node
     *
     * @param node the Verse node
     * @return result of the visit
     */
    public Object visit(Verse node){
        node.getMemberList().accept(this);
        return null;
    }

    /**
     * Visit a Chorus node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Chorus node){
        node.getMemberList().accept(this);
        return null;
    }

    /**
     * Visit a Layout node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Layout node){
        return null;
    }

    /**
     * Visit a MemberList node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(MemberList node){
        for (ASTNode hand : node)
            hand.accept(this);
        return null;
    }

    /**
     * Visit a RightHand node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(RightHand node){
        node.getNotesList().accept(this);
        return null;
    }

    /**
     * Visit a LeftHand node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(LeftHand node){
        node.getNotesList().accept(this);
        return null;
    }

    /**
     * Visit a NotesList node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(NotesList node){
        for (ASTNode note: node)
            note.accept(this);
        return null;
    }

    /**
     * Visit a LayoutList node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(LayoutList node){
        for (ASTNode layoutObj: node){
            layoutObj.accept(this);
        }
        return null;
    }

    /**
     * Visit a Writer node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Writer node){
        return null;
    }

    /**
     * Visit a Instrument node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Instrument node){
        return null;
    }

    /**
     * Visit a Tempo node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Tempo node){
        return null;
    }

    /**
     * Visit a OrderObj node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(OrderObj node){
        return null;
    }

    /**
     * Visit a Note node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Note node){
        return null;
    }


    /**
     * Visit a AST node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(ASTNode node) {
        throw new RuntimeException("This visitor method should not be called (node is abstract)");
    }

    /**
     * Visit a list node (should never be called)
     *
     * @param node the list node
     * @return result of the visit
     */
    public Object visit(ListNode node) {
        throw new RuntimeException("This visitor method should not be called (node is abstract)");
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
        node.getMemberList().accept(this);
        return null;
    }

    /**
     * Visit a member node (should never be calle)
     *
     * @param node the member node
     * @return result of the visit
     */
    public Object visit(Member node) {
        throw new RuntimeException("This visitor method should not be called (node is abstract)");
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
        }
        return null;
    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Method node) {
        node.getFormalList().accept(this);
        node.getStmtList().accept(this);
        return null;
    }

    /**
     * Visit a list node of formals
     *
     * @param node the formal list node
     * @return result of the visit
     */
    public Object visit(FormalList node) {
        for (Iterator it = node.iterator(); it.hasNext(); )
            ((Formal) it.next()).accept(this);
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
     * Visit a statement node (should never be calle)
     *
     * @param node the statement node
     * @return result of the visit
     */
    public Object visit(Stmt node) {
        throw new RuntimeException("This visitor method should not be called (node is abstract)");
    }

    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return result of the visit
     */
    public Object visit(DeclStmt node) {
        node.getInit().accept(this);
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
        node.getPredExpr().accept(this);
        node.getThenStmt().accept(this);
        if (node.getElseStmt() != null) {
            node.getElseStmt().accept(this);
        }
        return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return result of the visit
     */
    public Object visit(WhileStmt node) {
        node.getPredExpr().accept(this);
        node.getBodyStmt().accept(this);
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {
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
        return null;
    }
}