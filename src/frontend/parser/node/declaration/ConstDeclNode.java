package frontend.parser.node.declaration;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class ConstDeclNode extends Node {
    public ConstDeclNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
