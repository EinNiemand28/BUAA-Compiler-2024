package frontend.lexer;

import frontend.enums.TokenType;

public record Token(TokenType type, String content, int lineno) {

    @Override
    public String toString() {
        return type.toString() + " " + content + "\n";
    }
}