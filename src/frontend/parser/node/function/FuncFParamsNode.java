package frontend.parser.node.function;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class FuncFParamsNode extends Node {
    public FuncFParamsNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }
}
