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

package proj18AhnSlagerZhao.bantam.ast;

import proj18AhnSlagerZhao.bantam.visitor.Visitor;


/**
 * The <tt>Piece</tt> class represents an entire piece, which
 * consists of an author and list of verses, chorus, bridge, layout (<tt>pieceList</tt>).
 *
 * @see ASTNode
 * @see PieceList
 */
public class Piece extends ASTNode {

    /**
     * List of class declarations that comprise the piece
     */
    protected PieceList pieceList;

    /**
     * Program constructor
     *
     * @param lineNum   source line number corresponding to this AST node
     * @param pieceList list of class declarations
     */
    public Piece(int lineNum, PieceList pieceList) {
        super(lineNum);
        this.pieceList = pieceList;
    }

    /**
     * Get list of verses, chorus, bridge, layout that comprise the program
     *
     * @return list of above
     */
    public PieceList getPieceList() {
        return pieceList;
    }

    /**
     * Visitor method
     *
     * @param v proj18AhnSlagerZhao.bantam.visitor object
     * @return result of visiting this node
     * @see proj18AhnSlagerZhao.bantam.visitor.Visitor
     */
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
