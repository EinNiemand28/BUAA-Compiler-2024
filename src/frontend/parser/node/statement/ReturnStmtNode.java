package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class ReturnStmtNode extends Node {
    public ReturnStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
