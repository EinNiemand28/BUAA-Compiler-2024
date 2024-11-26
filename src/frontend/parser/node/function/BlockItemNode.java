package frontend.parser.node.function;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;
import frontend.parser.node.declaration.DeclNode;
import frontend.parser.node.statement.StmtNode;

import java.util.List;

public class BlockItemNode extends Node {
    private DeclNode decl = null;
    private StmtNode stmt = null;
    public BlockItemNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof DeclNode) {
                decl = (DeclNode) child;
            } else if (child instanceof StmtNode) {
                stmt = (StmtNode) child;
            }
        }
    }

    public DeclNode getDecl() { return decl; }
    public StmtNode getStmt() { return stmt; }
}
