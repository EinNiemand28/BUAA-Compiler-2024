package frontend.parser.node.expression;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class CondNode extends Node {
    public CondNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
