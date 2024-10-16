package frontend.parser.node.function;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class FuncTypeNode extends Node {
    public FuncTypeNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
