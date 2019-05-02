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
 * The <tt>LeftHand</tt> class represents a right hand declaration within
 * a verse, chorus or bridge.  It contains the memberList of a leftHand (<tt>memberList</tt>)
 *
 * @see ASTNode
 */
public class LeftHand extends Member {
    /**
     * A list of formal parameters
     */
    protected NotesList notesList;

    /**
     * Method constructor
     *
     * @param lineNum    source line number corresponding to this AST node
     * @param notesList list of notes and such in the hand
     */
    public LeftHand(int lineNum, NotesList notesList) {
        super(lineNum);
        this.notesList = notesList;
    }

    /**
     * Get list of notes
     *
     * @return list of notes
     */
    public NotesList getNotesList() {
        return notesList;
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
