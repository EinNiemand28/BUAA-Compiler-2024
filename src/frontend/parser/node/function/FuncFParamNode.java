package frontend.parser.node.function;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;
import frontend.parser.node.declaration.BTypeNode;

import java.util.List;

public class FuncFParamNode extends Node {
    private Token ident = null;
    private BTypeNode bType = null;

    public FuncFParamNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.IDENFR) {
                    ident = ((TokenNode) child).getToken();
                }
            } else if (child instanceof BTypeNode) {
                bType = (BTypeNode) child;
            }
        }
    }

    public Token getIdent() { return ident; }
    public BTypeNode getBType() { return bType; }
}
