package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class RelExpNode extends Node {
    private AddExpNode addExp = null;
    private Token operator = null;
    private RelExpNode relExp = null;
    private boolean isConst = false;
    private int constValue = 0;

    public RelExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof AddExpNode) {
                addExp = (AddExpNode) child;
            } else if (child instanceof RelExpNode) {
                relExp = (RelExpNode) child;
            } else if (child instanceof TokenNode) {
                operator = ((TokenNode) child).getToken();
            }
        }
    }

    public void setConstValue(int value) {
        isConst = true;
        this.constValue = value;
    }

    public AddExpNode getAddExp() { return addExp; }
    public RelExpNode getRelExp() { return relExp; }
    public Token getOperator() { return operator; }
    public int getConstValue() { return constValue; }
    public boolean isConst() { return isConst; }
}
