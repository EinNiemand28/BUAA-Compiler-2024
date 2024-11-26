package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class BreakStmtNode extends StmtNode {
    private Token breakToken = null;
    private Token continueToken = null;

    public BreakStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof TokenNode) {
                switch (((TokenNode) child).getToken().type()) {
                    case BREAKTK -> breakToken = ((TokenNode) child).getToken();
                    case CONTINUETK -> continueToken = ((TokenNode) child).getToken();
                    default -> {}
                }
            }
        }
    }

    public Token getBreakToken() { return breakToken; }
    public Token getContinueToken() { return continueToken; }
}
