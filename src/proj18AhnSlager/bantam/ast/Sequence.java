package proj18AhnSlager.bantam.ast;

import proj18AhnSlager.bantam.visitor.Visitor;

public class Sequence extends ASTNode {

    /**
     * NotesList containing the different sequences
     */
    protected NotesList notesList;

    /**
     * Name of the sequence
     */
    protected String name;

    /**
     * Sequence constructor
     */
    public Sequence(int lineNum, String name, NotesList notesList){
        super(lineNum);
        this.name = name;
        this.notesList = notesList;
    }

    /**
     * Getter for name of the sequence
     *
     * @return name
     */
    public String getName() { return this.name; }


    /**
     * Getter for notesList
     *
     * @return notesList
     */
    public NotesList getNotesList() { return this.notesList; }

    /**
     * Visitor method
     *
     * @param  v proj18AhnSlager.bantam.visitor object
     * @return result of visiting this node
     * @see proj18AhnSlager.bantam.visitor.Visitor
     */
    public Object accept(Visitor v) { return v.visit(this); }
}
