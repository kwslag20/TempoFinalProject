package proj16AhnSlagerZhao.bantam.codegenmips;

import proj16AhnSlagerZhao.bantam.ast.ConstStringExpr;
import proj16AhnSlagerZhao.bantam.ast.FormalList;
import proj16AhnSlagerZhao.bantam.util.ClassTreeNode;
import proj16AhnSlagerZhao.bantam.visitor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StringConstantsVisitor extends Visitor {
    private ArrayList<String> stringList = new ArrayList<>();

    public Map<String, String> getStringConstants(ClassTreeNode node){
        Map<String,String> stringMap = new HashMap<>();
        for(ClassTreeNode classNode: node.getClassMap().values()){
            classNode.getASTNode().accept(this);
        }
        return stringMap;
    }


    /**
     * Overrides the visit method that takes in
     * a Constant String Expression.
     *
     * When the visitor encounters it, the constant gets
     * added to a string list
     * @param node the string constant expression node
     * @return
     */
    public Object visit(ConstStringExpr node){
        stringList.add(node.getConstant());
        return null;
    }

    /**
     * prevents further traversal
     * @param node the formal list node
     * @return
     */
    public Object visit(FormalList node){
        return null;
    }
}
