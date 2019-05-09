package proj18AhnSlager.bantam.ast;

import proj18AhnSlager.bantam.visitor.Visitor;

public class SeqObj extends Member {

    /**
     * The name of the sequence object
     */
    protected String name;

    /**
     * The number of times the sequence will be repeated
     */
    protected String repeats;

    /**
     * The instrument the sequence is to be played in
     */
    protected String instrument;

    /**
     * Whether or not the sequence should be played in a different voice
     */
    protected String voice;

    /**
     * Constructor for a seqObj
     *
     * @param linenum
     * @param name
     * @param repeats
     * @param instrument
     * @param voice
     */
    public SeqObj(int linenum, String name, String repeats, String instrument, String voice){
        super(linenum);
        this.name = name;
        this.repeats = repeats;
        this.instrument = instrument;
        this.voice = voice;
    }

    /**
     * Getter for sequence object name
     *
     * @return name
     */
    public String getName() { return this.name; }

    /**
     * Getter for sequence object repeats
     *
     * @return name
     */
    public String getRepeats() { return this.repeats; }

    /**
     * Getter for sequence object instrument
     *
     * @return name
     */
    public String getInstrument() { return this.instrument; }

    /**
     * Getter for sequence object voice
     *
     * @return name
     */
    public String getVoice() { return this.voice; }

    /**
     * Visitor method
     *
     * @param v proj18AhnSlager.bantam.visitor object
     * @return the result of the visit
     * @see proj18AhnSlager.bantam.visitor.Visitor
     */
    public Object accept(Visitor v){ return v.visit(this); }
}
