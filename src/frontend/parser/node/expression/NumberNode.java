package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class NumberNode extends Node {
    public NumberNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
