package frontend.parser.node;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.declaration.DeclNode;
import frontend.parser.node.function.FuncDefNode;
import frontend.parser.node.function.MainFuncDefNode;

import java.util.ArrayList;
import java.util.List;

public class CompUnitNode extends Node {
    private final List<DeclNode> decls = new ArrayList<>();
    private final List<FuncDefNode> funcDefs = new ArrayList<>();
    private MainFuncDefNode mainFunc;
    public CompUnitNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof DeclNode) {
                decls.add((DeclNode) child);
            } else if (child instanceof FuncDefNode) {
                funcDefs.add((FuncDefNode) child);
            } else if (child instanceof MainFuncDefNode) {
                mainFunc = (MainFuncDefNode) child;
            }
        }
    }

    public List<DeclNode> getDecls() { return decls; }
    public List<FuncDefNode> getFuncDefs() { return funcDefs; }
    public MainFuncDefNode getMainFunc() { return mainFunc; }
}
