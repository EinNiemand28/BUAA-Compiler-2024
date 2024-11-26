package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class CharacterNode extends Node {
    private Token character = null;

    public CharacterNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.CHRCON) {
                    character = ((TokenNode) child).getToken();
                }
            }
        }
    }

    public Token getCharacter() { return character; }
}
