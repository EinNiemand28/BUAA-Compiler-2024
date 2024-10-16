package frontend.parser.node.statement;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class ForLoopStmtNode extends Node {
    public ForLoopStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
