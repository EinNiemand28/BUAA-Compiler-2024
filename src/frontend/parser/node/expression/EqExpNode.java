package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class EqExpNode extends Node {
    private RelExpNode relExp = null;
    private Token operator = null;
    private EqExpNode eqExp = null;

    public EqExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof RelExpNode) {
                relExp = (RelExpNode) child;
            } else if (child instanceof EqExpNode) {
                eqExp = (EqExpNode) child;
            } else if (child instanceof TokenNode) {
                operator = ((TokenNode) child).getToken();
            }
        }
    }

    public RelExpNode getRelExp() { return relExp; }
    public EqExpNode getEqExp() { return eqExp; }
    public Token getOperator() { return operator; }
}
