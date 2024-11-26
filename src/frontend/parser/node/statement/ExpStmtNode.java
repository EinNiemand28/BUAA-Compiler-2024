package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;
import frontend.parser.node.expression.ExpNode;

import java.util.List;

public class ExpStmtNode extends StmtNode {
    private ExpNode exp = null;

    public ExpStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof ExpNode) {
                exp = (ExpNode) child;
            }
        }
    }

    public ExpNode getExp() { return exp; }
}
