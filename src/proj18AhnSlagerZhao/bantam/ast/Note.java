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
 * The <tt>Note</tt> class represents a note declaration
 * appearing in a verse, chorus or bridge declaration.  It contains a length (<tt>length</tt>),
 * a name (<tt>name</tt>), a pitch (<tt>pitch</tt>), and an optional initialization expression
 * (<tt>init</tt>).
 *
 * @see ASTNode
 */
public class Note extends Member {
    /**
     * The length of the note
     */
    protected String length;

    /**
     * The pitch of the note
     */
    protected String pitch;

    /**
     * The octave value for the note
     */
    protected int octave;

    /**
     * Field constructor
     *
     * @param lineNum source line number corresponding to this AST node
     * @param length    the length of the note
     * @param pitch    the pitch of the note
     * @param octave   the octave of the note
     */
    public Note(int lineNum, String length, String pitch, int octave) {
        super(lineNum);
        this.length = length;
        this.pitch = pitch;
        this.octave = octave;
    }

    /**
     * Get the length of the note
     *
     * @return note length
     */
    public String getNoteLength() {
        return length;
    }

    /**
     * Get the pitch of the note
     *
     * @return note pitch
     */
    public String getPitch() {
        return pitch;
    }

    /**
     * Get the octave of a note
     *
     * @return note octave
     */
    public int getOctave() { return octave;}

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
