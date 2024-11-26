package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class NumberNode extends Node {
    private Token number = null;

    public NumberNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.INTCON) {
                    number = ((TokenNode) child).getToken();
                }
            }
        }
    }

    public Token getNumber() { return number; }
}
