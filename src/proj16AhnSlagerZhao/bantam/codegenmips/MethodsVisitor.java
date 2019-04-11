package proj16AhnSlagerZhao.bantam.codegenmips;

import proj16AhnSlagerZhao.bantam.ast.Class_;
import proj16AhnSlagerZhao.bantam.ast.Method;
import proj16AhnSlagerZhao.bantam.ast.Program;
import proj16AhnSlagerZhao.bantam.visitor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;

public class MethodsVisitor extends Visitor {
    private HashMap<Class_, ArrayList<String>> methodsMap;
    private Class_ className;

    public HashMap<Class_, ArrayList<String>> getMethodsMap(Program ast){
        methodsMap = new HashMap<>();
        ast.accept(this);
        return methodsMap;
    }

    public Object visit(Class_ node){
        className = node;
        methodsMap.put(node, new ArrayList<>());
        super.visit(node);
        return null;
    }

    public Object visit(Method node){
        methodsMap.get(className).add(node.getName());
        super.visit(node);
        return null;
    }
}

