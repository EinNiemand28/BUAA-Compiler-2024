package frontend.parser.node.declaration;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class InitValNode extends Node {
    public InitValNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
