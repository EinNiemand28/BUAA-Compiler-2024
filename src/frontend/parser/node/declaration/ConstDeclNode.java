package frontend.parser.node.declaration;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.ArrayList;
import java.util.List;

public class ConstDeclNode extends Node {
    private BTypeNode bType = null;
    private final List<ConstDefNode> constDefs = new ArrayList<>();

    public ConstDeclNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof ConstDefNode) { constDefs.add((ConstDefNode) child); }
            else if (child instanceof BTypeNode) {
                bType = (BTypeNode) child;
            }
        }
    }

    public List<ConstDefNode> getConstDefs() { return constDefs; }
    public BTypeNode getBType() { return bType; }
}
