package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class AddExpNode extends Node {
    private AddExpNode addExp = null;
    private Token operator = null;
    private MulExpNode mulExp = null;
    private boolean isConst = false;
    private int constValue = 0;

    public AddExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof AddExpNode) {
                addExp = (AddExpNode) child;
            } else if (child instanceof MulExpNode) {
                mulExp = (MulExpNode) child;
            } else if (child instanceof TokenNode) {
                operator = ((TokenNode) child).getToken();
            }
        }
    }

    public void setConstValue(int value) {
        constValue = value;
        isConst = true;
    }

    public AddExpNode getAddExp() { return addExp; }
    public Token getOperator() { return operator; }
    public MulExpNode getMulExp() { return mulExp; }
    public boolean isConst() { return isConst; }
    public int getConstValue() { return constValue; }
}
