package frontend.parser.node.declaration;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;
import frontend.parser.node.expression.ExpNode;

import java.util.ArrayList;
import java.util.List;

public class InitValNode extends Node {
    private final List<ExpNode> exps = new ArrayList<>();
    private Token stringConst = null;
    public InitValNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof ExpNode) {
                exps.add((ExpNode) child);
            } else if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.STRCON) {
                    stringConst = ((TokenNode) child).getToken();
                }
            }
        }
    }

    public List<ExpNode> getExps() { return exps; }
    public Token getStringConst() { return stringConst; }
}
