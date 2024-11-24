package frontend.parser.node.declaration;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class DeclNode extends Node {
    public DeclNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }


}
