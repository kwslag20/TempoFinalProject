package proj18AhnSlager.bantam.ast;

import proj18AhnSlager.bantam.visitor.Visitor;

public class Sequences extends ASTNode{

    /**
     * SequencesList containing the different sequences
     */
    protected SequencesList sequencesList;

    /**
     * Sequences constructor
     */
    public Sequences(int lineNum, SequencesList sequencesList){
        super(lineNum);
        this.sequencesList = sequencesList;
    }

    /**
     * Getter for sequencesList
     *
     * @return sequencesList
     */
    public SequencesList getSequencesList() { return this.sequencesList; }

    /**
     * Visitor method
     *
     * @param  v proj18AhnSlager.bantam.visitor object
     * @return result of visiting this node
     * @see proj18AhnSlager.bantam.visitor.Visitor
     */
    public Object accept(Visitor v) { return v.visit(this); }


}
