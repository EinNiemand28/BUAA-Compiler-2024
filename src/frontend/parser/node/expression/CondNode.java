package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class CondNode extends Node {
    private LOrExpNode lOrEXp = null;

    public CondNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof LOrExpNode) {
                lOrEXp = (LOrExpNode) child;
            }
        }
    }

    public LOrExpNode getLOrEXp() { return lOrEXp; }
}
