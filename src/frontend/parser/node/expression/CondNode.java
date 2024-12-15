package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class CondNode extends Node {
    private LOrExpNode lOrExp = null;
    private boolean isConst = false;
    private int constValue = 0;

    public CondNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof LOrExpNode) {
                lOrExp = (LOrExpNode) child;
            }
        }
    }

    public void setConstValue(int value) {
        constValue = value;
        isConst = true;
    }

    public LOrExpNode getLOrExp() { return lOrExp; }
    public boolean isConst() { return isConst; }
    public int getConstValue() { return constValue; }
}
