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
 * The <tt>Class_</tt> class represents a class declaration,
 * which consists of a filename (<tt>filename</tt>), a class name
 * (<tt>name</tt>), the name of its parent class (<tt>parent</tt>),
 * and a list of members (<tt>members</tt>) which can be either field
 * declarations or method declarations.
 *
 * @see ASTNode
 */
public class Verse extends ASTNode {
    /**
     * The name of this verse
     */
    protected String name;

    /**
     * List of the verse members
     */
    protected MemberList memberList;

    /**
     * Class_ constructor
     *
     * @param lineNum    source line number corresponding to this AST node
     * @param name       the name of this class
     * @param memberList a list of the class members
     */
    public Verse(int lineNum, String name, MemberList memberList) {
        super(lineNum);
        this.name = name;
        this.memberList = memberList;
    }

    /**
     * Get the name of this class
     *
     * @return class name
     */
    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    /**
     * Get list of members that this class contains
     *
     * @return list of fields
     */
    public MemberList getMemberList() {
        return memberList;
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
