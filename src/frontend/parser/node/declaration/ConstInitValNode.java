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
    private ConstExpNode constExp = null;
    private final List<ConstExpNode> constExps = new ArrayList<>();
    private String stringConst = null;

    public ConstInitValNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        boolean flag = false;
        for (Node child : children) {
            if (child instanceof ConstExpNode) {
                constExps.add((ConstExpNode) child);
            } else if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.STRCON) {
                    String tmp = ((TokenNode) child).getToken().content();
                    stringConst = tmp.substring(1, tmp.length() - 1);
                }
                if (((TokenNode) child).getToken().type() == TokenType.LBRACE) {
                    flag = true;
                }
            }
        }
        if (!flag && !constExps.isEmpty()) {
            constExp = constExps.get(0);
            constExps.clear();
        }
    }

    public ConstExpNode getConstExp() { return constExp; }
    public List<ConstExpNode> getConstExps() { return constExps; }
    public String getStringConst() { return stringConst; }
}
