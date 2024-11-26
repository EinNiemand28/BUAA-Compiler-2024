package frontend.parser.node.function;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class MainFuncDefNode extends Node {
    private BlockNode block = null;
    private Token ident = null;

    public MainFuncDefNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof BlockNode) {
                block = (BlockNode) child;
            } else if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.MAINTK) {
                    ident = ((TokenNode) child).getToken();
                }
            }
        }
    }

    public BlockNode getBlock() { return block; }
    public Token getIdent() { return ident; }
}
