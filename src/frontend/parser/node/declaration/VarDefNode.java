package frontend.parser.node.declaration;

import frontend.enums.SyntaxCompType;
import frontend.enums.TokenType;
import frontend.lexer.Token;
import frontend.parser.node.Node;
import frontend.parser.node.TokenNode;
import frontend.parser.node.expression.ConstExpNode;

import java.util.ArrayList;
import java.util.List;

public class VarDefNode extends Node {
    private Token ident = null;
    private final List<ConstExpNode> constExps = new ArrayList<>();
    private InitValNode initVal = null;

    public VarDefNode(SyntaxCompType type, List<Node> children) {
        super(type, children);
        for (Node child : children) {
            if (child instanceof ConstExpNode) {
                constExps.add((ConstExpNode) child);
            } else if (child instanceof InitValNode) {
                initVal = (InitValNode) child;
            } else if (child instanceof TokenNode) {
                if (((TokenNode) child).getToken().type() == TokenType.IDENFR) {
                    ident = ((TokenNode) child).getToken();
                }
            }
        }
    }

    public List<ConstExpNode> getConstExps() { return constExps; }
    public InitValNode getInitVal() { return initVal; }
    public Token getIdent() { return ident; }
}
