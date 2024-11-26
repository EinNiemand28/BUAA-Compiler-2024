package frontend.parser.node.declaration;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.ArrayList;
import java.util.List;

public class VarDeclNode extends Node {
    private BTypeNode bType = null;
    private final List<VarDefNode> varDefs = new ArrayList<>();

    public VarDeclNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof VarDefNode) {
                varDefs.add((VarDefNode) child);
            } else if (child instanceof BTypeNode) {
                bType = (BTypeNode) child;
            }
        }
    }

    public List<VarDefNode> getVarDefs() { return varDefs; }
    public BTypeNode getBType() { return bType; }
}
