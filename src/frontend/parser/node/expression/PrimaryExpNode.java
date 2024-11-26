package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class PrimaryExpNode extends Node {
    private ExpNode exp = null;
    private LValNode lVal = null;
    private NumberNode number = null;
    private CharacterNode character = null;

    public PrimaryExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof ExpNode) {
                exp = (ExpNode) child;
            } else if (child instanceof LValNode) {
                lVal = (LValNode) child;
            } else if (child instanceof NumberNode) {
                number = (NumberNode) child;
            } else if (child instanceof CharacterNode) {
                character = (CharacterNode) child;
            }
        }
    }

    public ExpNode getExp() { return exp; }
    public LValNode getLVal() { return lVal; }
    public NumberNode getNumber() { return number; }
    public CharacterNode getCharacter() { return character; }
}
