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

package proj18AhnSlager.bantam.ast;

import proj18AhnSlager.bantam.visitor.Visitor;


/**
 * The <tt>OrderObj</tt> class represents a orderObj declaration.
 * It contains the name of the orderObj.
 *
 * @see ASTNode
 */
public class OrderObj extends Member {
    /**
     * The name of the orderObj
     */
    protected String name;

    /**
     * Tempo constructor
     *
     * @param lineNum source line number corresponding to this AST node
     * @param name    the name of the orderObj
     */
    public OrderObj(int lineNum, String name) {
        super(lineNum);
        this.name = name;
    }

    /**
     * Get the name of the orderObj
     *
     * @return orderObj
     */
    public String getName() {
        return name;
    }

    /**
     * Visitor method
     *
     * @param v proj18AhnSlager.bantam.visitor object
     * @return result of visiting this node
     * @see proj18AhnSlager.bantam.visitor.Visitor
     */
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
