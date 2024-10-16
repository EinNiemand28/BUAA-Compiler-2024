package frontend.parser.node;

import enums.SyntaxCompType;
import utils.Printer;

import java.io.IOException;
import java.util.List;

public class Node {
    protected final SyntaxCompType type;
    protected final List<Node> children;
    protected final int beginLine;
    protected final int endLine;
    protected int size;

    public Node(SyntaxCompType type, List<Node> children) {
        this.type = type;
        this.children = children;
        if (type != SyntaxCompType.TOKEN) {
            this.beginLine = children.get(0).beginLine;
            this.endLine = children.get(children.size() - 1).endLine;
            this.size = 0;
            for (Node child : children) { this.size += child.size; }
        } else {
            this.beginLine = this.endLine = 0;
            this.size = 1;
        }
    }

    public SyntaxCompType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public void print() throws IOException {
        for (Node child : children) {
            child.print();
        }
        if (type != SyntaxCompType.BlockItem &&
            type != SyntaxCompType.Decl &&
            type != SyntaxCompType.BType) {
            Printer.printSyntaxComp(type.toString());
        }
    }
}
