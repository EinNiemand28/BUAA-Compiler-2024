package frontend.parser.node.expression;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class LAndExpNode extends Node {
    public LAndExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
