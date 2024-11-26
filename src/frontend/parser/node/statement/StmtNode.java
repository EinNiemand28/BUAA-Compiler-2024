package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public abstract class StmtNode extends Node {
    public StmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
