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
     *
     * @param piece
     * @return
     */
    public String getAuthorName(Piece piece){
        piece.accept(this);

        return authorName;
    }

    /**
     *
     * @param node the method node
     * @return
     */
    @Override
    public Object visit(Writer node){
        authorName = node.getWriter();
        return null;
    }
}
