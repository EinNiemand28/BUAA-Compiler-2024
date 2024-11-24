package frontend.symbol;

import frontend.enums.SymbolType;
import frontend.lexer.Token;

public abstract class Symbol {
    private final Token token;
    private final SymbolType type;

    public Symbol(Token token, SymbolType type) {
        this.token = token;
        this.type = type;
    }

    public Token getToken() {
        return token;
    }

    public String getContent() {
        return token.content();
    }

    @Override
    public String toString() {
        return token.content() + " " + type;
    }
}
