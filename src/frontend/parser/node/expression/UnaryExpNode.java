package frontend.parser.node.expression;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;
import frontend.parser.node.function.FuncRParamsNode;

import java.util.List;

public class UnaryExpNode extends Node {
    private PrimaryExpNode primaryExp = null;
    private UnaryExpNode unaryExp = null;
    private UnaryOpNode unaryOp = null;
    private Token ident = null;
    private FuncRParamsNode funcRParams = null;

    public UnaryExpNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof PrimaryExpNode) {
                primaryExp = (PrimaryExpNode) child;
            } else if (child instanceof UnaryExpNode) {
                unaryExp = (UnaryExpNode) child;
            } else if (child instanceof UnaryOpNode) {
                unaryOp = (UnaryOpNode) child;
            } else if (child instanceof FuncRParamsNode) {
                funcRParams = (FuncRParamsNode) child;
            } else if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.IDENFR) {
                    ident = ((TokenNode) child).getToken();
                }
            }
        }
    }

    public PrimaryExpNode getPrimaryExp() { return primaryExp; }
    public UnaryExpNode getUnaryExp() { return unaryExp; }
    public UnaryOpNode getUnaryOp() { return unaryOp; }
    public FuncRParamsNode getFuncRParams() { return funcRParams; }
    public Token getIdent() { return ident; }
}
