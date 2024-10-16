package frontend.parser.node.expression;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class MulExpNode extends Node {
    public MulExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
