package frontend.parser.node.declaration;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class ConstInitValNode extends Node {
    public ConstInitValNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
