package frontend.parser.node.statement;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class GetCharStmtNode extends Node {
    public GetCharStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
