package frontend.parser.node.function;

import enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class FuncTypeNode extends Node {
    private String typeName;
    public FuncTypeNode(SyntaxCompType type, List<Node> children, String typeName) {
        super(type, children);
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
