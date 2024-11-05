package frontend.parser.node;

import enums.SyntaxCompType;
import utils.Printer;

import java.io.IOException;
import java.util.List;

public class Node {
    protected final SyntaxCompType type;
    protected final List<Node> children;
    protected int beginLine;
    protected int endLine;
    protected int size;

    public Node(SyntaxCompType type, List<Node> children) {
        this.type = type;
        this.children = children;
        if (type != SyntaxCompType.TOKEN) {
            this.size = 0;
            if (!children.isEmpty()) {
                this.beginLine = children.get(0).beginLine;
                this.endLine = children.get(children.size() - 1).endLine;
                for (Node child : children) { this.size += child.size; }
            } else { this.beginLine = this.endLine = 0; }
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

    public int getBeginLine() {
        return beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public List<Node> getChildren() {
        return children;
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

    public void printCons() {
        System.out.println(beginLine + " " + endLine + " " + type);
        for (Node child : children) {
            System.out.println(child.type);
        }
    }
}
