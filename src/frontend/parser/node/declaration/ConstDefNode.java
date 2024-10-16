package frontend.parser.node.declaration;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class ConstDefNode extends Node {
    public ConstDefNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
