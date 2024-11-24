package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class PrintfStmtNode extends Node {
    public PrintfStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
