package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class LOrExpNode extends Node {
    private LAndExpNode lAndExp = null;
    private Token operator = null;
    private LOrExpNode lOrExp = null;

    public LOrExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof LAndExpNode) {
                lAndExp = (LAndExpNode) child;
            } else if (child instanceof LOrExpNode) {
                lOrExp = (LOrExpNode) child;
            } else if (child instanceof TokenNode) {
                operator = ((TokenNode) child).getToken();
            }
        }
    }

    public LAndExpNode getLAndExp() { return lAndExp; }
    public LOrExpNode getLOrExp() { return lOrExp; }
    public Token getOperator() { return operator; }
}
