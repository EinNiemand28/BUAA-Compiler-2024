package frontend.symbol;

import frontend.enums.SymbolType;
import frontend.lexer.Token;

import llvm.value.Value;

public abstract class Symbol {
    private final Token token;
    private final SymbolType type;
    private Value irValue;

    public Symbol(Token token, SymbolType type) {
        this.token = token;
        this.type = type;
        this.irValue = null;
    }

    public Token getToken() {
        return token;
    }

    public String getContent() {
        return token.content();
    }

    public Value getValue() {
        return irValue;
    }

    public void setValue(Value irValue) {
        // call when declaring a variable/function
        this.irValue = irValue;
    }

    @Override
    public String toString() {
        return token.content() + " " + type;
    }
}
