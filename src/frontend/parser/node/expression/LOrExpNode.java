package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class LOrExpNode extends Node {
    public LOrExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
