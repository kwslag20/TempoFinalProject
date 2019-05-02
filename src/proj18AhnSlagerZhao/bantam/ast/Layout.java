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

import java.util.ArrayList;

/**
 * The <tt>Layout</tt> class represents a class declaration,
 * which consists of a filename (<tt>filename</tt>), a class name
 * (<tt>name</tt>), the name of its parent class (<tt>parent</tt>),
 * and a list of members (<tt>members</tt>) which can be either field
 * declarations or method declarations.
 *
 * @see ASTNode
 */
public class Layout extends ASTNode {

    /**
     * LayoutList containing the properties of the Layout
     */
    protected LayoutList layoutList;

    /**
     * Class_ constructor
     *
     * @param lineNum    source line number corresponding to this AST node
     */
    public Layout(int lineNum, LayoutList layoutList) {
        super(lineNum);
        this.layoutList = layoutList;
    }

    /**
     * Getter for layoutList
     *
     * @return layoutList
     */
    public LayoutList getLayoutList(){
        return this.layoutList;
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
