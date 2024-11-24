package frontend.parser.node.declaration;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.List;

public class BTypeNode extends Node {
    private String typeName;
    public BTypeNode(SyntaxCompType type, List<Node> children, String typeName) {
        super(type, children);
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
