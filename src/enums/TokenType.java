package enums;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {
    IDENFR,         // Ident
    INTCON,         // IntConst
    STRCON,         // StringConst
    CHRCON,         // CharConst
    MAINTK("main"),
    CONSTTK("const"),
    INTTK("int"),
    CHARTK("char"),
    BREAKTK("break"),
    CONTINUETK("continue"),
    IFTK("if"),
    ELSETK("else"),
    NOT("!"),
    AND("&&"),
    OR("||"),
    FORTK("for"),
    GETINTTK("getint"),
    GETCHARTK("getchar"),
    PRINTFTK("printf"),
    RETURNTK("return"),
    PLUS("+"),
    MINU("-"),
    VOIDTK("void"),
    MULT("*"),
    DIV("/"),
    MOD("%"),
    LSS("<"),
    LEQ("<="),
    GRE(">"),
    GEQ(">="),
    EQL("=="),
    NEQ("!="),
    ASSIGN("="),
    SEMICN(";"),
    COMMA(","),
    LPARENT("("),
    RPARENT(")"),
    LBRACK("["),
    RBRACK("]"),
    LBRACE("{"),
    RBRACE("}"),
    COMMENT,

    ;

    private final String lexeme;
    private static final Map<String, TokenType> TOKEN_MAP = new HashMap<>();

    static {
        for (TokenType tokenType : TokenType.values()) {
            if (!tokenType.lexeme.isEmpty()) { TOKEN_MAP.put(tokenType.lexeme, tokenType); }
        }
    }

    TokenType(final String lexeme) {
        this.lexeme = lexeme;
    }

    TokenType() {
        this.lexeme = "";
    }

    public static TokenType getTokenType(String lexeme) {
        return TOKEN_MAP.getOrDefault(lexeme, TokenType.IDENFR);
    }
}
