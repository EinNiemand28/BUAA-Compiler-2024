package frontend.parser.node.expression;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class UnaryExpNode extends Node {
    public UnaryExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
