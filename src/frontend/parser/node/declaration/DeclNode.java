package frontend.parser.node.declaration;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class DeclNode extends Node {
    private ConstDeclNode constDecl = null;
    private VarDeclNode varDecl = null;

    public DeclNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof ConstDeclNode) { constDecl = (ConstDeclNode) child; }
            else if (child instanceof VarDeclNode) { varDecl = (VarDeclNode) child; }
        }
    }

    public ConstDeclNode getConstDecl() { return constDecl; }
    public VarDeclNode getVarDecl() { return varDecl; }
}
