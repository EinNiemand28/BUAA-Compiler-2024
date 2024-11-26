package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class ConstExpNode extends Node {
    private AddExpNode addExp = null;
    private int constValue = 0;

    public ConstExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof AddExpNode) {
                addExp = (AddExpNode) child;
            }
        }
    }

    public void setConstValue(int value) {
        this.constValue = value;
    }

    public AddExpNode getAddExp() { return addExp; }
    public int getConstValue() { return constValue; }
}
