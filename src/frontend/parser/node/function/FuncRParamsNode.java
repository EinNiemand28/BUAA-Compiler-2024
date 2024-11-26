package frontend.parser.node.function;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;
import frontend.parser.node.expression.ExpNode;

import java.util.ArrayList;
import java.util.List;

public class FuncRParamsNode extends Node {
    private final List<ExpNode> exps = new ArrayList<>();

    public FuncRParamsNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof ExpNode) {
                exps.add((ExpNode) child);
            }
        }
    }

    public List<ExpNode> getExps() { return exps; }
}
