package frontend.parser.node.declaration;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class BTypeNode extends Node {
    public BTypeNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
