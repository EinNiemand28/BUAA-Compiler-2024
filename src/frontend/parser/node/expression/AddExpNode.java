package frontend.parser.node.expression;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class AddExpNode extends Node {
    public AddExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
