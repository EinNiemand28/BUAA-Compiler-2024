package frontend.parser.node.declaration;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;
import frontend.parser.node.expression.ConstExpNode;

import java.util.ArrayList;
import java.util.List;

public class ConstInitValNode extends Node {
    private final List<ConstExpNode> constExps = new ArrayList<>();
    private Token stringConst = null;

    public ConstInitValNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof ConstExpNode) {
                constExps.add((ConstExpNode) child);
            } else if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.STRCON) {
                    stringConst = ((TokenNode) child).getToken();
                }
            }
        }
    }

    public List<ConstExpNode> getConstExps() { return constExps; }
    public Token getStringConst() { return stringConst; }
}
