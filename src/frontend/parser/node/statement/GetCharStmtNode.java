package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;
import frontend.parser.node.expression.LValNode;

import java.util.List;

public class GetCharStmtNode extends StmtNode {
    private LValNode lVal = null;

    public GetCharStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof LValNode) {
                lVal = (LValNode) child;
            }
        }
    }

    public LValNode getLVal() { return lVal; }
}
