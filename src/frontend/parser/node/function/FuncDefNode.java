package frontend.parser.node.function;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;

import java.util.List;

public class FuncDefNode extends Node {
    private FuncTypeNode funcType;
    private Token ident = null;
    private FuncFParamsNode funcFParams = null;
    private BlockNode block = null;

    public FuncDefNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof FuncFParamsNode) {
                funcFParams = (FuncFParamsNode) child;
            } else if (child instanceof BlockNode) {
                block = (BlockNode) child;
            } else if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.IDENFR) {
                    ident = ((TokenNode) child).getToken();
                }
            } else if (child instanceof FuncTypeNode) {
                funcType = (FuncTypeNode) child;
            }
        }
    }

    public Token getIdent() { return ident; }
    public FuncFParamsNode getFuncFParams() { return funcFParams; }
    public BlockNode getBlock() { return block; }
    public FuncTypeNode getFuncType() { return funcType; }
}
