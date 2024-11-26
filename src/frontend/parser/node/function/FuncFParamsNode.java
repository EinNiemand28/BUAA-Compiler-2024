package frontend.parser.node.function;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.ArrayList;
import java.util.List;

public class FuncFParamsNode extends Node {
    private final List<FuncFParamNode> funcFParams = new ArrayList<>();

    public FuncFParamsNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof FuncFParamNode) {
                funcFParams.add((FuncFParamNode) child);
            }
        }
    }

    public List<FuncFParamNode> getFuncFParams() { return funcFParams; }
}
