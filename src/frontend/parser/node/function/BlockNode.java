package frontend.parser.node.function;

import frontend.enums.SyntaxCompType;
import frontend.parser.node.Node;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends Node {
    private final List<BlockItemNode> blockItems = new ArrayList<>();

    public BlockNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof BlockItemNode) {
                blockItems.add((BlockItemNode) child);
            }
        }
    }

    public List<BlockItemNode> getBlockItems() { return blockItems; }
}
