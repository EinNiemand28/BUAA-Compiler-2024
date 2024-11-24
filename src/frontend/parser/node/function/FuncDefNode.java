package frontend.parser.node.function;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class FuncDefNode extends Node {
    public FuncDefNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }


}
