package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.ArrayList;
import java.util.List;

public class LAndExpNode extends Node {
    private EqExpNode eqExp = null;
    private Token operator = null;
    private LAndExpNode lAndExp = null;
    private boolean isConst = false;
    private int constValue = 0;

    public LAndExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof EqExpNode) {
                eqExp = (EqExpNode) child;
            } else if (child instanceof LAndExpNode) {
                lAndExp = (LAndExpNode) child;
            } else if (child instanceof TokenNode) {
                operator = ((TokenNode) child).getToken();
            }
        }
    }

    public void setConstValue(int value) {
        isConst = true;
        constValue = value;
    }

    public EqExpNode getEqExp() { return eqExp; }
    public LAndExpNode getLAndExp() { return lAndExp; }
    public Token getOperator() { return operator; }
    public int getConstValue() { return constValue; }
    public boolean isConst() { return isConst; }

    public List<EqExpNode> getEqExps() {
        List<EqExpNode> allEqExps = new ArrayList<>();
        if (lAndExp != null) {
            allEqExps.addAll(lAndExp.getEqExps());
        }
        if (eqExp != null) {
            allEqExps.add(eqExp);
        }
        return allEqExps;
    }
}
