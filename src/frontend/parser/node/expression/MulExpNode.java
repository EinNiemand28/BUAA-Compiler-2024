package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class MulExpNode extends Node {
    private MulExpNode mulExp = null;
    private Token operator = null;
    private UnaryExpNode unaryExp = null;

    public MulExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof UnaryExpNode) {
                unaryExp = (UnaryExpNode) child;
            } else if (child instanceof MulExpNode) {
                mulExp = (MulExpNode) child;
            } else if (child instanceof TokenNode) {
                operator = ((TokenNode) child).getToken();
            }
        }
    }

    public MulExpNode getMulExp() { return mulExp; }
    public Token getOperator() { return operator; }
    public UnaryExpNode getUnaryExp() { return unaryExp; }
}
