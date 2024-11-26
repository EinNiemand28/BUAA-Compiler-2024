package frontend.parser.node.statement;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;
import frontend.parser.node.function.BlockNode;

import java.util.List;

public class BlockStmtNode extends StmtNode {
    private BlockNode block = null;

    public BlockStmtNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof BlockNode) {
                block = (BlockNode) child;
            }
        }
    }

    public BlockNode getBlock() { return block; }
}
