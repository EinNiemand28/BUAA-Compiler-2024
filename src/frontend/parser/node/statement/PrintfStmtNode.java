package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;
import frontend.parser.node.expression.ExpNode;

import java.util.ArrayList;
import java.util.List;

public class PrintfStmtNode extends StmtNode {
    private String stringConst = null;
    private final List<ExpNode> exps = new ArrayList<>();

    public PrintfStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof ExpNode) {
                exps.add((ExpNode) child);
            } else if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.STRCON) {
                    String tmp = ((TokenNode) child).getToken().content();
                    stringConst = tmp.substring(1, tmp.length() - 1);
                }
            }
        }
    }

    public List<ExpNode> getExps() { return exps; }
    public String getStringConst() { return stringConst; }
}
