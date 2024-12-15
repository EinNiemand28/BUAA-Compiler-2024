package frontend.parser.node.declaration;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;
import frontend.parser.node.expression.ExpNode;

import java.util.ArrayList;
import java.util.List;

public class InitValNode extends Node {
    private ExpNode exp = null;
    private final List<ExpNode> exps = new ArrayList<>();
    private String stringConst = null;
    public InitValNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        boolean flag = false;
        for (Node child : children) {
            if (child instanceof ExpNode) {
                exps.add((ExpNode) child);
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
        if (!flag && !exps.isEmpty()) {
            exp = exps.get(0);
            exps.clear();
        }
    }

    public ExpNode getExp() { return exp; }
    public List<ExpNode> getExps() { return exps; }
    public String getStringConst() { return stringConst; }
}
