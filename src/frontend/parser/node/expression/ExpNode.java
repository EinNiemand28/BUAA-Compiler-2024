package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class ExpNode extends Node {
    public ExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
