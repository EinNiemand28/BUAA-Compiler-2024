package frontend.parser.node;

import enums.SyntaxCompType;
import frontend.lexer.Token;
import utils.Printer;

import java.io.IOException;
import java.util.ArrayList;

public class TokenNode extends Node {
    private final Token token;

    public TokenNode(SyntaxCompType type, Token token) {
        super(type, new ArrayList<>());
        this.token = token;
        super.beginLine = super.endLine = token.lineno();
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void print() throws IOException {
        Printer.printSyntaxComp(token.toString());
    }

    public String toString() {
        return token.toString();
    }
}
