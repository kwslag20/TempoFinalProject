package proj16AhnSlagerZhao.bantam.codegenmips;

import proj16AhnSlagerZhao.bantam.ast.Class_;
import proj16AhnSlagerZhao.bantam.ast.Method;
import proj16AhnSlagerZhao.bantam.ast.Program;
import proj16AhnSlagerZhao.bantam.visitor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * visitor class to populate a hashmap with the classes being the key
 * and the methods being the value (and an arrayList)
 */
public class MethodsVisitor extends Visitor {
    private HashMap<Class_, ArrayList<String>> methodsMap;
    private Class_ className;


    /**
     * gets the map of the methods
     * @param ast
     * @return
     */
    public HashMap<Class_, ArrayList<String>> getMethodsMap(Program ast){
        methodsMap = new HashMap<>();
        ast.accept(this);
        return methodsMap;
    }

    /**
     * method to override the visit method
     * @param node the class node
     * @return
     */
    public Object visit(Class_ node){
        className = node;
        methodsMap.put(node, new ArrayList<>());
        super.visit(node);
        return null;
    }

    /**
     * method to override the visit method
     * @param node the class node
     * @return
     */
    public Object visit(Method node){
        methodsMap.get(className).add(node.getName());
        super.visit(node);
        return null;
    }
}

