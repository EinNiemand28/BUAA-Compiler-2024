package frontend.lexer;

import enums.ErrorType;
import enums.TokenType;
import utils.Recorder;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final PushbackInputStream stream;
    private char curChar;
    private int lineno;
    private final List<Token> tokenList;

    public Lexer(PushbackInputStream stream) {
        this.stream = stream;
        this.lineno = 1;
        this.tokenList = new ArrayList<>();
    }

    public List<Token> getTokenList() throws IOException {
        if (!tokenList.isEmpty()) {
            return tokenList;
        }
        for (Token token = getToken(); token != null; token = getToken()) {
            if (token.type() != TokenType.COMMENT) { tokenList.add(token); }
        }
        return tokenList;
    }

    private Token getToken() throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean escape = false;

        skipBlank();
        if (isEOF()) { return null; }
        sb.append(curChar);

        if (curChar == '/') {
            if (skipComment()) { return new Token(TokenType.COMMENT, "", lineno); }
        } else if (isLetter() || isUnderscore()) {
            read();
            while (isLetter() || isUnderscore() || isDigit()) {
                sb.append(curChar);
                read();
            }
            unread();
        } else if (isDigit()) {
            read();
            while (isDigit()) {
                sb.append(curChar);
                read();
            }
            unread();
            return new Token(TokenType.INTCON, sb.toString(), lineno);
        } else if (curChar == '\'') {
            read();
            do {
                sb.append(curChar);
                if (curChar == '\\') { escape = !escape; }
                else { escape = false; }
                read();
            } while (curChar != '\'' || escape);
            sb.append(curChar);
            return new Token(TokenType.CHRCON, sb.toString(), lineno);
        } else if (curChar == '\"') {
            read();
            do {
                sb.append(curChar);
                if (curChar == '\\') { escape = !escape; }
                else { escape = false; }
                read();
            } while (curChar != '\"' || escape);
            sb.append(curChar);
            return new Token(TokenType.STRCON, sb.toString(), lineno);
        } else if (curChar == '&') {
            read();
            if (curChar != '&') {
                Recorder.addErrorMessage(ErrorType.illegalSymbol, lineno);
                unread();
            }
            sb.append('&');
        } else if (curChar == '|') {
            read();
            if (curChar != '|') {
                Recorder.addErrorMessage(ErrorType.illegalSymbol, lineno);
                unread();
            }
            sb.append('|');
        } else if (curChar == '<' || curChar == '>' || curChar == '=' || curChar == '!') {
            read();
            if (curChar == '=') { sb.append('='); }
            else { unread(); }
        }

        return new Token(TokenType.getTokenType(sb.toString()), sb.toString(), lineno);
    }

    private void read() throws IOException {
        curChar = (char) stream.read();
    }

    private void unread() throws IOException {
        stream.unread(curChar);
    }

    private boolean isBlank() {
        return Character.isWhitespace(curChar);
    }

    private boolean isLineFeed() throws IOException {
        if (curChar == '\r') { read(); }
        return curChar == '\n';
    }

    private boolean isLetter() {
        return Character.isLetter(curChar);
    }

    private boolean isDigit() {
        return Character.isDigit(curChar);
    }

    private boolean isUnderscore() {
        return curChar == '_';
    }

    private boolean isEOF() {
        return curChar == 65535;
    }

    private void skipBlank() throws IOException {
        if (!isEOF()) { read(); }
        while (isBlank()) {
            if (isLineFeed()) { lineno++; }
            read();
        }
    }

    private boolean skipComment() throws IOException {
        read();
        if (curChar == '/' || curChar == '*') {
            if (curChar == '/') {
                do { read(); } while (!isLineFeed() && !isEOF());
                lineno++;
            } else {
                read();
                do {
                    while (curChar != '*') {
                        read();
                        if (isLineFeed()) { lineno++; }
                    }
                    read();
                } while (curChar != '/');
            }
            return true;
        } else { unread(); }
        return false;
    }
}