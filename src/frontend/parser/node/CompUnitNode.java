package frontend.parser.node;

import enums.SyntaxCompType;

import java.util.List;

public class CompUnitNode extends Node {
    public CompUnitNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
    }


}
