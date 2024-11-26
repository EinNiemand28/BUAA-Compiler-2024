package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class UnaryOpNode extends Node {
    private Token operator = null;

    public UnaryOpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof TokenNode) {
                operator = ((TokenNode) child).getToken();
            }
        }
    }

    public Token getOperator() { return operator; }
}
