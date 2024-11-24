package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class EqExpNode extends Node {
    public EqExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
