package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.ArrayList;
import java.util.List;

public class LValNode extends Node {
    private Token ident = null;
    private List<ExpNode> exps = new ArrayList<>();

    public LValNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof ExpNode) {
                exps.add((ExpNode) child);
            } else if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.IDENFR) {
                    ident = ((TokenNode) child).getToken();
                }
            }
        }
    }

    public List<ExpNode> getExps() { return exps; }
    public Token getIdent() { return ident; }
}
