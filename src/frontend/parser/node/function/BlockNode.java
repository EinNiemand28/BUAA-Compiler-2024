package frontend.parser.node.function;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class BlockNode extends Node {
    public BlockNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
