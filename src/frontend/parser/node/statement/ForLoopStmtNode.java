package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;
import frontend.parser.node.expression.CondNode;

import java.util.List;

public class ForLoopStmtNode extends StmtNode {
    private ForStmtNode initStmt = null;
    private CondNode condStmt = null;
    private ForStmtNode forStmt = null;
    private StmtNode stmt = null;

    public ForLoopStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        int count = 0;
        for (Node child : children) {
            if (child instanceof TokenNode &&
                    ((TokenNode) child).getToken().type() == TokenType.SEMICN) {
                count++;
            }
            if (child instanceof ForStmtNode) {
                if (count == 0) {
                    initStmt = (ForStmtNode) child;
                } else {
                    forStmt = (ForStmtNode) child;
                }
            } else if (child instanceof CondNode) {
                condStmt = (CondNode) child;
            } else if (child instanceof StmtNode) {
                stmt = (StmtNode) child;
            }
        }
    }

    public ForStmtNode getInitStmt() { return initStmt; }
    public CondNode getCondStmt() { return condStmt; }
    public ForStmtNode getForStmt() { return forStmt; }
    public StmtNode getStmt() { return stmt; }
}
