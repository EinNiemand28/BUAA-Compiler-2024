package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class CharacterNode extends Node {
    private char character = 0;

    public CharacterNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.CHRCON) {
                    String tmp = ((TokenNode) child).getToken().content();
                    if (tmp.charAt(1) == '\\') {
                        switch (tmp.charAt(2)) {
                            case 'n' -> character = '\n';
                            case 't' -> character = '\t';
                            case '\\' -> character = '\\';
                            case '\"' -> character = '\"';
                            case '\'' -> character = '\'';
                            default -> {}
                        }
                    } else if (tmp.charAt(1) == '\'') {
                        character = 0;
                    } else {
                        character = tmp.charAt(1);
                    }
                }
            }
        }
    }

    public char getCharacter() { return character; }
}
