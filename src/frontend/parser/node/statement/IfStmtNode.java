package frontend.parser.node.statement;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class IfStmtNode extends Node {
    public IfStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
