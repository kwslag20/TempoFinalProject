package proj18AhnSlager.bantam.codegenmusic;

import proj18AhnSlager.bantam.ast.Piece;
import proj18AhnSlager.bantam.ast.Writer;
import proj18AhnSlager.bantam.visitor.Visitor;

/**
 * Visitor method to get the name of the writer
 */
public class WriterVisitor extends Visitor {

    String authorName;

    /**
     * method to start the visitng of a piece to grab the authors name
     * from the writer node
     * @param piece
     * @return the name of the author
     */
    public String getAuthorName(Piece piece){
        piece.accept(this);
        return this.authorName;
    }

    /**
     * overrides the visit method of the writer node to
     * get the name of the author
     * @param node the method node
     * @return
     */
    @Override
    public Object visit(Writer node){
        this.authorName = node.getWriter();
        return null;
    }
}
