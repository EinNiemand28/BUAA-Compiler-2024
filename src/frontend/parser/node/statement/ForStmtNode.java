package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;
import frontend.parser.node.expression.ExpNode;
import frontend.parser.node.expression.LValNode;

import java.util.List;

public class ForStmtNode extends StmtNode {
    private LValNode lVal = null;
    private ExpNode exp = null;

    public ForStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof LValNode) {
                lVal = (LValNode) child;
            } else if (child instanceof ExpNode) {
                exp = (ExpNode) child;
            }
        }
    }

    public LValNode getLVal() { return lVal; }
    public ExpNode getExp() { return exp; }
}
