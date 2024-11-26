package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;
import frontend.parser.node.expression.CondNode;

import java.util.List;

public class IfStmtNode extends StmtNode {
    private CondNode cond = null;
    private StmtNode thenStmt = null;
    private StmtNode elseStmt = null;

    public IfStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof CondNode) {
                cond = (CondNode) child;
            } else if (child instanceof StmtNode) {
                if (thenStmt == null) {
                    thenStmt = (StmtNode) child;
                } else if (elseStmt == null) {
                    elseStmt = (StmtNode) child;
                }
            }
        }
    }

    public CondNode getCond() { return cond; }
    public StmtNode getThenStmt() { return thenStmt; }
    public StmtNode getElseStmt() { return elseStmt; }
}
