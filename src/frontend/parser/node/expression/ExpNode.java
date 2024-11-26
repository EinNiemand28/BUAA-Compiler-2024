package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class ExpNode extends Node {
    private AddExpNode addExp = null;
    private boolean isConst = false;
    private int constValue = 0;

    public ExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof AddExpNode) {
                addExp = (AddExpNode) child;
            }
        }
    }

    public void setConstValue(int value) {
        constValue = value;
        isConst = true;
    }

    public AddExpNode getAddExp() { return addExp; }
    public boolean isConst() { return isConst; }
    public int getConstValue() { return constValue; }
}
