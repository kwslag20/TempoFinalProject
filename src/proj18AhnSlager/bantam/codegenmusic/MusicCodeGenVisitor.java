package proj18AhnSlager.bantam.codegenmusic;

import proj18AhnSlager.bantam.ast.*;
import proj18AhnSlager.bantam.visitor.Visitor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MusicCodeGenVisitor extends Visitor {

    private PrintStream out;
    private HashMap<String, ArrayList<String>> patternMap;
    private int patternCount;
    private String sectionName;
    private String currentPattern;
    private ArrayList<String> orderObjects;
    private Boolean isRightHand, isLeftHand;
    private HashMap<String, Integer> noteValues;
    private HashMap<String, String> sequenceMap;
    private String currentSequencePatter;
    private int seqVoiceNum;
    private boolean inSequence;

    public MusicCodeGenVisitor(PrintStream out){
        this.inSequence = false;
        this.out = out;
        this.patternCount = 0;
        this.sectionName = "";
        this.currentPattern = "";
        this.currentSequencePatter = "";
        this.seqVoiceNum = 15;
        this.orderObjects = new ArrayList<>();
        this.patternMap = new HashMap<>();
        this.sequenceMap = new HashMap<>();
        this.isLeftHand = false;
        this.isRightHand = false;
        this.noteValues = new HashMap<>();
        this.noteValues.put("c", 1);
        this.noteValues.put("c#", 2);
        this.noteValues.put("d", 3);
        this.noteValues.put("d#", 4);
        this.noteValues.put("e", 5);
        this.noteValues.put("f", 6);
        this.noteValues.put("f#", 7);
        this.noteValues.put("g", 8);
        this.noteValues.put("g#", 9);
        this.noteValues.put("a", 10);
        this.noteValues.put("a#", 11);
        this.noteValues.put("b", 12);

    }
    /**
     * Visit an Piece node (should never be called)
     *
     * @param node the AST node
     * @return result of the visit
     */

    public Object visit(Piece node){
        node.getPieceList().accept(this);
        System.out.println(this.patternMap);
        return null;
    }

    /**
     * Visit a PieceList node
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(PieceList node){
        for(ASTNode musicSection : node){
            musicSection.accept(this);
        }
        return null;
    }

    /**
     * Visit a Verse node
     *
     * @param node the Verse node
     * @return result of the visit
     */
    public Object visit(Verse node){
        this.inSequence = false;
        this.sectionName = node.getName();
        node.getMemberList().accept(this);
        return null;
    }

    /**
     * Visit a Chorus node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Chorus node){
        this.inSequence = false;
        this.sectionName = "chorus";
        node.getMemberList().accept(this);
        return null;
    }

    /**
     * Visit a Layout node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Layout node){
        this.inSequence = false;
        node.getLayoutList().accept(this);
        for(int i = 0; i < orderObjects.size(); i++){
            String output = "\t\tpattern" + "0.add(\"";
            output += patternMap.get(orderObjects.get(i)+"rh").get(1);
            output += "\");";
            out.println(output);
            output = "\t\tpattern" + "1.add(\"";
            output += patternMap.get(orderObjects.get(i)+"lh").get(1);
            output += "\");";
            out.println(output);
        }
        return null;
    }

    /**
     * Visit a MemberList node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(MemberList node){
        for (ASTNode hand : node)
            hand.accept(this);
        return null;
    }

    /**
     * Visit a RightHand node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(RightHand node){
        this.isRightHand = true;
        this.isLeftHand = false;
        node.getNotesList().accept(this);
        ArrayList<String> pattern = new ArrayList<>();
        pattern.add(Integer.toString(patternCount));
        pattern.add(currentPattern);
        patternMap.put(this.sectionName + "rh", pattern);
        patternCount++;
        currentPattern = "";
        return null;
    }

    /**
     * Visit a LeftHand node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(LeftHand node){
        this.isLeftHand = true;
        this.isRightHand = false;
        node.getNotesList().accept(this);
        ArrayList<String> pattern = new ArrayList<>();
        pattern.add(Integer.toString(patternCount));
        pattern.add(currentPattern);
        patternMap.put(this.sectionName + "lh", pattern);
        patternCount++;
        currentPattern = "";
        return null;
    }

    public Object visit(Sequences node){
        this.inSequence = true;
        node.getSequencesList().accept(this);
        return null;
    }

    public Object visit(SequencesList node){
        for(ASTNode seqs: node) seqs.accept(this);
        return null;
    }

    public Object visit(Sequence node){
        this.currentSequencePatter = "";
        node.getNotesList().accept(this);
        this.sequenceMap.put(node.getName(), this.currentSequencePatter);
        this.currentSequencePatter = "";
        return null;
    }

    public Object visit(SeqObj node){
        if(node.getInstrument().length() > 0){
            currentPattern += "I[" + node.getInstrument().toUpperCase().replace(" ", "") + "] ";
        }
        if(node.getVoice().length() > 0){
            currentPattern += "V" + this.seqVoiceNum + " ";
            this.seqVoiceNum--;
        }
        try {
            for (int i = 0; i < Integer.parseInt(node.getRepeats()); i++) {
                System.out.println(sequenceMap.get(node.getName()));
                currentPattern += sequenceMap.get(node.getName());
            }
        }
        catch(NumberFormatException e){
            System.out.println("Repeats must be an integer value");
        }
        return null;
    }

    /**
     * Visit a NotesList node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(NotesList node){
        for (ASTNode note: node)
            note.accept(this);
        return null;
    }

    /**
     * Visit a LayoutList node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(LayoutList node){
        for (ASTNode layoutObj: node){
            layoutObj.accept(this);
        }
        return null;
    }

    /**
     * Visit a Writer node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Writer node){
        return null;
    }

    /**
     * Visit a Instrument node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Instrument node){
        return null;
    }

    /**
     * Visit a Tempo node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Tempo node){
        return null;
    }

    /**
     * Visit a OrderObj node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(OrderObj node){
        orderObjects.add(node.getName());
        return null;
    }

    /**
     * Visit a Note node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Note node){
        if(!node.getPitch().contains(",")){
            String note = node.getPitch().toUpperCase();
            note += node.getOctave();
            note += node.getNoteLength().replace("n","");
            note += " ";
            if(!inSequence) {
                currentPattern += note;
            }
            currentSequencePatter += note;
        }
        else{
            String chord = "(";
            int octave = node.getOctave();
            String note = node.getPitch().replace("(","");
            note = note.substring(0, note.length()-1);
            String[] notes = note.split(",");
            for(int i = 0; i < notes.length; i++){
                chord += notes[i].toUpperCase().replace(")", "");
                if(i>0) {
                    if (this.noteValues.get(notes[i]) <= this.noteValues.get(notes[i - 1])) {
                        octave++;
                    }
                }
                chord += octave;
                if(i != notes.length - 1) {
                    chord += "+";
                }
            }
            chord += ")"+node.getNoteLength().replace("n", "")+" ";
            if(!inSequence) {
                currentPattern += chord;
            }
            currentSequencePatter += chord;
        }

        return null;
    }

    public Object visit(Rest node){
        String rest = "R";
        rest += node.getRestLength().replace("r", "");
        rest += " ";
        if(!inSequence) {
            currentPattern += rest;
        }
        currentSequencePatter += rest;
        return null;
    }
}
