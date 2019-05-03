package proj18AhnSlagerZhao.bantam.codegenmusic;

import proj18AhnSlagerZhao.bantam.ast.*;
import proj18AhnSlagerZhao.bantam.visitor.Visitor;

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

    public MusicCodeGenVisitor(PrintStream out){
        this.out = out;
        this.patternCount = 0;
        this.sectionName = "";
        this.currentPattern = "";
        this.orderObjects = new ArrayList<>();
        this.patternMap = new HashMap<>();

    }
    /**
     * Visit an Piece node (should never be called)
     *
     * @param node the AST node
     * @return result of the visit
     */

    public Object visit(Piece node){
        node.getPieceList().accept(this);
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
        node.getNotesList().accept(this);
        ArrayList<String> pattern = new ArrayList<>();
        pattern.add(Integer.toString(patternCount));
        pattern.add(currentPattern);
        patternMap.put(this.sectionName + "lh", pattern);
        patternCount++;
        currentPattern = "";
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
        String note = node.getPitch().toUpperCase();
        note += node.getOctave();
        note += node.getNoteLength().replace("n","");
        note += " ";
        currentPattern += note;
        return null;
    }
}
